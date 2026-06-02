package com.karuna.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.karuna.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.karuna.entity.*;
import com.karuna.entity.Case.CaseStatus;
import com.karuna.entity.Case.Severity;
import com.karuna.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CaseService {

    private static final Logger log = LoggerFactory.getLogger(CaseService.class);

    private final CaseRepository caseRepo;
    private final DonationRepository donationRepo;
    private final AdoptionApplicationRepository adoptionRepo;
    private final UserRepository userRepo;
    private final GeminiService geminiService;
    private final ObjectMapper mapper = new ObjectMapper();

    public CaseService(CaseRepository caseRepo, DonationRepository donationRepo,
                       AdoptionApplicationRepository adoptionRepo, UserRepository userRepo,
                       GeminiService geminiService) {
        this.caseRepo = caseRepo;
        this.donationRepo = donationRepo;
        this.adoptionRepo = adoptionRepo;
        this.userRepo = userRepo;
        this.geminiService = geminiService;
    }

    public List<CaseResponse> getAllCases() {
        return caseRepo.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public List<CaseResponse> getOpenCases() {
        var excluded = List.of(CaseStatus.adopted, CaseStatus.released, CaseStatus.discharged);
        return caseRepo.findByStatusNotInOrderByCreatedAtDesc(excluded).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public List<CaseResponse> getCasesByReporter(Long userId) {
        return caseRepo.findByReporterIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public CaseResponse getCase(Long id) {
        Case c = caseRepo.findById(id).orElseThrow(() -> new RuntimeException("Case not found"));
        return toResponse(c);
    }

    @Transactional
    public CaseResponse createCase(CaseRequest req, Long reporterId) {
        User reporter = userRepo.findById(reporterId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ── Gemini AI: auto-analyze the case ──────────────────────────────
        String probableCondition = req.getProbableCondition();
        String firstAidSteps     = req.getFirstAidSteps();
        String injuryType        = req.getInjuryType();
        String severity          = req.getSeverity() != null ? req.getSeverity() : "urgent";
        Integer estimatedCost    = req.getEstimatedCostInr();
        String aiSummary         = null;

        try {
            // If a photo was provided, run vision analysis first
            AiAnalysisResult aiResult = null;
            if (req.getImageDataUrl() != null && !req.getImageDataUrl().isBlank()) {
                log.info("Running Gemini Vision analysis for new case (with photo)");
                aiResult = geminiService.analyzePhoto(req.getImageDataUrl());
            }
            // Always run text analysis (overrides vision if text is more specific)
            if (req.getSpecies() != null || req.getProbableCondition() != null) {
                log.info("Running Gemini text analysis for new case");
                AiAnalysisResult textResult = geminiService.analyzeCase(
                        req.getSpecies(),
                        req.getInjuryType(),
                        req.getProbableCondition(),
                        req.getLocationLabel()
                );
                // Text analysis wins if available
                if (textResult != null) aiResult = textResult;
            }

            if (aiResult != null) {
                if (aiResult.getProbableCondition() != null) probableCondition = aiResult.getProbableCondition();
                if (aiResult.getFirstAidSteps()     != null) firstAidSteps     = aiResult.getFirstAidSteps();
                if (aiResult.getInjuryType()        != null && injuryType == null) injuryType = aiResult.getInjuryType();
                if (aiResult.getSeverity()          != null) severity           = aiResult.getSeverity();
                if (aiResult.getEstimatedCostInr()  != null) estimatedCost      = aiResult.getEstimatedCostInr();
                aiSummary = aiResult.getAiSummary();
                log.info("Gemini AI analysis complete. Severity={}, Confidence={}", aiResult.getSeverity(), aiResult.getConfidence());
            }
        } catch (Exception e) {
            log.error("Gemini analysis failed (non-fatal): {}", e.getMessage());
            // Case creation continues even if AI fails
        }

        // Build severity enum safely
        Severity severityEnum;
        try {
            severityEnum = Severity.valueOf(severity != null ? severity : "urgent");
        } catch (IllegalArgumentException ex) {
            severityEnum = Severity.urgent;
        }

        Case c = Case.builder()
                .createdAt(LocalDateTime.now())
                .reporterName(reporter.getName())
                .reporterContact(reporter.getPhone())
                .imageDataUrl(req.getImageDataUrl())
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .locationLabel(req.getLocationLabel())
                .species(req.getSpecies())
                .injuryType(injuryType)
                .severity(severityEnum)
                .probableCondition(probableCondition)
                .firstAidSteps(firstAidSteps != null ? firstAidSteps : "[]")
                .estimatedCostInr(estimatedCost)
                .status(CaseStatus.reported)
                .notes(aiSummary != null ? "[\"AI: " + aiSummary.replace("\"", "'") + "\"]" : "[]")
                .reporter(reporter)
                .build();
        c = caseRepo.save(c);
        return toResponse(c);
    }

    @Transactional
    public CaseResponse assignCase(Long caseId, String responderName, String ngo, Long actorId) {
        Case c = caseRepo.findById(caseId).orElseThrow(() -> new RuntimeException("Case not found"));
        c.setStatus(CaseStatus.assigned);
        c.setAssignedResponder(responderName);
        c.setNgo(ngo);
        c = caseRepo.save(c);
        return toResponse(c);
    }

    @Transactional
    public CaseResponse advanceStatus(Long caseId, CaseStatus newStatus, String actor) {
        Case c = caseRepo.findById(caseId).orElseThrow(() -> new RuntimeException("Case not found"));
        c.setStatus(newStatus);
        c = caseRepo.save(c);
        return toResponse(c);
    }

    @Transactional
    public CaseResponse addNote(Long caseId, String note, String actor) {
        Case c = caseRepo.findById(caseId).orElseThrow(() -> new RuntimeException("Case not found"));
        try {
            List<String> notes = mapper.readValue(c.getNotes(), new TypeReference<List<String>>() {});
            notes.add(note);
            c.setNotes(mapper.writeValueAsString(notes));
        } catch (Exception e) {
            c.setNotes("[\"" + note + "\"]");
        }
        c = caseRepo.save(c);
        return toResponse(c);
    }

    @Transactional
    public CaseResponse updateCase(Long caseId, CaseRequest req) {
        Case c = caseRepo.findById(caseId).orElseThrow(() -> new RuntimeException("Case not found"));
        if (req.getSpecies() != null) c.setSpecies(req.getSpecies());
        if (req.getProbableCondition() != null) c.setProbableCondition(req.getProbableCondition());
        if (req.getSeverity() != null) c.setSeverity(Severity.valueOf(req.getSeverity()));
        if (req.getEstimatedCostInr() != null) c.setEstimatedCostInr(req.getEstimatedCostInr());
        if (req.getFirstAidSteps() != null) c.setFirstAidSteps(req.getFirstAidSteps());
        c = caseRepo.save(c);
        return toResponse(c);
    }

    private CaseResponse toResponse(Case c) {
        List<Donation> donations = donationRepo.findByCaseRefIdOrderByTsDesc(c.getId());
        List<AdoptionApplication> apps = adoptionRepo.findByCaseRefIdOrderByTsDesc(c.getId());

        List<String> firstAidSteps = parseJsonList(c.getFirstAidSteps());
        List<String> notes = parseJsonList(c.getNotes());

        return CaseResponse.builder()
                .id(c.getId())
                .createdAt(c.getCreatedAt())
                .reporterName(c.getReporterName())
                .reporterContact(c.getReporterContact())
                .imageDataUrl(c.getImageDataUrl())
                .latitude(c.getLatitude())
                .longitude(c.getLongitude())
                .locationLabel(c.getLocationLabel())
                .species(c.getSpecies())
                .injuryType(c.getInjuryType())
                .severity(c.getSeverity().name())
                .probableCondition(c.getProbableCondition())
                .firstAidSteps(firstAidSteps)
                .status(c.getStatus().name())
                .assignedResponder(c.getAssignedResponder())
                .ngo(c.getNgo())
                .estimatedCostInr(c.getEstimatedCostInr())
                .donations(donations.stream().map(d -> DonationResponse.builder()
                        .id(d.getId()).ts(d.getTs()).donorName(d.getDonorName())
                        .amountInr(d.getAmountInr()).message(d.getMessage()).build())
                        .collect(Collectors.toList()))
                .adoptionApplications(apps.stream().map(a -> AdoptionResponse.builder()
                        .id(a.getId()).ts(a.getTs()).applicantName(a.getApplicantName())
                        .contact(a.getContact()).reason(a.getReason()).status(a.getStatus().name()).build())
                        .collect(Collectors.toList()))
                .notes(notes)
                .events(buildEvents(c))
                .build();
    }

    private List<CaseEventResponse> buildEvents(Case c) {
        List<CaseEventResponse> events = new ArrayList<>();
        events.add(CaseEventResponse.builder()
                .ts(c.getCreatedAt()).type("created")
                .actor(c.getReporterName())
                .details("Case opened (" + c.getSeverity() + ")").build());
        if (c.getAssignedResponder() != null) {
            events.add(CaseEventResponse.builder()
                    .ts(c.getCreatedAt().plusMinutes(5)).type("assigned")
                    .actor(c.getNgo()).details("Dispatched to " + c.getAssignedResponder()).build());
        }
        return events;
    }

    private List<String> parseJsonList(String json) {
        if (json == null || json.isBlank() || json.equals("[]")) return new ArrayList<>();
        try {
            return mapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
