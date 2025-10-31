package org.mekluppie.services;


import org.junit.jupiter.api.Test;

import java.util.List;

class TermsSuggestionServiceTest {

    @Test
    void getTermSuggestionsTest() throws Exception {
        TermsSuggestionService service = new TermsSuggestionService();

        var response = service.fetchTerms(
                List.of("http://vocab.getty.edu/aat#processes-and-techniques", "http://vocab.getty.edu/aat"),
                "architecture rotterdam",
                List.of("en")
        );

        System.out.println(response);
    }
}