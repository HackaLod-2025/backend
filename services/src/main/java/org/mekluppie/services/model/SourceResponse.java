package org.mekluppie.services.model;

import java.util.List;

public record SourceResponse(List<SourceItem> sources) {

    public record SourceItem(String source, String description, String url) { }
}
