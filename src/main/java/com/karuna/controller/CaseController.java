package com.karuna.controller;

import com.karuna.dto.CaseRequest;
import com.karuna.dto.CaseResponse;
import com.karuna.entity.Case.CaseStatus;
import com.karuna.entity.User;
import com.karuna.repository.UserRepository;
import com.karuna.service.CaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cases")
public class CaseController {

    private final CaseService caseService;
    private final UserRepository userRepo;

    public CaseController(CaseService caseService, UserRepository userRepo) {
        this.caseService = caseService;
        this.userRepo = userRepo;
    }

    @GetMapping
    public ResponseEntity<List<CaseResponse>> getAllCases() {
        return ResponseEntity.ok(caseService.getAllCases());
    }

    @GetMapping("/open")
    public ResponseEntity<List<CaseResponse>> getOpenCases() {
        return ResponseEntity.ok(caseService.getOpenCases());
    }

    @GetMapping("/my")
    public ResponseEntity<List<CaseResponse>> getMyCases(Authentication auth) {
        Long userId = (Long) auth.getDetails();
        return ResponseEntity.ok(caseService.getCasesByReporter(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CaseResponse> getCase(@PathVariable Long id) {
        return ResponseEntity.ok(caseService.getCase(id));
    }

    @PostMapping
    public ResponseEntity<?> createCase(@RequestBody CaseRequest req, Authentication auth) {
        try {
            Long userId = (Long) auth.getDetails();
            return ResponseEntity.ok(caseService.createCase(req, userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCase(@PathVariable Long id, @RequestBody CaseRequest req) {
        try {
            return ResponseEntity.ok(caseService.updateCase(id, req));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<?> assignCase(@PathVariable Long id, @RequestBody Map<String, Object> body,
                                         Authentication auth) {
        try {
            Long actorId = (Long) auth.getDetails();

            // Support both responderId (number) and responderName (string)
            Object responderIdObj = body.get("responderId");
            String responderName;
            String ngo = null;

            if (responderIdObj instanceof Number) {
                Long responderId = ((Number) responderIdObj).longValue();
                User responder = userRepo.findById(responderId)
                        .orElseThrow(() -> new RuntimeException("Responder not found"));
                responderName = responder.getName();
                ngo = responder.getNgoName();
            } else {
                responderName = (String) body.get("responderName");
                ngo = (String) body.get("ngo");
            }

            return ResponseEntity.ok(caseService.assignCase(id, responderName, ngo, actorId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{id}/advance")
    public ResponseEntity<?> advanceStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            CaseStatus status = CaseStatus.valueOf(body.get("event"));
            String actor = body.getOrDefault("actor", "NGO Staff");
            return ResponseEntity.ok(caseService.advanceStatus(id, status, actor));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{id}/notes")
    public ResponseEntity<?> addNote(@PathVariable Long id, @RequestBody Map<String, String> body,
                                      Authentication auth) {
        try {
            String actor = auth.getName();
            return ResponseEntity.ok(caseService.addNote(id, body.get("text"), actor));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
