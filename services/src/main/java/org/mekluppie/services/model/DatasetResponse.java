package org.mekluppie.services.model;

import java.util.List;

public record DatasetResponse(List<DatasetItem> datasets) {

    public record DatasetItem(String title, String description, String publisher) { }
}
