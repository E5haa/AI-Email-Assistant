package com.SaaS.AI.Email.Assistant.Service;

import com.SaaS.AI.Email.Assistant.dto.MessageRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;


@Service
public class AIService {

    private final WebClient webClient;

    @Value("${ai.google.genai.api-key}")
    private String apiKey;

    @Value("${ai.google.genai.api.url}")
    private String apiUrl;


    public AIService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();

    }


    public String sendMessage(MessageRequest messageRequest) {
        String prompt = buildPrompt(messageRequest);
        String requestBody = String.format("""
                {
                  "contents": [
                    {
                      "parts": [
                        {
                          "text": "%s"
                        }
                      ]
                    }
                  ]
                }""", prompt);

        String response = webClient.post()
                .uri(apiUrl + "?key=" + apiKey)
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.valueOf("application/json"))
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return extractResponseContent(response);
    }


    private String extractResponseContent(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            JsonNode candidates = root.path("candidates");
            if (!candidates.isArray() || candidates.size() == 0) {
                throw new RuntimeException("No candidates found in AI response");
            }

            JsonNode firstCandidate = candidates.get(0);
            JsonNode contentNode = firstCandidate.path("content");
            if (contentNode.isMissingNode()) {
                throw new RuntimeException("Content node missing in AI response");
            }

            JsonNode parts = contentNode.path("parts");
            if (!parts.isArray() || parts.size() == 0) {
                throw new RuntimeException("No parts found in AI response");
            }

            return parts.get(0).path("text").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response: " + e.getMessage(), e);
        }
    }


    private String buildPrompt(MessageRequest messageRequest) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a professional email reply for this message:");
        if(messageRequest.getTone() != null && !messageRequest.getTone().isEmpty()) {
            prompt.append("Use a ").append(messageRequest.getTone()).append(" tone");
        }
        prompt.append("Original Email: \n").append(messageRequest.getContent());
        return prompt.toString();
    }
}