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
                         "source": "Adamlink: streets in Amsterdam",
                         "description": "Streets in Amsterdam",
                         "url": "https://adamlink.nl/geo/streets/list"
                       },
                       {
                         "source": "Archaeological Basic Register",
                         "description": "Subjects for describing research, finds and traces",
                         "url": "https://data.cultureelerfgoed.nl/term/id/abr"
                       },
                       {
                         "source": "Art & Architecture Thesaurus",
                         "description": "Subjects for describing architectural, art and cultural-historical collections",
                         "url": "http://vocab.getty.edu/aat"
                       },
                       {
                         "source": "Art & Architecture Thesaurus - materials",
                         "description": "Selection of AAT terms for describing materials in architectural, art and cultural-historical collections",
                         "url": "http://vocab.getty.edu/aat#materials"
                       },
                       {
                         "source": "Art & Architecture Thesaurus - processes and techniques",
                         "description": "Selection of AAT terms for describing processes and techniques in architectural, art and cultural-historical collections",
                         "url": "http://vocab.getty.edu/aat#processes-and-techniques"
                       },
                       {
                         "source": "Art & Architecture Thesaurus - styles and periods",
                         "description": "Selection of AAT terms for describing styles in architectural, art and cultural-historical collections",
                         "url": "http://vocab.getty.edu/aat#styles-and-periods"
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
                         "source": "Cultural-historical Thesaurus",
                         "description": "Subjects for describing cultural heritage",
                         "url": "https://data.cultureelerfgoed.nl/term/id/cht"
                       },
                       {
                         "source": "Cultural-historical Thesaurus - Materials",
                         "description": "Subjects for describing material features of cultural heritage",
                         "url": "https://data.cultureelerfgoed.nl/term/id/cht#materials"
                       },
                       {
                         "source": "Cultural-historical Thesaurus - Styles and periods",
                         "description": "Subjects for describing stylistic and periodical features of cultural heritage",
                         "url": "https://data.cultureelerfgoed.nl/term/id/cht#styles-and-periodes"
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
                         "source": "EuroVoc - thesaurus of the European Union",
                         "description": "Subjects about all areas in which the European Union operates, with an emphasis on parliamentary activities of the EU",
                         "url": "https://data.europa.eu/data/datasets/eurovoc"
                       },
                       {
                         "source": "GeoNames: geographical names in The Netherlands, Belgium and Germany",
                         "description": "Selection of geographical names such as places, administrative divisions (municipalities, provinces) and water bodies (rivers, streams, lakes etc.)",
                         "url": "https://www.geonames.org#nl-be-de"
                       },
                       {
                         "source": "GeoNames: global geographical names",
                         "description": "Selection of geographical names such as places, administrative divisions (municipalities, provinces) and water bodies (rivers, streams, lakes etc.)",
                         "url": "https://www.geonames.org"
                       },
                       {
                         "source": "Gouda streets",
                         "description": "Streets in Gouda",
                         "url": "https://www.goudatijdmachine.nl/id/straten"
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
                         "source": "Music: genres and styles",
                         "description": "Genres and styles in the collection of Muziekweb",
                         "url": "https://data.muziekweb.nl/MuziekwebOrganization/Muziekweb#mw-genresstijlen"
                       },
                       {
                         "source": "Music: persons and groups",
                         "description": "Musical artists, both individuals and groups, in the collection of Muziekweb",
                         "url": "https://data.muziekweb.nl/MuziekwebOrganization/Muziekweb#mw-personengroepen"
                       },
                       {
                         "source": "Muziekschatten: classical music works",
                         "description": "Terms for describing (Dutch) classical music works",
                         "url": "https://data.muziekschatten.nl/som/Klassiekewerken"
                       },
                       {
                         "source": "Muziekschatten: persons",
                         "description": "Persons in the catalog of the sheet music collection of Stichting Omroep Muziek",
                         "url": "https://data.muziekschatten.nl/#personen"
                       },
                       {
                         "source": "Muziekschatten: subjects",
                         "description": "Subjects in the catalog of the sheet music collection of Stichting Omroep Muziek",
                         "url": "https://data.muziekschatten.nl/#onderwerpen"
                       },
                       {
                         "source": "Regiotermen Fryslân: Persons",
                         "description": "Frysian historical persons, a collection of historical figures from the Dutch region of Fryslân.",
                         "url": "https://fryslan.regiotermen.nl/personen"
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
                         "source": "Thesaurus Historische Persoonsgegevens - roles",
                         "description": "Standardized terms for the role that a person can have in the source. Part of the Persons in Context standard",
                         "url": "https://terms.personsincontext.org/ThesaurusHistorischePersoonsgegevens/44"
                       },
                       {
                         "source": "Thesaurus Historische Persoonsgegevens - source types",
                         "description": "Standardized terms for the type of source in which a person is mentioned. Part of the Persons in Context standard",
                         "url": "https://terms.personsincontext.org/ThesaurusHistorischePersoonsgegevens/523"
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
                         "source": "Uitvoeringsmedium",
                         "description": "Terms for describing musical instruments, vocal parts and ensembles (in single and multiple formation)",
                         "url": "https://data.muziekschatten.nl/som/Uitvoeringsmedium"
                       },
                       {
                         "source": "Wikidata: all entities",
                         "description": "All entities in Wikidata, such as professions, movies or artworks",
                         "url": "https://www.wikidata.org#entities-all"
                       },
                       {
                         "source": "Wikidata: persons",
                         "description": "Persons",
                         "url": "https://www.wikidata.org#entities-persons"
                       },
                       {
                         "source": "Wikidata: places in the Netherlands and Belgium",
                         "description": "Places in the Netherlands and Belgium",
                         "url": "https://www.wikidata.org#entities-places"
                       },
                       {
                         "source": "Wikidata: streets in the Netherlands",
                         "description": "Streets in the Netherlands",
                         "url": "https://www.wikidata.org#entities-streets"
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
