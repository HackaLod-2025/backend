package org.mekluppie.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mekluppie.services.model.AuthorQueryResponse;
import org.mekluppie.services.model.ImageQueryResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SparqlEndpointService {

    private final RestTemplate restTemplate;

    public SparqlEndpointService() {
        this.restTemplate = new RestTemplate();
    }

    public List<ImageQueryResponse> executeImageQuery(String imageName) {
        String endpoint = "https://lod.uba.uva.nl/_api/datasets/UB-UVA/Beeldbank/services/virtuoso/sparql";
        String query = String.format("prefix dcmi: <http://purl.org/dc/dcmitype/>\nprefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\nprefix edm: <http://www.europeana.eu/schemas/edm/>\nselect * {\n  ?image a dcmi:Image .\n  ?image rdfs:label ?widgetLabel; edm:isShownBy ?widgetImage .\n  # filter(?image = <https://hdl.handle.net/11245/3.19290>)\n  filter(CONTAINS(LCASE(?widgetLabel), LCASE(\"%s\")))\n} LIMIT 30", imageName);

        var response = executeQuery(query, endpoint);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            List<ImageQueryResponse> results = new ArrayList<>();
            for (JsonNode binding : root) {
                String image = binding.path("image").asText();
                String widgetLabel = binding.path("widgetLabel").asText();
                String widgetImage = binding.path("widgetImage").asText();
                results.add(new ImageQueryResponse(image, widgetLabel, widgetImage));
            }
            return results.subList(0, Math.min(results.size(), 30));
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse SPARQL response", e);
        }

    }

    public List<AuthorQueryResponse> executeAuthorQuery(String authorName) {
        String endpoint = "https://data.bibliotheken.nl/_api/datasets/KB/Production/services/Production-VTS/sparql";
        String query = String.format("prefix schema: <http://schema.org/>\nprefix rdfs: <http://www.w3" +
                ".org/2000/01/rdf-schema#>\nselect ?boek (sample(?titel) as ?t) where {\n  ?auteur schema:familyName " +
                "?familyName .\n  ?boek schema:author ?auteur; schema:name ?titel; rdfs:label ?boekLabel .\n  filter" +
                "(?familyName = \"Mulisch\")\n} group by ?boek", authorName);

        var response = executeQuery(query, endpoint);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            List<AuthorQueryResponse> results = new ArrayList<>();
            for (JsonNode binding : root) {
                String boek = binding.path("boek").asText();
                String titel = binding.path("t").asText();
                results.add(new AuthorQueryResponse(boek, titel));
            }
            return results;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse SPARQL response", e);
        }
    }

    public String executeQuery(String queryStr, String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = Map.of("query", queryStr);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        return restTemplate.postForObject(endpoint, request, String.class);
    }

}
