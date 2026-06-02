package com.karuna.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.karuna.dto.AiAnalysisResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * GeminiService — despite the name, now works with ANY AI provider.
 * The active provider (Claude or Gemini) is injected via AiProviderConfig.
 *
 * To switch AI providers: change ai.provider in application.properties and restart.
 */
@Service
public class GeminiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);

    private final AiProvider ai;
    private final ObjectMapper mapper = new ObjectMapper();

    // Spring injects the @Primary AiProvider bean — whichever is active
    public GeminiService(AiProvider ai) {
        this.ai = ai;
    }

    // ─── 1. Analyze case from text ─────────────────────────────────────────
    public AiAnalysisResult analyzeCase(String species, String injuryType,
                                        String userDescription, String locationLabel) {
        String prompt = buildCasePrompt(species, injuryType, userDescription, locationLabel);
        String raw = ai.complete(prompt);
        return parseCaseAnalysis(raw);
    }

    // ─── 2. Analyze photo (vision) ─────────────────────────────────────────
    public AiAnalysisResult analyzePhoto(String base64ImageDataUrl) {
        String prompt = """
            You are an expert veterinary AI assistant for the Karuṇā animal rescue platform.
            Analyze this image of an animal and provide a structured JSON response:
            {
              "species": "dog/cat/cow/bird/other",
              "injuryType": "brief injury description",
              "severity": "critical/urgent/routine",
              "probableCondition": "detailed medical assessment",
              "firstAidSteps": ["step 1", "step 2", "step 3"],
              "estimatedCostInr": <integer>,
              "aiSummary": "1-2 sentence summary for rescuers",
              "confidence": "high/medium/low"
            }
            Respond ONLY with valid JSON, no markdown, no extra text.
            """;

        String imageBase64 = base64ImageDataUrl;
        String mimeType = "image/jpeg";
        if (base64ImageDataUrl != null && base64ImageDataUrl.contains(",")) {
            String header = base64ImageDataUrl.split(",")[0];
            imageBase64 = base64ImageDataUrl.split(",")[1];
            if (header.contains("png")) mimeType = "image/png";
            else if (header.contains("webp")) mimeType = "image/webp";
        }

        String raw = ai.completeWithImage(prompt, imageBase64, mimeType);
        return parseCaseAnalysis(raw);
    }

    // ─── 3. First Aid chatbot ──────────────────────────────────────────────
    public String getFirstAid(String species, String injuryDescription, String locationContext) {
        String prompt = String.format("""
            You are a veterinary first aid expert for the Karuṇā animal rescue app in India.
            A citizen found an injured animal and needs immediate first aid guidance.

            Animal: %s
            Situation: %s
            Location context: %s

            Respond ONLY with this JSON (no markdown):
            {
              "immediateSteps": ["step 1", "step 2", "step 3", "step 4"],
              "doNotDo": ["warning 1", "warning 2"],
              "whenToCallVet": "urgency description",
              "estimatedWaitAdvice": "what to do while waiting"
            }
            """, species, injuryDescription, locationContext);

        return ai.complete(prompt);
    }

    // ─── 4. Case summary for NGO ───────────────────────────────────────────
    public String summarizeCase(Long caseId, String species, String injuryType,
                                 String severity, String status, String locationLabel,
                                 String probableCondition, String assignedResponder,
                                 String notes) {
        String prompt = String.format("""
            You are an AI assistant for the Karuṇā animal rescue NGO portal.
            Generate a concise professional case summary.

            Case #K%03d:
            - Animal: %s | Injury: %s | Severity: %s | Status: %s
            - Location: %s
            - Medical Assessment: %s
            - Assigned Responder: %s
            - Field Notes: %s

            Respond ONLY with this JSON (no markdown):
            {
              "headline": "one-line case title",
              "summary": "2-3 sentence professional summary",
              "urgencyNote": "most needed action right now",
              "progressNote": "assessment of case progress",
              "recommendedNextStep": "specific next action for NGO staff"
            }
            """,
            caseId, species, injuryType, severity, status, locationLabel,
            probableCondition,
            assignedResponder != null ? assignedResponder : "Unassigned",
            notes != null && !notes.equals("[]") ? notes : "No notes yet");

        return ai.complete(prompt);
    }

    // ─── Helpers ───────────────────────────────────────────────────────────
    private String buildCasePrompt(String species, String injuryType,
                                    String description, String location) {
        return String.format("""
            You are an expert veterinary AI for the Karuṇā animal rescue platform in India.
            A citizen has reported an injured animal. Analyze and respond with structured JSON.

            Reported:
            - Animal: %s
            - Injury type: %s
            - Description: %s
            - Location: %s

            Respond ONLY with valid JSON (no markdown, no extra text):
            {
              "probableCondition": "detailed medical assessment",
              "severity": "critical OR urgent OR routine",
              "injuryType": "refined injury classification",
              "firstAidSteps": ["rescuer step 1", "step 2", "step 3", "step 4"],
              "estimatedCostInr": <integer, India context>,
              "aiSummary": "1-2 sentence plain English summary for NGO dispatch",
              "confidence": "high OR medium OR low"
            }

            Severity guide:
            - critical: life-threatening, rescue needed < 1 hour
            - urgent: serious, rescue within a few hours
            - routine: stable stray/minor injury

            India cost estimates: basic 500-2000, surgery 5000-20000, critical 20000+
            """, species, injuryType, description, location);
    }

    public AiAnalysisResult parseCaseAnalysis(String raw) {
        AiAnalysisResult result = new AiAnalysisResult();
        // Safe defaults
        result.setSeverity("urgent");
        result.setProbableCondition("AI analysis pending – please assess manually.");
        result.setFirstAidSteps("[\"Keep animal calm.\",\"Do not move unnecessarily.\",\"Await rescue team.\"]");
        result.setEstimatedCostInr(2000);
        result.setAiSummary("Automated analysis unavailable. Manual review required.");
        result.setConfidence("low");

        if (raw == null || raw.isBlank()) return result;

        try {
            String json = raw.trim()
                    .replaceAll("(?s)```json\\s*", "")
                    .replaceAll("```\\s*", "")
                    .trim();

            JsonNode node = mapper.readTree(json);
            if (node.has("probableCondition")) result.setProbableCondition(node.get("probableCondition").asText());
            if (node.has("severity"))          result.setSeverity(node.get("severity").asText().toLowerCase());
            if (node.has("injuryType"))        result.setInjuryType(node.get("injuryType").asText());
            if (node.has("estimatedCostInr"))  result.setEstimatedCostInr(node.get("estimatedCostInr").asInt());
            if (node.has("aiSummary"))         result.setAiSummary(node.get("aiSummary").asText());
            if (node.has("confidence"))        result.setConfidence(node.get("confidence").asText());
            if (node.has("firstAidSteps") && node.get("firstAidSteps").isArray())
                result.setFirstAidSteps(mapper.writeValueAsString(node.get("firstAidSteps")));

            log.info("AI analysis complete via {} — severity={}, confidence={}",
                    ai.providerName(), result.getSeverity(), result.getConfidence());

        } catch (Exception e) {
            log.warn("Could not parse AI JSON output: {}", e.getMessage());
        }
        return result;
    }
}
