package org.mekluppie.restapp;

import org.mekluppie.restapp.model.RecommendDatasetRequest;
import org.mekluppie.services.DatasetSuggestionService;
import org.mekluppie.services.model.DatasetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DatasetController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatasetController.class);

    private final DatasetSuggestionService datasetSuggestionService;

    public DatasetController(DatasetSuggestionService datasetSuggestionService) {
        this.datasetSuggestionService = datasetSuggestionService;
    }

    @PostMapping("/api/datasets/recommend")
    public DatasetResponse recommendDatasets(@RequestBody RecommendDatasetRequest userQuery) {
        return datasetSuggestionService.suggestDataset(userQuery.userQuery());
    }
}
