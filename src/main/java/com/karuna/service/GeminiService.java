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
    public AiAnalysisResult analyzePhoto(String base64ImageDataUrl, String lat, String lon, String description) {
        String locationInfo = (lat != null && !lat.isBlank() && lon != null && !lon.isBlank())
            ? "User is at latitude " + lat + ", longitude " + lon + " (India)."
            : "User location not provided.";
        String descPart = (description != null && !description.isBlank())
            ? "Additional context from user: \"" + description + "\""
            : "";

        String prompt = """
            You are Karuṇā, a compassionate veterinary AI assistant for animal rescue in India.
            Analyze this image and provide a structured JSON response.

            CRITICAL SPECIES CLASSIFICATION RULES:
            - You must accurately classify the animal in the image. The allowed values for the "species" field are exactly: "dog", "cat", "cow", "bird", or "other" (all lowercase).
            - DO NOT confuse cattle (cows, bulls, calves, buffaloes) or goats with dogs. Street cattle of various sizes and breeds are very common in India.
            - Look for key identifying physical features of cattle/cows: hooves, horns, hump on the back (common in Zebu cows/bulls), droopy ears, a large bovine snout/muzzle, and distinct body shape/proportions. If the animal is a bovine of any size (even a small calf or a large bull), classify it as "cow".
            - If it is a canine (dog, puppy, street mongrel), classify it as "dog".
            - If it is a feline, classify it as "cat".
            - If it is a bird, classify it as "bird".
            - If it is any other animal (e.g. goat, sheep, monkey, donkey, pig, buffalo, squirrel, snake, rabbit), classify it as "other".
            """ + locationInfo + "\n" + descPart + """

            VETERINARY CONTACTS DATABASE (pick 2-3 nearest based on location):
            [
              {"name":"NTR Veterinary Super Specialty Hospital","address":"Bunder Road, Labbipet, Vijayawada, AP 520010","phone":"N/A"},
              {"name":"Prathyusha Pet Clinic","address":"Labbipet, Vijayawada, AP 520010","phone":"N/A"},
              {"name":"Bluewings Pet Clinic","address":"Veterinary Colony, Vijayawada, AP 520008","phone":"N/A"},
              {"name":"K-Petz Hospital (24-hour)","address":"Gunadala Poranki, Vijayawada, AP","phone":"N/A"},
              {"name":"Animal Warriors Conservation Society","address":"Hyderabad, Telangana","phone":"Via Facebook"},
              {"name":"AASRA Pets","address":"Bowrampet, Hyderabad, Telangana","phone":"weaasra.org"},
              {"name":"PETA India (Delhi Office)","address":"Delhi, NCR","phone":"petaindia.com"},
              {"name":"Delhi Govt. Veterinary Hospital, Rohini","address":"Rohini, Delhi","phone":"Contact MCD"},
              {"name":"Delhi Govt. Veterinary Hospital, Dwarka","address":"Dwarka, Delhi","phone":"Contact MCD"},
              {"name":"Pet Care Clinic, Visakhapatnam","address":"Murali Nagar, Visakhapatnam, AP 530007","phone":"N/A"}
            ]

            Return ONLY valid JSON (no markdown):
            {
              "species": "exactly one of: dog, cat, cow, bird, other",
              "injuryType": "brief injury description",
              "severity": "critical/urgent/routine",
              "probableCondition": "detailed medical assessment",
              "firstAidSteps": ["step 1", "step 2", "step 3", "step 4"],
              "medicines": [
                {"name": "MedicineName", "dosage": "dose/kg", "route": "Oral/IV/Topical", "frequency": "Every X hours", "notes": "practical usage tip for citizen"}
              ],
              "localSupport": [
                {"name": "Nearest Clinic Name", "address": "Full address", "phone": "number or N/A"}
              ],
              "disclaimer": "This is emergency first aid only. Severe wounds require immediate professional veterinary care.",
              "estimatedCostInr": <integer>,
              "aiSummary": "1-2 sentence summary for rescuers",
              "confidence": "high/medium/low"
            }
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

    // backward-compat overload
    public AiAnalysisResult analyzePhoto(String base64ImageDataUrl) {
        return analyzePhoto(base64ImageDataUrl, "", "", "");
    }

    // ─── 3. First Aid chatbot ──────────────────────────────────────────────
    public String getFirstAid(String species, String injuryDescription, String locationContext, String language) {
        if (language == null || language.isBlank()) {
            language = "English";
        }
        String prompt = String.format("""
            You are a veterinary first aid expert for the Karuṇā animal rescue app in India.
            A citizen found an injured animal and needs immediate first aid guidance.

            Animal: %s
            Situation: %s
            Location context: %s
            Requested Language: %s

            Respond ONLY with this JSON structure (no markdown, no additional comments).
            All text fields and messages inside the JSON values must be translated and written in the requested language (%s). Keep any critical veterinary terminology or specific medicine names recognizable (or in English if appropriate), but translate all descriptions, steps, and advices fully:
            {
              "immediateSteps": ["translated step 1", "translated step 2", "translated step 3", "translated step 4"],
              "doNotDo": ["translated warning 1", "translated warning 2"],
              "whenToCallVet": "translated urgency description",
              "estimatedWaitAdvice": "translated what to do while waiting"
            }
            """, species, injuryDescription, locationContext, language, language);

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
              "medicines": [
                {"name": "MedicineName", "dosage": "dose/kg", "route": "Oral/IV/Topical", "frequency": "Every X hours", "notes": "brief usage note"}
              ],
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
            if (node.has("species"))           result.setSpecies(node.get("species").asText().toLowerCase());
            if (node.has("probableCondition")) result.setProbableCondition(node.get("probableCondition").asText());
            if (node.has("severity"))          result.setSeverity(node.get("severity").asText().toLowerCase());
            if (node.has("injuryType"))        result.setInjuryType(node.get("injuryType").asText());
            if (node.has("estimatedCostInr"))  result.setEstimatedCostInr(node.get("estimatedCostInr").asInt());
            if (node.has("aiSummary"))         result.setAiSummary(node.get("aiSummary").asText());
            if (node.has("confidence"))        result.setConfidence(node.get("confidence").asText());
            if (node.has("firstAidSteps") && node.get("firstAidSteps").isArray())
                result.setFirstAidSteps(mapper.writeValueAsString(node.get("firstAidSteps")));
            if (node.has("medicines") && node.get("medicines").isArray())
                result.setMedicines(mapper.writeValueAsString(node.get("medicines")));
            if (node.has("localSupport") && node.get("localSupport").isArray())
                result.setLocalSupport(mapper.writeValueAsString(node.get("localSupport")));
            if (node.has("disclaimer"))
                result.setDisclaimer(node.get("disclaimer").asText());

            log.info("AI analysis complete via {} — severity={}, confidence={}",
                    ai.providerName(), result.getSeverity(), result.getConfidence());

        } catch (Exception e) {
            log.warn("Could not parse AI JSON output: {}", e.getMessage());
        }
        return result;
    }
}
