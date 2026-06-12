package com.karuna.config;

import com.karuna.service.AiProvider;
import com.karuna.service.ClaudeProvider;
import com.karuna.service.GeminiProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Wires up the active AI provider based on application.properties:
 *
 *   ai.provider=claude   → uses ClaudeProvider
 *   ai.provider=gemini   → uses GeminiProvider
 *
 * To switch: change the ONE line above and restart the backend.
 * No code changes needed.
 */
@Configuration
public class AiProviderConfig {

    private static final Logger log = LoggerFactory.getLogger(AiProviderConfig.class);

    @Value("${ai.provider:claude}")
    private String provider;

    @Bean
    @Primary
    public AiProvider activeAiProvider(ClaudeProvider claude, GeminiProvider gemini) {
        AiProvider active = switch (provider.toLowerCase().trim()) {
            case "claude", "anthropic" -> claude;
            case "gemini", "google"    -> gemini;
            default -> {
                log.warn("Unknown ai.provider '{}', falling back to Claude", provider);
                yield claude;
            }
        };
        log.info("╔══════════════════════════════════════╗");
        log.info("║  AI Provider: {}  ║", active.providerName());
        log.info("╚══════════════════════════════════════╝");
        return active;
    }
}
