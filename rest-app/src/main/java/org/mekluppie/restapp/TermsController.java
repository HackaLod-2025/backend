package org.mekluppie.restapp;

import org.mekluppie.restapp.model.RecommendTermsRequest;
import org.mekluppie.services.TermsSuggestionService;
import org.mekluppie.services.model.TermsSuggestResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TermsController {
    private final TermsSuggestionService termsSuggestionService;

    public TermsController(TermsSuggestionService termsSuggestionService) {
        this.termsSuggestionService = termsSuggestionService;
    }

    @PostMapping("/api/terms/recommend")
    public TermsSuggestResponse suggestTerms(@RequestBody RecommendTermsRequest recommendTermsRequest)  throws Exception {

        return termsSuggestionService.fetchTerms(
                recommendTermsRequest.sources(),
                recommendTermsRequest.query(),
                recommendTermsRequest.languages()
        );
    }
}
