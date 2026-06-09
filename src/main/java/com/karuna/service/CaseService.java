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
        String detectedSpecies   = req.getSpecies();

        try {
            // If a photo was provided, run vision analysis first
            AiAnalysisResult aiResult = null;
            if (req.getImageDataUrl() != null && !req.getImageDataUrl().isBlank()) {
                log.info("Running Gemini Vision analysis for new case (with photo)");
                aiResult = geminiService.analyzePhoto(req.getImageDataUrl());
                if (aiResult != null && aiResult.getSpecies() != null) {
                    if (detectedSpecies == null || detectedSpecies.trim().isEmpty() || 
                        detectedSpecies.equalsIgnoreCase("other") || detectedSpecies.equalsIgnoreCase("unknown")) {
                        detectedSpecies = aiResult.getSpecies();
                    }
                }
            }
            // Always run text analysis (overrides vision if text is more specific)
            if (detectedSpecies != null || req.getProbableCondition() != null) {
                log.info("Running Gemini text analysis for new case using species: {}", detectedSpecies);
                AiAnalysisResult textResult = geminiService.analyzeCase(
                        detectedSpecies,
                        req.getInjuryType(),
                        req.getProbableCondition(),
                        req.getLocationLabel()
                );
                // Text analysis wins if available, but preserve the visual-detected species!
                if (textResult != null) {
                    if (textResult.getSpecies() == null) {
                        textResult.setSpecies(detectedSpecies);
                    }
                    aiResult = textResult;
                }
            }

            if (aiResult != null) {
                if (aiResult.getSpecies()           != null) detectedSpecies   = aiResult.getSpecies();
                if (aiResult.getProbableCondition() != null) probableCondition = aiResult.getProbableCondition();
                if (aiResult.getFirstAidSteps()     != null) firstAidSteps     = aiResult.getFirstAidSteps();
                if (aiResult.getInjuryType()        != null && injuryType == null) injuryType = aiResult.getInjuryType();
                if (aiResult.getSeverity()          != null) severity           = aiResult.getSeverity();
                if (aiResult.getEstimatedCostInr()  != null) estimatedCost      = aiResult.getEstimatedCostInr();
                aiSummary = aiResult.getAiSummary();
                log.info("Gemini AI analysis complete. Species={}, Severity={}, Confidence={}", detectedSpecies, aiResult.getSeverity(), aiResult.getConfidence());
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
                .species(detectedSpecies != null && !detectedSpecies.isBlank() ? detectedSpecies : "other")
                .injuryType(injuryType)
                .severity(severityEnum)
                .probableCondition(probableCondition)
                .firstAidSteps(firstAidSteps != null ? firstAidSteps : "[]")
                .estimatedCostInr(estimatedCost)
                .status(CaseStatus.reported)
                .notes(aiSummary != null ? "[\"AI: " + aiSummary.replace("\"", "'") + "\"]" : "[]")
                .reporter(reporter)
                .build();
        // Automated Volunteer Matcher (Haversine Distance-based Auto-dispatch)
        if (c.getLatitude() != null && c.getLongitude() != null) {
            java.util.List<User> volunteers = userRepo.findByRoleAndAvailable(User.Role.VOLUNTEER, true);
            User closestVolunteer = null;
            double minDistance = Double.MAX_VALUE;
            for (User v : volunteers) {
                if (v.getLatitude() != null && v.getLongitude() != null) {
                    double dist = calculateDistance(c.getLatitude(), c.getLongitude(), v.getLatitude(), v.getLongitude());
                    if (dist < minDistance) {
                        minDistance = dist;
                        closestVolunteer = v;
                    }
                }
            }
            // If closest volunteer is within 15 km, assign them auto-dispatched
            if (closestVolunteer != null && minDistance < 15.0) {
                c.setStatus(CaseStatus.assigned);
                c.setAssignedResponder(closestVolunteer.getName());
                closestVolunteer.setAvailable(false);
                userRepo.save(closestVolunteer);
                log.info("Auto-assigned case to closest volunteer: {} (distance: {} km)", closestVolunteer.getName(), minDistance);
            }
        }

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

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }
}
