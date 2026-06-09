package com.karuna.controller;

import com.karuna.dto.AiAnalysisResult;
import com.karuna.entity.Case;
import com.karuna.repository.CaseRepository;
import com.karuna.service.GeminiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AI endpoints powered by Gemini 1.5 Flash
 *
 * POST /api/ai/analyze      – Analyze a case description (text)
 * POST /api/ai/analyze-photo – Analyze an uploaded photo (vision)
 * POST /api/ai/firstaid     – Get first aid steps for a situation
 * GET  /api/cases/:id/summary – Get AI-generated NGO case summary
 */
@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final GeminiService geminiService;
    private final CaseRepository caseRepo;

    public AiController(GeminiService geminiService, CaseRepository caseRepo) {
        this.geminiService = geminiService;
        this.caseRepo = caseRepo;
    }

    // ─────────────────────────────────────────────────────────────────────
    // 1. Analyze case from text description (called before form submit)
    //    Body: { species, injuryType, description, locationLabel }
    // ─────────────────────────────────────────────────────────────────────
    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeCase(@RequestBody Map<String, String> body) {
        try {
            AiAnalysisResult result = geminiService.analyzeCase(
                    body.getOrDefault("species", ""),
                    body.getOrDefault("injuryType", ""),
                    body.getOrDefault("description", ""),
                    body.getOrDefault("locationLabel", "")
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "AI analysis failed: " + e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // 2. Analyze a photo (Gemini Vision)
    //    Body: { imageDataUrl: "data:image/jpeg;base64,..." }
    // ─────────────────────────────────────────────────────────────────────
    @PostMapping("/analyze-photo")
    public ResponseEntity<?> analyzePhoto(@RequestBody Map<String, String> body) {
        String imageDataUrl = body.get("imageDataUrl");
        if (imageDataUrl == null || imageDataUrl.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "imageDataUrl is required"));
        }
        try {
            String lat = body.getOrDefault("lat", "");
            String lon = body.getOrDefault("lon", "");
            String description = body.getOrDefault("description", "");
            AiAnalysisResult result = geminiService.analyzePhoto(imageDataUrl, lat, lon, description);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Photo analysis failed: " + e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // 3. First Aid chatbot
    //    Body: { species, injuryDescription, locationContext }
    //    Returns raw JSON string from Gemini with immediateSteps, doNotDo, etc.
    // ─────────────────────────────────────────────────────────────────────
    @PostMapping("/firstaid")
    public ResponseEntity<?> getFirstAid(@RequestBody Map<String, String> body) {
        try {
            String rawJson = geminiService.getFirstAid(
                    body.getOrDefault("species", "animal"),
                    body.getOrDefault("injuryDescription", ""),
                    body.getOrDefault("locationContext", ""),
                    body.getOrDefault("language", "English")
            );
            // Return as parsed JSON (not a string)
            if (rawJson != null) {
                // Strip markdown if present
                String cleaned = rawJson.trim()
                        .replaceAll("^```json\\s*", "")
                        .replaceAll("^```\\s*", "")
                        .replaceAll("```\\s*$", "")
                        .trim();
                return ResponseEntity.ok()
                        .header("Content-Type", "application/json")
                        .body(cleaned);
            }
            return ResponseEntity.ok(Map.of(
                "immediateSteps", java.util.List.of(
                    "Keep the animal calm and away from traffic.",
                    "Do not attempt to move it if spinal injury is suspected.",
                    "Call your nearest animal rescue NGO immediately."
                ),
                "doNotDo", java.util.List.of("Do not give human food or medicine."),
                "whenToCallVet", "Call immediately if the animal is unconscious or bleeding heavily.",
                "estimatedWaitAdvice", "Keep the animal in a quiet, shaded spot."
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "First aid lookup failed: " + e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // 4. AI case summary for NGO (also accessible via GET /api/cases/:id/summary)
    // ─────────────────────────────────────────────────────────────────────
    @GetMapping("/summary/{caseId}")
    public ResponseEntity<?> getCaseSummary(@PathVariable Long caseId) {
        Case c = caseRepo.findById(caseId).orElse(null);
        if (c == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            String rawJson = geminiService.summarizeCase(
                    c.getId(),
                    c.getSpecies(),
                    c.getInjuryType(),
                    c.getSeverity() != null ? c.getSeverity().name() : "unknown",
                    c.getStatus() != null ? c.getStatus().name() : "reported",
                    c.getLocationLabel(),
                    c.getProbableCondition(),
                    c.getAssignedResponder(),
                    c.getNotes()
            );
            if (rawJson != null) {
                String cleaned = rawJson.trim()
                        .replaceAll("^```json\\s*", "")
                        .replaceAll("^```\\s*", "")
                        .replaceAll("```\\s*$", "")
                        .trim();
                return ResponseEntity.ok()
                        .header("Content-Type", "application/json")
                        .body(cleaned);
            }
            return ResponseEntity.ok(Map.of("summary", "AI summary unavailable. Please review manually."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Summary generation failed: " + e.getMessage()));
        }
    }
}
