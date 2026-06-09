package com.karuna.service;

import com.karuna.dto.AdoptionResponse;
import com.karuna.entity.AdoptionApplication;
import com.karuna.entity.AdoptionApplication.AppStatus;
import com.karuna.entity.Case.CaseStatus;
import com.karuna.repository.AdoptionApplicationRepository;
import com.karuna.repository.CaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdoptionService {

    private final AdoptionApplicationRepository adoptionRepo;
    private final CaseRepository caseRepo;

    public AdoptionService(AdoptionApplicationRepository adoptionRepo, CaseRepository caseRepo) {
        this.adoptionRepo = adoptionRepo;
        this.caseRepo = caseRepo;
    }

    public List<AdoptionResponse> getAllApplications() {
        return adoptionRepo.findAllByOrderByTsDesc().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public List<AdoptionResponse> getApplicationsForCase(Long caseId) {
        return adoptionRepo.findByCaseRefIdOrderByTsDesc(caseId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public AdoptionResponse apply(Long caseId, String applicantName, String contact, String reason, String adopterIdUrl) {
        var c = caseRepo.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found"));
        AdoptionApplication app = AdoptionApplication.builder()
                .ts(LocalDateTime.now())
                .applicantName(applicantName)
                .contact(contact)
                .reason(reason)
                .status(AppStatus.pending)
                .adopterIdUrl(adopterIdUrl)
                .checkinsLogs("[]")
                .caseRef(c)
                .build();
        app = adoptionRepo.save(app);
        return toResponse(app);
    }

    @Transactional
    public AdoptionResponse decide(Long appId, AppStatus decision) {
        AdoptionApplication app = adoptionRepo.findById(appId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        app.setStatus(decision);
        app = adoptionRepo.save(app);
        if (decision == AppStatus.approved) {
            var c = app.getCaseRef();
            c.setStatus(CaseStatus.adopted);
            caseRepo.save(c);
        }
        return toResponse(app);
    }

    @Transactional
    public AdoptionResponse addCheckin(Long appId, String text, String photoUrl) {
        AdoptionApplication app = adoptionRepo.findById(appId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        
        String cleanText = text == null ? "" : text.replace("\"", "\\\"");
        String cleanPhoto = photoUrl == null ? "" : photoUrl.replace("\"", "\\\"");
        String logEntry = String.format("{\"date\":\"%s\",\"text\":\"%s\",\"photoUrl\":\"%s\"}", 
                LocalDateTime.now().toString(), cleanText, cleanPhoto);
        
        String currentLogs = app.getCheckinsLogs();
        if (currentLogs == null || currentLogs.isBlank() || currentLogs.equals("[]")) {
            app.setCheckinsLogs("[" + logEntry + "]");
        } else {
            app.setCheckinsLogs(currentLogs.substring(0, currentLogs.length() - 1) + "," + logEntry + "]");
        }
        app = adoptionRepo.save(app);
        return toResponse(app);
    }

    private AdoptionResponse toResponse(AdoptionApplication a) {
        return AdoptionResponse.builder()
                .id(a.getId()).ts(a.getTs())
                .applicantName(a.getApplicantName()).contact(a.getContact())
                .reason(a.getReason()).status(a.getStatus().name())
                .adopterIdUrl(a.getAdopterIdUrl())
                .checkinsLogs(a.getCheckinsLogs())
                .build();
    }
}
