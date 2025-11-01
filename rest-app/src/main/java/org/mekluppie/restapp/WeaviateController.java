package org.mekluppie.restapp;

import org.mekluppie.services.InitializeWeaviate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WeaviateController {
    private static final Logger log = LoggerFactory.getLogger(WeaviateController.class);

    private final InitializeWeaviate initializeWeaviate;

    public WeaviateController(InitializeWeaviate initializeWeaviate) {
        this.initializeWeaviate = initializeWeaviate;
    }

    @GetMapping("/api/weaviate/query")
    public List<InitializeWeaviate.QueryResult> doQuery(@RequestParam(name = "term") String term) {
        List<InitializeWeaviate.QueryResult> results = initializeWeaviate.queryDatasets(term, 5);
        results.forEach(r -> log.info("Found: {} (distance: {})", r.title(), r.distance()));
        return results;
    }
}
