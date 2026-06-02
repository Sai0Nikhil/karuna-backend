package com.karuna.controller;

import com.karuna.dto.AdoptionResponse;
import com.karuna.entity.AdoptionApplication.AppStatus;
import com.karuna.service.AdoptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/adoptions")
public class AdoptionController {

    private final AdoptionService adoptionService;

    public AdoptionController(AdoptionService adoptionService) {
        this.adoptionService = adoptionService;
    }

    @GetMapping
    public ResponseEntity<List<AdoptionResponse>> getAll() {
        return ResponseEntity.ok(adoptionService.getAllApplications());
    }

    @GetMapping("/case/{caseId}")
    public ResponseEntity<List<AdoptionResponse>> getForCase(@PathVariable Long caseId) {
        return ResponseEntity.ok(adoptionService.getApplicationsForCase(caseId));
    }

    @PostMapping("/case/{caseId}/apply")
    public ResponseEntity<?> apply(@PathVariable Long caseId, @RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(adoptionService.apply(caseId,
                    body.get("applicantName"), body.get("contact"), body.get("reason")));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{appId}")
    public ResponseEntity<?> decide(@PathVariable Long appId, @RequestBody Map<String, String> body) {
        try {
            AppStatus status = AppStatus.valueOf(body.get("status"));
            return ResponseEntity.ok(adoptionService.decide(appId, status));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
