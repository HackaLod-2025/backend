package org.mekluppie.services;

import org.mekluppie.services.model.SourceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class SourcesSuggestionService {
    private static final Logger logger = LoggerFactory.getLogger(SourcesSuggestionService.class);

    private final ChatClient chatClient;

    public SourcesSuggestionService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public SourceResponse recommendSources(String userQuery) {
        logger.debug("SourcesSuggestionService handling request: {}", userQuery);

        String prompt = """
                You are an intelligent agent specializing in the domain of terminology, heritage, and cultural data sources from the Netherlands’ National Heritage Network (Termennetwerk).
                
                Your core responsibility is to recommend the best matching terminology sources for user-provided search terms.
                
                You are provided with a static list of sources, each containing the following fields:
                - `source`: the official name or label of the source
                - `description`: a short summary describing what the source covers
                - `url`: the web address of the source
                
                When you receive a user query:
                1. Interpret the query semantically. Identify key concepts, categories, entities, or cultural domains implied by the terms.
                2. Compare these concepts to both the `source` and `description` fields of each listed source.
                3. Classify each source as either:
                   - **Match:** if the source name or description clearly relates to the domain or terminology of the query.
                   - **No Match:** otherwise.
                4. Return **only** the matching sources in a valid JSON array.
                   - Preserve the exact structure: `[{ "source": "...", "description": "...", "url": "..." }, ...]`
                   - Maintain proper JSON formatting (no trailing commas or comments).
                   - Do not summarize or explain results outside of JSON.
                
                If no sources are relevant, return an empty JSON array `[]`.
                
                Be concise, deterministic, and consistent. Do not invent or modify any of the listed data.
                
                Below is the complete list of 48 terminology sources:

                # source
                [
                       {
                         "source": "Adamlink: historical addresses in Amsterdam",
                         "description": "Historical addresses in Amsterdam",
                         "url": "https://adamlink.nl/geo/addresses/start/"
                       },
                       {
                         "source": "Art & Architecture Thesaurus",
                         "description": "Subjects for describing architectural, art and cultural-historical collections",
                         "url": "http://vocab.getty.edu/aat"
                       },
                       {
                         "source": "Brabants buildings",
                         "description": "Buildings in the province of Gebouwen in de provincie of North Brabant, as yet with a religious function such as monasteries",
                         "url": "https://data.brabantcloud.nl/gebouwen"
                       },
                       {
                         "source": "Brinkman subjects",
                         "description": "Subjects that the National Library of the Netherlands assigns to publications",
                         "url": "http://data.bibliotheken.nl/id/dataset/brinkman"
                       },
                       {
                         "source": "Colonial Past",
                         "description": "Terms for describing museum collections about the colonial past",
                         "url": "https://data.cultureelerfgoed.nl/koloniaalverleden/"
                       },
                       {
                         "source": "Dutch East Indies Heritage Thesaurus",
                         "description": "Terms for describing collections from the period 1930–1970 around the former Dutch East Indies, independent Indonesia and postcolonial migration of persons to the Netherlands",
                         "url": "https://data.indischherinneringscentrum.nl/ied"
                       },
                       {
                         "source": "Dutch National Thesaurus for Author Names",
                         "description": "Names and other personal data of authors",
                         "url": "http://data.bibliotheken.nl/id/dataset/persons"
                       },
                       {
                         "source": "GeoNames: geographical names in The Netherlands, Belgium and Germany",
                         "description": "Selection of geographical names such as places, administrative divisions (municipalities, provinces) and water bodies (rivers, streams, lakes etc.)",
                         "url": "https://www.geonames.org#nl-be-de"
                       },
                       {
                         "source": "GTAA: classification",
                         "description": "Classifications for describing audiovisual material",
                         "url": "http://data.beeldengeluid.nl/gtaa/Classificatie"
                       },
                       {
                         "source": "GTAA: genres",
                         "description": "Genres for describing audiovisual material",
                         "url": "http://data.beeldengeluid.nl/gtaa/Genre"
                       },
                       {
                         "source": "GTAA: geographical names",
                         "description": "Geographical names for describing audiovisual material",
                         "url": "http://data.beeldengeluid.nl/gtaa/GeografischeNamen"
                       },
                       {
                         "source": "GTAA: names",
                         "description": "Various types of proper names for describing audiovisual material",
                         "url": "http://data.beeldengeluid.nl/gtaa/Namen"
                       },
                       {
                         "source": "GTAA: personal names",
                         "description": "Persons for describing audiovisual material",
                         "url": "http://data.beeldengeluid.nl/gtaa/Persoonsnamen"
                       },
                       {
                         "source": "GTAA: subjects",
                         "description": "Subjects for describing audiovisual material",
                         "url": "http://data.beeldengeluid.nl/gtaa/Onderwerpen"
                       },
                       {
                         "source": "GTAA: subjects sound-vision",
                         "description": "Subjects that are only applied to shots/clips/sounds for describing audiovisual material",
                         "url": "http://data.beeldengeluid.nl/gtaa/OnderwerpenBenG"
                       },
                       {
                         "source": "Homosaurus",
                         "description": "Terms for describing LGBTIQ (Lesbian/Gay/Bisexual/Transgender/Intersex/Queer) publications and heritage",
                         "url": "https://data.ihlia.nl/homosaurus"
                       },
                       {
                         "source": "Iconclass",
                         "description": "Terms for describing the content of images",
                         "url": "https://iconclass.org"
                       },
                       {
                         "source": "Rijksmonumentenregister",
                         "description": "National monuments in the Netherlands",
                         "url": "https://linkeddata.cultureelerfgoed.nl/cho-kennis/id/rijksmonument/"
                       },
                       {
                         "source": "RKDartists",
                         "description": "Biographical data of Dutch and foreign artists from the Middle Ages to the present",
                         "url": "https://data.rkd.nl/rkdartists"
                       },
                       {
                         "source": "STCN: printers",
                         "description": "Printers, a subset of Short-Title Catalogue Netherlands (STCN)",
                         "url": "http://data.bibliotheken.nl/id/dataset/stcn/printers"
                       },
                       {
                         "source": "Thesaurus Camp Westerbork",
                         "description": "Terms for describing and contextualising collections relating to the history of Camp Westerbork (1939–1971) and dealing with the historical site (1971–present)",
                         "url": "https://data.kampwesterbork.nl/thesaurus"
                       },
                       {
                         "source": "Thesaurus National Museum of World Cultures",
                         "description": "Subjects divided over the facets Function, Culture, Geographical origin, Object, Material & Technique",
                         "url": "https://data.colonialcollections.nl/nmvw/thesaurus"
                       },
                       {
                         "source": "Thesaurus WW2",
                         "description": "Events, places, concepts and objects from the Second World War",
                         "url": "https://data.niod.nl/WO2_Thesaurus"
                       },
                       {
                         "source": "Wikidata: persons",
                         "description": "Persons",
                         "url": "https://www.wikidata.org#entities-persons"
                       },
                       {
                         "source": "WW2 biographies",
                         "description": "Short biographies of persons who played a key role in World War II",
                         "url": "https://data.niod.nl/WO2_biografieen"
                       }
                     ]
                
                """;

        String userMessage = String.format("""
                Here are the terms to find sources for:
                %s
                
                Return all matching sources from the provided list that are relevant to these terms.\s
                Match terms by meaning as well as by keywords in the source and description.
                Output only a JSON array containing the matching sources (no explanations or additional text).
                """, userQuery);

        return chatClient.prompt()
                .system(prompt)
                .user(userMessage)
                .call()
                .entity(SourceResponse.class);
    }
}
