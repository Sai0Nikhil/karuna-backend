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
 * Claude (Anthropic) AI provider.
 * Docs: https://docs.anthropic.com/en/api/messages
 *
 * Active when: ai.provider=claude in application.properties
 */
@Component
public class ClaudeProvider implements AiProvider {

    private static final Logger log = LoggerFactory.getLogger(ClaudeProvider.class);

    @Value("${claude.api.key:}")
    private String apiKey;

    @Value("${claude.model:claude-3-5-haiku-20241022}")
    private String model;

    @Value("${claude.base-url:https://api.anthropic.com/v1/messages}")
    private String baseUrl;

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String complete(String prompt) {
        return callClaude(prompt, null, null);
    }

    @Override
    public String completeWithImage(String prompt, String base64Image, String mimeType) {
        return callClaude(prompt, base64Image, mimeType);
    }

    @Override
    public String providerName() {
        return "Claude (" + model + ")";
    }

    private String callClaude(String prompt, String base64Image, String mimeType) {
        try {
            // Build content array
            List<Map<String, Object>> content = new ArrayList<>();

            // Add image if provided (Claude vision)
            if (base64Image != null && !base64Image.isBlank()) {
                content.add(Map.of(
                    "type", "image",
                    "source", Map.of(
                        "type", "base64",
                        "media_type", mimeType != null ? mimeType : "image/jpeg",
                        "data", base64Image
                    )
                ));
            }

            // Add text prompt
            content.add(Map.of("type", "text", "text", prompt));

            // Build request body
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("model", model);
            body.put("max_tokens", 1024);
            body.put("messages", List.of(
                Map.of("role", "user", "content", content)
            ));

            String requestJson = mapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("Content-Type", "application/json")
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                    .timeout(Duration.ofSeconds(30))
                    .build();

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.error("Claude API error {}: {}", response.statusCode(), response.body());
                return null;
            }

            // Parse: response.content[0].text
            JsonNode root = mapper.readTree(response.body());
            JsonNode text = root.path("content").get(0).path("text");
            if (text.isMissingNode()) {
                log.error("Unexpected Claude response: {}", response.body());
                return null;
            }
            return text.asText();

        } catch (Exception e) {
            log.error("Claude API call failed: {}", e.getMessage());
            return null;
        }
    }
}
