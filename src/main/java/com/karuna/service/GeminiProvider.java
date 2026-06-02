package com.karuna.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

/**
 * Gemini (Google) AI provider.
 * Docs: https://ai.google.dev/api/generate-content
 *
 * Active when: ai.provider=gemini in application.properties
 */
@Component
public class GeminiProvider implements AiProvider {

    private static final Logger log = LoggerFactory.getLogger(GeminiProvider.class);

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.model:gemini-1.5-flash}")
    private String model;

    @Value("${gemini.base-url:https://generativelanguage.googleapis.com/v1beta/models}")
    private String baseUrl;

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String complete(String prompt) {
        return callGemini(prompt, null, null);
    }

    @Override
    public String completeWithImage(String prompt, String base64Image, String mimeType) {
        return callGemini(prompt, base64Image, mimeType);
    }

    @Override
    public String providerName() {
        return "Gemini (" + model + ")";
    }

    private String callGemini(String prompt, String base64Image, String mimeType) {
        try {
            String url = baseUrl + "/" + model + ":generateContent?key=" + apiKey;

            List<Map<String, Object>> parts = new ArrayList<>();

            // Add image if provided
            if (base64Image != null && !base64Image.isBlank()) {
                Map<String, Object> inlineData = new LinkedHashMap<>();
                inlineData.put("mimeType", mimeType != null ? mimeType : "image/jpeg");
                inlineData.put("data", base64Image);
                parts.add(Map.of("inlineData", inlineData));
            }

            // Add text prompt
            parts.add(Map.of("text", prompt));

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("contents", List.of(Map.of("parts", parts, "role", "user")));
            body.put("generationConfig", Map.of(
                "temperature", 0.2,
                "topK", 1,
                "topP", 0.95,
                "maxOutputTokens", 1024
            ));

            String requestJson = mapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                    .timeout(Duration.ofSeconds(30))
                    .build();

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse: candidates[0].content.parts[0].text
            JsonNode root = mapper.readTree(response.body());
            JsonNode text = root.path("candidates").get(0)
                    .path("content").path("parts").get(0).path("text");
            if (text.isMissingNode()) {
                log.error("Unexpected Gemini response: {}", response.body());
                return null;
            }
            return text.asText();

        } catch (Exception e) {
            log.error("Gemini API call failed: {}", e.getMessage());
            return null;
        }
    }
}
