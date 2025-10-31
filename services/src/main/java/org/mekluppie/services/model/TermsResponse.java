package org.mekluppie.services.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TermsResponse(TermsData data) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TermsData(
            List<TermSource> terms
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TermSource(
            Source source,
            TermResult result,
            long responseTimeMs
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Source(
            String name,
            String uri,
            String alternateName,
            String description,
            List<Creator> creators
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Creator(
            String name,
            String alternateName
    ) {}

    // Allow flexible mapping for result (TranslatedTerms or Error)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TermResult(
            List<Term> terms
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Term(
            String uri,
            List<Label> prefLabel,
            List<Label> altLabel,
            List<Label> hiddenLabel,
            List<Label> scopeNote,
            List<String> seeAlso,
            List<RelatedTerm> broader,
            List<RelatedTerm> narrower,
            List<RelatedTerm> related,
            List<RelatedTerm> exactMatch // corrected: not plain String
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Label(
            String language,
            String value
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RelatedTerm(
            String uri,
            List<Label> prefLabel
    ) {}
}