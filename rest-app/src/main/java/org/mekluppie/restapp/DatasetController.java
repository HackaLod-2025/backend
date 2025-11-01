package org.mekluppie.restapp;

import org.mekluppie.restapp.model.RecommendDatasetRequest;
import org.mekluppie.restapp.model.SparqlRequest;
import org.mekluppie.services.DatasetSuggestionService;
import org.mekluppie.services.SparqlEndpointService;
import org.mekluppie.services.model.AuthorQueryResponse;
import org.mekluppie.services.model.DatasetResponse;
import org.mekluppie.services.model.ImageQueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DatasetController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatasetController.class);

    private final DatasetSuggestionService datasetSuggestionService;
    private final SparqlEndpointService sparqlEndpointService;

    public DatasetController(DatasetSuggestionService datasetSuggestionService, SparqlEndpointService sparqlEndpointService) {
        this.datasetSuggestionService = datasetSuggestionService;
        this.sparqlEndpointService = sparqlEndpointService;
    }

    @PostMapping("/api/datasets/recommend")
    public DatasetResponse recommendDatasets(@RequestBody RecommendDatasetRequest userQuery) {
        return datasetSuggestionService.suggestDataset(userQuery.userQuery());
    }

    @PostMapping("/api/datasets/author")
    public List<AuthorQueryResponse> findAuthorInfo(@RequestBody SparqlRequest query) {
        return sparqlEndpointService.executeAuthorQuery(query.query());
    }

    @PostMapping("/api/datasets/images")
    public List<ImageQueryResponse> findImageInfo(@RequestBody SparqlRequest query) {
        return sparqlEndpointService.executeImageQuery(query.query());
    }
}
