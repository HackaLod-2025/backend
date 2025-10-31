package org.mekluppie.services;


import org.junit.jupiter.api.Test;

import java.util.List;

class TermsEndpointServiceTest {

    @Test
    void getTermSuggestionsTest() throws Exception {
        TermsEndpointService service = new TermsEndpointService();

        var response = service.fetchTerms(
                List.of("http://vocab.getty.edu/aat#processes-and-techniques", "http://vocab.getty.edu/aat"),
                "architecture rotterdam",
                List.of("en")
        );

        System.out.println(response);
    }
}