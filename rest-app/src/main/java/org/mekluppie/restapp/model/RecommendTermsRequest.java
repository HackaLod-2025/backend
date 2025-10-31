package org.mekluppie.restapp.model;

import java.util.List;

public record RecommendTermsRequest(List<String> sources, String query, List<String> languages) {
}
