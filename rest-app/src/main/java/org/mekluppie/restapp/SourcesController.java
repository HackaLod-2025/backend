package org.mekluppie.restapp;

import org.mekluppie.restapp.model.RecommendSourceRequest;
import org.mekluppie.services.TermsSourcesService;
import org.mekluppie.services.model.SourceResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SourcesController {
    private final TermsSourcesService service;

    public SourcesController(TermsSourcesService service) {
        this.service = service;
    }


    @PostMapping("/api/sources/recommend")
    public SourceResponse recommendSources(@RequestBody RecommendSourceRequest userQuery) {
        return service.recommendSources(userQuery.userQuery());
    }
}
