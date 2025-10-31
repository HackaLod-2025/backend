package org.mekluppie.services.model;

import java.util.List;

public record TermsSuggestResponse(List<TermsSuggestItem> terms) {

    public record TermsSuggestItem(String uri, String prefLabel, String scopeNote, String source) { }
}
