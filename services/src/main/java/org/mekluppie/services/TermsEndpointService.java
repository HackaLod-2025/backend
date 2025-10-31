package org.mekluppie.services;

import org.mekluppie.services.model.TermsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TermsEndpointService {
    private static final Logger logger = LoggerFactory.getLogger(TermsEndpointService.class);

    public List<String> fetchTerms(List<String> sources, String query, List<String> languages) throws Exception {
        logger.info("Fetching terms with sources: {}, query: {}, languages: {}", sources, query, languages);

        ClassPathResource resource = new ClassPathResource("TermQuery.graphql");
        String graphqlQuery = Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);

        Map<String, Object> variables = new HashMap<>();
        variables.put("sources", sources);
        variables.put("query", query);
        variables.put("languages", languages);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", graphqlQuery);
        requestBody.put("variables", variables);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<TermsResponse> response = restTemplate.postForEntity(
                "https://termennetwerk-api.netwerkdigitaalerfgoed.nl/graphql",
                entity,
                TermsResponse.class
        );

        var termsResponse = response.getBody();
        if (termsResponse != null && termsResponse.data() != null) {
            var foundTerms = termsResponse.data().terms().stream()
                    .flatMap(termSource -> termSource.result().terms().stream()
                            .map(term -> String.format("URI: %s%nPreferred Label: %s%nScope Note: %s%nSource: %s",
                                    term.uri(),
                                    term.prefLabel().stream()
                                            .map(TermsResponse.Label::value)
                                            .findFirst()
                                            .orElse("N/A"),
                                    term.scopeNote().stream()
                                            .map(TermsResponse.Label::value)
                                            .findFirst()
                                            .orElse("N/A"),
                                    termSource.source().name())))
                    .toList();
            logger.info("Fetched {} terms", foundTerms.size());
            return foundTerms;
        } else {
            logger.warn("No terms found in the response");
            return List.of();
        }
    }
}
