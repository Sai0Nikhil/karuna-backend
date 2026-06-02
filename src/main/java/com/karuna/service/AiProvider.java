package com.karuna.service;

/**
 * Common interface for all AI providers (Claude, Gemini, etc.)
 * Swap providers by changing ai.provider in application.properties
 */
public interface AiProvider {

    /**
     * Send a prompt and get a text response.
     * @param prompt  The full prompt text
     * @return        Raw text response from the AI model
     */
    String complete(String prompt);

    /**
     * Send a prompt with a base64 image and get a text response.
     * @param prompt       The text prompt
     * @param base64Image  Base64-encoded image data (without data URL prefix)
     * @param mimeType     e.g. "image/jpeg", "image/png"
     * @return             Raw text response
     */
    String completeWithImage(String prompt, String base64Image, String mimeType);

    /**
     * @return Human-readable name for logging, e.g. "Claude 3.5 Haiku"
     */
    String providerName();
}
