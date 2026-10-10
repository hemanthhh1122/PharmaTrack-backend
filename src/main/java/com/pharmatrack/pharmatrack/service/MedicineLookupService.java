
package com.pharmatrack.pharmatrack.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MedicineLookupService {

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Map<String, String>> search(String query) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Medicine search query is required");
        }

        if (query.length() > 100) {
            throw new IllegalArgumentException("Search query is too long");
        }

        String url = UriComponentsBuilder
                .fromUriString("https://rxnav.nlm.nih.gov/REST/drugs.json")
                .queryParam("name", query.trim())
                .build()
                .encode()
                .toUriString();

        String response = restClient.get()
                .uri(url)
                .retrieve()
                .body(String.class);

        List<Map<String, String>> results = new ArrayList<>();

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode groups = root.path("drugGroup").path("conceptGroup");

            for (JsonNode group : groups) {
                JsonNode concepts = group.path("conceptProperties");

                for (JsonNode concept : concepts) {
                    results.add(Map.of(
                            "id", concept.path("rxcui").asText(""),
                            "name", concept.path("name").asText(""),
                            "type", concept.path("tty").asText("")
                    ));
                }
            }

            return results;

        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to read medicine information", e);
        }
    }
}
