package org.mbarek0.folioflex.service.ai.impl;

import org.mbarek0.folioflex.service.ai.GeminiTranslationService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.List;

@Service
public class GeminiTranslationServiceImpl implements GeminiTranslationService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public GeminiTranslationServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String translateText(String text, String targetLanguage) {
        String prompt = "Translate to " + targetLanguage + ": " + text;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        GeminiRequest request = new GeminiRequest(
                new Content(List.of(new Part(prompt)))
        );

        String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("key", apiKey)
                .toUriString();

        HttpEntity<GeminiRequest> entity = new HttpEntity<>(request, headers);

        GeminiResponse response = restTemplate.postForObject(
                url,
                entity,
                GeminiResponse.class
        );

        return response.candidates().get(0).content().parts().get(0).text();
    }

    // Keep the same record classes from previous example
    public record GeminiRequest(Content contents) {}
    public record Content(List<Part> parts) {}
    public record Part(String text) {}
    public record GeminiResponse(List<Candidate> candidates) {}
    public record Candidate(Content content) {}
}