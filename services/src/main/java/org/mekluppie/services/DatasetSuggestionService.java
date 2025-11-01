package org.mekluppie.services;

import org.mekluppie.services.model.DatasetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class DatasetSuggestionService {
    private static final Logger logger  = LoggerFactory.getLogger(DatasetSuggestionService.class);


    private final ChatClient chatClient;

    public DatasetSuggestionService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public DatasetResponse suggestDataset(String userQuery) {
        logger.info("Suggesting dataset for query: {}", userQuery);

        String prompt = """
                You are an intelligent agent specializing in the domain of terminology, heritage, and cultural data sources from the Netherlands’ National Heritage Network (Termennetwerk).
                
                Your core responsibility is to recommend the best matching dataset user-provided search terms.
                
                You are provided with a static list of dataset (colections), each containing the following fields:
                - `title`: the official name for the dataset
                - `description`: a short summary describing what the dataset covers
                - `publisher`: the owner of the dataset
                
                When you receive a user query:
                1. Interpret the query semantically. Identify key concepts, categories, entities, or cultural domains implied by the terms.
                2. Create a list of ten terms from step 1 to use for comparison against the dataset titles and descriptions.
                3. Compare these concepts to both the `title` and `description` fields of each listed datasets.
                4. Classify each source as either:
                   - **Match:** if the dataset title or description clearly relates to the domain or terminology of the query.
                   - **No Match:** otherwise.
                5. Return **only** the matching datasets in a valid JSON array.
                   - Preserve the exact structure: `[{ "title": "...", "description": "...", "publisher": "..." }, ...]`
                   - Maintain proper JSON formatting (no trailing commas or comments).
                   - Do not summarize or explain results outside of JSON.
                
                If no sources are relevant, return an empty JSON array `[]`.
                
                Be concise, deterministic, and consistent. Do not invent or modify any of the listed data.
                
                Below is the complete list of 121 datasets:

                # datasets
                [
                  {
                    "title": "Beeldbank",
                    "description": "Beeldbank (Digital Image Repository). Contains metadata on digitized images of the collection (books, manuscripts, images, maps, archaeological and other objects). Datasource (catalogue) front end: https://lib.uva.nl/ Image Repository front end: https://www.uvaerfgoed.nl/beeldbank",
                    "publisher": "University of Amsterdam Library"
                  },
                  {
                    "title": "Amateurfilm collectie",
                    "description": "Samenvatting: De amateurfilmcollectie bevat films gemaakt vanaf de jaren 1910 tot nu. Het bevat familiefilms, reisfilms, fictiefilms en animatie. De films waren bedoeld voor vertoningen thuis of op de filmclubs in Nederland. Inmiddels zijn het geliefde onderwerpen voor historici en mediamakers.",
                    "publisher": "Nederlands Instituut voor Beeld en Geluid"
                  },
                  {
                    "title": "Anne Frank Knowledge Base",
                    "description": "Persons, events, locations and subjects related to the person Anne Frank, her family, and other people in hiding in the Secret Annex and their helpers. Information from current historical research of the Anne Frank House.",
                    "publisher": "Anne Frank House"
                  },
                  {
                    "title": "Beeld en Geluid catalogus",
                    "description": "Metadata for the catalogue objects from the Netherlands Institute for Sound and Vision.",
                    "publisher": "Nederlands Instituut voor Beeld en Geluid"
                  },
                  {
                    "title": "Brinkman trefwoordenthesaurus",
                    "description": "De Brinkman is een thesaurus waarmee de depotcollectie en de catalogus van deze collectie, de Nederlandse Bibliografie online (ORS), op onderwerp zijn ontsloten. Deze dataset bevat ook een alignment met de Gemeenschappelijke Thesaurus Audiovisuele Archieven van het Nederlands Instituut voor Beeld en Geluid. Zie http://www.kb.nl/bronnen-zoekwijzers/dataservices-en-apis voor meer informatie.",
                    "publisher": "KB, national library of the Netherlands"
                  },
                  {
                    "title": "Centsprenten",
                    "description": "Centsprenten, a dataset defined for the Europeana Rise of Literacy project.",
                    "publisher": "KB, national library of the Netherlands"
                  },
                  {
                    "title": "Gemeenschappelijke Trefwoordenthesaurus (GTT)",
                    "description": "Gemeenschappelijke Trefwoordenthesaurus (GTT), ook wel Gemeenschappelijk Onderwerpsontsluiting (GOO) genoemd, is ontwikkeld om de onderwerpsontsluiting door wetenschappelijke bibliotheken gezamenlijk uit te voeren als onderdeel van het Gemeenschappelijk Geautomatiseerd Catalogiseersysteem (GGC). Zie http://www.kb.nl/bronnen-zoekwijzers/dataservices-en-apis voor meer informatie.",
                    "publisher": "KB, national library of the Netherlands"
                  },
                  {
                    "title": "Golden age of illustration collection",
                    "description": "Bijzondere collectie van de universiteitsbibliotheek Maastricht",
                    "publisher": "Wikidata"
                  },
                  {
                    "title": "GTAA (Gemeenschappelijke Thesaurus Audiovisuele Archieven)",
                    "description": "Het Nederlands Instituut voor Beeld en Geluid heeft samen met andere Nederlandse organisaties die audiovisueel cultureel erfgoed beheren de Gemeenschappelijke Thesaurus voor Audiovisuele Archieven (GTAA) ontwikkeld. De GTAA wordt gebruikt voor het doeltreffend karakteriseren van de inhoud van audiovisueel materiaal uit het archief met labels afkomstig uit een gecontroleerde en gestructureerde lijst van termen, een thesaurus.",
                    "publisher": "Nederlands Instituut voor Beeld en Geluid"
                  },
                  {
                    "title": "LOD Archiefbeschrijvingen",
                    "description": "de linked open datasets die beschikbaar gesteld voor de archiefbeschrijvingen",
                    "publisher": "Literatuurmuseum"
                  },
                  {
                    "title": "LOD Archieven",
                    "description": "de linked open datasets die beschikbaar gesteld voor de archieven",
                    "publisher": "Literatuurmuseum"
                  },
                  {
                    "title": "Muziekopnamen Zendgemachtigden",
                    "description": "De MOZ (Muziekopnamen Zendgemachtigden) collectie bevat opnamen van concerten in de 20e en 21e eeuw, bedoeld voor uitzending door Nederlandse publieke omroepen op TV en Radio.",
                    "publisher": "Nederlands Instituut voor Beeld en Geluid"
                  },
                  {
                    "title": "Muziekweb",
                    "description": "Muziekweb is de muziekbibliotheek van Nederland. Ons doel is om muziek en de informatie over muziek voor iedereen laagdrempelig aan te bieden. Muziekweb heeft sinds 1961 een collectie opgebouwd van 600.000 cd's, 300.000 lp's en 30.000 muziek-dvd's (dat is samen goed voor zo'n zeven miljoen tracks!). De site is een bron van informatie over muziek die de afgelopen vijftig jaar in Nederland is uitgebracht, en een ideale plek om meer muziek te leren kennen. Deze dataset beschikt over de laatste linked data versie van het muziekweb. Hier vind je een linked data view waarin alle albums van het muziekweb zijn weergegeven.",
                    "publisher": "Nederlands Instituut voor Beeld en Geluid"
                  },
                  {
                    "title": "Nederlandse Bibliografie Totaal (NBT)",
                    "description": "Nederlandse Bibliografie Totaal (NBT) bevat publicaties die in Nederland, over Nederland of in de Nederlandse taal zijn verschenen",
                    "publisher": "KB, national library of the Netherlands"
                  },
                  {
                    "title": "Organisaties uit de corporatiethesaurus van de Koninklijke Bibliotheek",
                    "description": "Organisaties uit KB Corporatiethesaurus. De KB Corporatiethesaurus is een thesaurus waarmee de Depotcollectie en de catalogus van deze collectie, de Nederlandse Bibliografie online (ORS), op corporatie zijn ontsloten. Zie http://www.kb.nl/bronnen-zoekwijzers/dataservices-en-apis voor meer informatie.",
                    "publisher": "KB, national library of the Netherlands"
                  },
                  {
                    "title": "Personen uit de Nederlandse Thesaurus van Auteursnamen (NTA)",
                    "description": "In de Nederlandse Thesaurus van Auteursnamen (NTA), ook wel de Persoonsnamenthesaurus genoemd, worden naams- en andere persoonsgegevens van auteurs opgeslagen, zodat onderscheid gemaakt kan worden tussen auteurs met dezelfde naam. Deze dataset presenteert de opgenomen namen als persoon. Zie http://www.kb.nl/bronnen-zoekwijzers/dataservices-en-apis voor meer informatie.",
                    "publisher": "KB, national library of the Netherlands"
                  },
                  {
                    "title": "STCN authors and contributors",
                    "description": "Thesaurus for the authors and contributors in the Short-Title Catalogue Netherlands (STCN)",
                    "publisher": "KB, national library of the Netherlands"
                  },
                  {
                    "title": "STCN printers, publishers and booksellers",
                    "description": "Thesaurus of the printers, publishers and booksellers in the Short-Title Catalogue Netherlands (STCN)",
                    "publisher": "KB, national library of the Netherlands"
                  },
                  {
                    "title": "STCN genres",
                    "description": "Thesaurus for the genres in the Short-Title Catalogue Netherlands (STCN)",
                    "publisher": "KB, national library of the Netherlands"
                  },
                  {
                    "title": "STCN geographical headings",
                    "description": "Thesaurus for the geographical headings in the Short-Title Catalogue Netherlands (STCN)",
                    "publisher": "KB, national library of the Netherlands"
                  },
                  {
                    "title": "STCN subjects",
                    "description": "Thesaurus for the subjects in the Short-Title Catalogue Netherlands (STCN)",
                    "publisher": "KB, national library of the Netherlands"
                  },
                  {
                    "title": "Thesaurus Auteurs DBNL",
                    "description": "Thesaurus auteurs van digitale bibliotheek voor de Nederlandse letteren (DBNL).",
                    "publisher": "KB, national library of the Netherlands"
                  },
                  {
                    "title": "Thesaurus KBcode",
                    "description": "Dataset omvat de metadata van de KBcode, de onderwerpsontsluiting op de cataloguskaartjes van De Systematische Catalogus van de KB, vertaald naar Nederlandse termen.",
                    "publisher": "KB, national library of the Netherlands"
                  },
                  {
                    "title": "Title records of the Short-Title Catalogue Netherlands (STCN)",
                    "description": "Expert descriptions of books printed in the Netherlands and/or in Dutch between the second half of the fifteenth century and 1801 in more than 223.000 editions in 650.000 copies. Contains descriptions of books in the collections of many libraries in the Netherlands and various libraries abroad. All books are described book in hand. Descriptions contain information about authors, titles, imprints, size, collation formula, subject keywords and typographical features. Every edition is distinguished by the STCN fingerprint. Newspapers and ambassadors' letters are excluded.",
                    "publisher": "KB, national library of the Netherlands"
                  },
                  {
                    "title": "Titels DBNL",
                    "description": "Titels van digitale bibliotheek voor de Nederlandse letteren (DBNL).",
                    "publisher": "KB, national library of the Netherlands"
                  },
                  {
                    "title": "Chabot Museum - Museum objecten",
                    "description": "Een lijst van alle beschikbare museale objecten van Chabot Museum.",
                    "publisher": "Chabot Museum"
                  },
                  {
                    "title": "Dataset collectiedata Nieuwe Instituut",
                    "description": "dataset van collectiedata voor de knowledge graph voor The Other Interface. Het bevat de volgende vijf datasets: Archives Books Objects People Thesaurus",
                    "publisher": "Nieuwe Instituut"
                  },
                  {
                    "title": "FORT - Museum objecten",
                    "description": "Een lijst van alle beschikbare museale objecten van FORT - Fortresse Holland Hellevoetsluis.",
                    "publisher": "Fortresse Holland"
                  },
                  {
                    "title": "HAN - Collectie aardewerk website",
                    "description": "Het Hannemahuis - Collectie aardewerk website",
                    "publisher": "Het Hannemahuis"
                  },
                  {
                    "title": "HAN - Collectie Baur website",
                    "description": "Het Hannemahuis - Collectie Baur website",
                    "publisher": "Het Hannemahuis"
                  },
                  {
                    "title": "HAN - Collectie Jansen, WFG website",
                    "description": "Het Hannemahuis - Collectie Jansen, W.F.G. website",
                    "publisher": "Het Hannemahuis"
                  },
                  {
                    "title": "HAN - Collectie zilver website",
                    "description": "Het Hannemahuis - Collectie zilver website",
                    "publisher": "Het Hannemahuis"
                  },
                  {
                    "title": "KEEP - Museum objecten",
                    "description": "Een lijst van alle beschikbare museale objecten van Koninklijk Eise Eisinga Planetarium.",
                    "publisher": "Koninklijke Eise Eisinga Planetarium"
                  },
                  {
                    "title": "KRIMP - Museum objecten",
                    "description": "Een lijst van alle beschikbare museale objecten van Streekmuseum Krimpenerwaard.",
                    "publisher": "Streekmuseum Krimpenerwaard"
                  },
                  {
                    "title": "LOD Museale Objecten",
                    "description": "de linked open datasets die beschikbaar gesteld voor de museale objecten.",
                    "publisher": "Literatuurmuseum"
                  },
                  {
                    "title": "LOD Persoon en Organisatie",
                    "description": "no description",
                    "publisher": "Literatuurmuseum"
                  },
                  {
                    "title": "LOD Stambestanden",
                    "description": "De linked open datasets die beschikbaar gesteld voor diverse stambestanden.",
                    "publisher": "Literatuurmuseum"
                  },
                  {
                    "title": "LOD Thesaurus",
                    "description": "de linked open datasets die beschikbaar gesteld voor de thesaurus.",
                    "publisher": "Literatuurmuseum"
                  },
                  {
                    "title": "Museum objecten",
                    "description": "no description",
                    "publisher": "Hunebedcentrum"
                  },
                  {
                    "title": "Museum objecten",
                    "description": "no description",
                    "publisher": "Museum Rotterdam"
                  },
                  {
                    "title": "Oorlogsbronnen",
                    "description": "Beeldmateriaal van Erfgoed Enschede uit de Tweede Wereldoorlog",
                    "publisher": "Erfgoed Enschede"
                  },
                  {
                    "title": "Oorlogsdoden van en in gemeente Venlo 1940-1949",
                    "description": "De bedoelding van deze datacollectie is om een zo waarheidsgetrouw mogelijk beeld te geven van de mensen uit of in Venlo die zijn doodgegaan of vermist geraakt als gevolg van de Tweede Wereldoorlog. Ook worden de gebeurtenissen beschreven waarvan zij het slachtoffer zijn geworden. De collectie is samengesteld door Wiel Mercus (1927-2001) en is voortgezet door het Gemeentearchief Venlo. Onder 'gemeente Venlo' wordt verstaan het geheel van de stadsdelen: Belfeld, Blerick, Steyl, Tegelen en Venlo. Op verzoeken vanuit de bevolking zijn ook de Venlose burgers en militairen in de collectie opgenomen die in of als gevolg van de politionele acties in Indië hun leven hebben verloren.",
                    "publisher": "VEN - Gemeente Venlo"
                  },
                  {
                    "title": "RoMeO-ROVM-dataset-alle-objecten",
                    "description": "Metadata van alle records (objecten) in de databank van Stichting RoMeO - Rotterdams Openbaar Vervoer Museum",
                    "publisher": "Stichting RoMeO"
                  },
                  {
                    "title": "VLA - Museum objecten",
                    "description": "Een lijst van alle beschikbare museale objecten van Museum Vlaardingen.",
                    "publisher": "Museum Vlaardingen"
                  },
                  {
                    "title": "VNVN - Bibliotheek",
                    "description": "Een lijst van alle beschikbare bibliotheekstukken van Historische Collectie V&VN.",
                    "publisher": "V&VN Verpleegkundigen & Verzorgenden Nederland"
                  },
                  {
                    "title": "VNVN - Museum objecten",
                    "description": "Een lijst van alle beschikbare museale objecten van Historische Collectie V&VN.",
                    "publisher": "V&VN Verpleegkundigen & Verzorgenden Nederland"
                  },
                  {
                    "title": "Zuid-Afrikahuis - Archieven",
                    "description": "Een lijst van alle beschikbare archieven van Zuid-Afrikahuis.",
                    "publisher": "Stichting Zuid-Afrikahuis"
                  },
                  {
                    "title": "AdamNet Heritage",
                    "description": "This dataset is a combination of all the heritage datasets available on data.adamlink.nl. It enables a user to find and use cultural heritage objects without the borders between institutions. The data are linked to streets, buildings, districts and persons they have in common as much as possible.",
                    "publisher": "A'damNet"
                  },
                  {
                    "title": "Basisregistratie Adressen en Gebouwen (BAG)",
                    "description": "De Basisregistratie Adressen & Gebouwen (BAG) versie 2.0 is beschikbaar als linked open data. Met de ingang van [BAG 2.0](https://www.kadaster.nl/zakelijk/registraties/basisregistraties/bag/bag-2.0-producten/bag-2.0-wat-is-er-veranderd) zijn er een aantal zaken gewijzigd aan de BAG en hiermee ook de linked data variant van deze basisregistratie.",
                    "publisher": "Kadaster"
                  },
                  {
                    "title": "Basisregistratie Grootschalige Topografie (BGT)",
                    "description": "De Basisregistratie Grootschalige Topografie (BGT) is een gedetailleerde digitale kaart van Nederland. In de BGT worden objecten zoals gebouwen, wegen, water, spoorlijnen en groen op eenduidige manier vastgelegd. De BGT wordt wettelijk geregeld. Op 1 januari 2016 is de wet in werking getreden voor bronhouders en de Landelijke Voorziening (LV BGT). Iedereen kan de informatie uit de BGT vrij gebruiken. Voor overheden en andere wettelijke gebruikers is het gebruik verplicht. Zie [deze pagina](https://data.labs.kadaster.nl/kadaster/bgt/browser?resource=https%3A%2F%2Fbgt.basisregistraties.overheid.nl%2Fbgt) voor de metadata beschrijving van de BGT-LD.",
                    "publisher": "Kadaster"
                  },
                  {
                    "title": "Basisregistratie Topografie",
                    "description": "De BRT bestaat uit digitale topografische bestanden op verschillende schaalniveaus. Deze verzameling topografische bestanden is beschikbaar als open data. Dat betekent dat het Kadaster deze gegevensbestanden kosteloos en met minimale leveringsvoorwaarden ter beschikking stelt.",
                    "publisher": "Kadaster"
                  },
                  {
                    "title": "Beelddocumenten",
                    "description": "no description",
                    "publisher": "CODA (Apeldoorn)"
                  },
                  {
                    "title": "CBS Wijk- en Buurtkaart",
                    "description": "De Wijk- en Buurtkaart bevat de digitale geometrie van de grenzen van de buurten, wijken en gemeenten in Nederland. De kerncijfers van de buurten en de geaggregeerde kerncijfers van de wijken en gemeenten zijn aan de kaart toegevoegd samen met de statistieken over de nabijheid van voorzieningen.",
                    "publisher": "Kadaster"
                  },
                  {
                    "title": "Cultuurhistorische Thesaurus",
                    "description": "Concepten die het cultureel erfgoedveld in Nederland beslaan – Cultuurhistorische Thesaurus",
                    "publisher": "Rijksdienst voor het Cultureel Erfgoed"
                  },
                  {
                    "title": "DAF Museum - Museum objecten",
                    "description": "Een lijst van alle beschikbare museale objecten van DAF Museum.",
                    "publisher": "DAF Museum"
                  },
                  {
                    "title": "Dutch Municipalities through Time: gemeentegeschiedenis",
                    "description": "@en (Nederlands hieronder) Today, The Netherlands consists of nearly 400 municipalities. Back in 1812, there were more than 1100 municipalities. This datasets describes the history of municipalities from 1812 to present day. This dataset is a must-have when analysing or visualising Dutch municipality time-series data, or when studying individual level data in municipality contexts. This dataset consists of the 'Repertorium van Nederlandse gemeenten vanaf 1812' by Ad van der Meer en Onno Boonstra and NLGis shapefiles of Onno Boonstra available via DANS, for which the municipality borders were registered from 1812 to 1997.",
                    "publisher": "Spatial Humanities Netherlands (NLGIS)"
                  },
                  {
                    "title": "FAIR Photos - CLARIAH FAIR Data Call 2023",
                    "description": "The collection of Fotopersbureau De Boer (1945-2005) consists of over two million press photos of about 250 thousand events and is unique, at least in the Netherlands, for its extensive metadata.",
                    "publisher": "Noord-Hollands Archief"
                  },
                  {
                    "title": "Historical Database of Dutch Municipalities (HDNG)",
                    "description": "The Historical Database of Dutch Municipalities (Historische Database Nederlandse Gemeenten (HDNG)) provides a wide range of characteristics of Dutch municipalities in the period 1800 - ca. 1970.",
                    "publisher": "dataLegend"
                  },
                  {
                    "title": "IISH knowledge graph (IISG - KG)",
                    "description": "The International Institute of Social History (IISH) is one of the world's foremost research institutions in the field of social history. The institute investigates how labour and labour relations develop globally in the long term. To this end, the institute collects archives and data on a global scale. This dataset brings together archives, library, audiovisual material and research datasets.",
                    "publisher": "International Institute of Social History"
                  },
                  {
                    "title": "Kadaster Knowledge Graph",
                    "description": "Geïntegreerde ontsluiting van meerdere Kadastrale bronnen gebruikmakend van het [Schema.org](https://schema.org) vocabulaire. Momenteel bevat de Knowledge Graph gegevens die zijn opgebouwd uit de volgende Linked Data sets: - [BAG](https://data.labs.kadaster.nl/kadaster/bag2) - [BGT](https://data.labs.kadaster.nl/kadaster/bgt) - [BRT](https://data.labs.kadaster.nl/kadaster/brt-2) - [CBS](https://data.labs.kadaster.nl/kadaster/wbk)",
                    "publisher": "Kadaster"
                  },
                  {
                    "title": "Knowledge Graph",
                    "description": "The knowledge graph of Colonial Collections. It contains heritage information from data providers, combined with information from other sources, e.g. terms from thesauri and dataset descriptions from the Dataset Register of the Dutch Digital Heritage Network (NDE). Feel free to use this dataset, but please be aware that it can change at any time so long as the Colonial Collections project is not finished.",
                    "publisher": "Colonial Collections Consortium"
                  },
                  {
                    "title": "Krantenpaginas",
                    "description": "no description",
                    "publisher": "CODA (Apeldoorn)"
                  },
                  {
                    "title": "Museum objecten",
                    "description": "CODA (Apeldoorn)",
                    "publisher": ""
                  },
                  {
                    "title": "NMVW Collection Archives",
                    "description": "This dataset is published by a TriplyETL pipeline: [internal link](https://git.triply.cc/customers/nmvw/collection-archives/-/pipelines) This is the dataset for NMVW.",
                    "publisher": "Nationaal Museum van Wereldculturen"
                  },
                  {
                    "title": "Oorlogsbronnen - Krantenpaginas",
                    "description": "no description",
                    "publisher": "PeelenMaasNet"
                  },
                  {
                    "title": "Oorlogsbronnen - Verhalen",
                    "description": "no description",
                    "publisher": "PeelenMaasNet"
                  },
                  {
                    "title": "Pierre Kemp Collection",
                    "description": "One of the most fascinating loners in twentieth-century Dutch poetry is Maastricht-born Pierre Kemp (1886-1967). In 1956 he was awarded the Constantijn Huygens Prize, followed in 1958 by the P.C. Hooft Prize. Maastricht University Library manages a large part of his personal estate.",
                    "publisher": "Maastricht University Library"
                  },
                  {
                    "title": "Thesaurus",
                    "description": "The NMVW thesaurus has a long history, first under the name OVM-thesaurus, later SVCN-thesaurus. The thesaurus is mapped to AAT and geonames via `skos:exactMatch` relations. ``` http://collectie.wereldculturen.nl/downloads/XMLRDFthesaurus.zip ```",
                    "publisher": "Nationaal Museum van Wereldculturen"
                  },
                  {
                    "title": "Thesaurus Second World War, Netherlands, Dutch, Dutch-Indies, Dutch West-Indies",
                    "description": "The Thesaurus WW2 (WO2 Thesaurus) is a validated, hierarchically structured list of concepts for thematic access to resources about WW2. With a focus of the Netherlands and Dutch Colonies. The Thesaurus consists of descriptions of concepts, camps, events and organizations. The concepts describe daily life, Government, Prosecution, Law, Combat etcetera. The keywords are described in the standard technical format: SKOS. When relevant links to images, GPS coordinates and dates are added. All concepts and their relations are downloadable and reusable.",
                    "publisher": "NIOD Instituut voor Oorlogs-, Holocaust- en Genocidestudies"
                  },
                  {
                    "title": "Zuid-Afrikahuis - Beelddocumenten",
                    "description": "Een lijst van alle beschikbare beelddocumenten van Zuid-Afrikahuis.",
                    "publisher": "Stichting Zuid-Afrikahuis"
                  },
                  {
                    "title": "Cinema Context",
                    "description": "Cinema Context is an online film encyclopaedia with more than 100,000 film screenings since 1895. It provides insight into the DNA of Dutch film and cinema culture and is praised by film historians worldwide. With a DANS Small Data Project grant, this dataset has been converted to a Linked Data format (RDF). For more information, see: https://uvacreate.gitlab.io/cinema-context/cinema-context-rdf/",
                    "publisher": "Cinema Context"
                  },
                  {
                    "title": "Colonial Objects",
                    "description": "This dataset is published by a TriplyETL pipeline: [internal link](https://git.triply.cc/customers/rce/colonial-objects/-/pipelines)",
                    "publisher": "Rijksdienst voor het Cultureel Erfgoed (RCE)"
                  },
                  {
                    "title": "ECARTICO",
                    "description": "ECARTICO is a comprehensive collection of structured biographical data concerning painters, engravers, printers, book sellers, gold- and silversmiths and others involved in the 'cultural industries' of the Low Countries in the sixteenth and seventeenth centuries. For more information, see: https://www.vondel.humanities.uva.nl/ecartico/",
                    "publisher": "CREATE"
                  },
                  {
                    "title": "ENK - Museum objecten",
                    "description": "Een lijst van alle beschikbare museale objecten van Gemeente Enkhuizen.",
                    "publisher": "Gemeente Enkhuizen"
                  },
                  {
                    "title": "Indertied",
                    "description": "Deze dataset bevat foto- en filmmateriaal vanaf de oudste tijden tot heden van de voormalige gemeente Berg en Terblijt en haar nabije omgeving. Het materiaal is voorzien van objectieve metadata ter identificatie, aangevuld met subjectieve verhalen en anekdotes die een historisch tijdsbeeld schetsen van het leven in de Zuid-Limburgse dorpjes Vilt, Terblijt, Berg en Geulhem.",
                    "publisher": "Indertied"
                  },
                  {
                    "title": "KW - Kamp Westerbork - Collecties",
                    "description": "Een lijst van alle beschikbare collecties van KW - Kamp Westerbork.",
                    "publisher": "KW - Kamp Westerbork"
                  },
                  {
                    "title": "KW - Kamp Westerbork - Personen",
                    "description": "Een lijst van alle beschikbare personen van KW - Kamp Westerbork.",
                    "publisher": "KW - Kamp Westerbork"
                  },
                  {
                    "title": "KW - Kamp Westerbork - Publicaties",
                    "description": "Een lijst van alle beschikbare Publicaties van KW - Kamp Westerbork.",
                    "publisher": "KW - Kamp Westerbork"
                  },
                  {
                    "title": "Leudalmuseum",
                    "description": "Deze dataset bevat gegevens die beschikbaar zijn gesteld door Leudalmuseum.",
                    "publisher": "Leudalmuseum"
                  },
                  {
                    "title": "Linked data van de Rijksdienst voor het Cultureel Erfgoed",
                    "description": "Linked data over cultuurhistorische objecten. Het gaat hierbij om gebouwde en archeologische rijksmonumenten, vondsten, vondstlocaties, complexen, archeologische complexen, onderzoeksgebieden en terreinen.",
                    "publisher": "Rijksdienst voor het Cultureel Erfgoed"
                  },
                  {
                    "title": "Museum Thorn",
                    "description": "Deze dataset bevat gegevens die beschikbaar zijn gesteld door Museum Thorn",
                    "publisher": "Museum Thorn"
                  },
                  {
                    "title": "Musiom",
                    "description": "Deze dataset van Musiom bevat gegevens over de collectie hedendaagse kunst van een generatie Nederlandse kunstenaars die sinds eind jaren '70 actief is. De dataset omvat informatie over kunstwerken, kunstenaars, materialen, technieken en stijlen, zowel abstract als figuratief.",
                    "publisher": "Musiom"
                  },
                  {
                    "title": "Wijkmuseum Soesterkwartier",
                    "description": "Deze dataset bevat gestructureerde beschrijvingen van voorwerpen, documenten en persoonlijke verhalen vanaf ongeveer de jaren '20 van de vorige eeuw met betrekking tot de wijk Soesterkwartier in Amersfoort. De dataset is bestemd voor iedereen in of buiten het Soesterkwartier die meer wil weten over het dagelijks leven en de bedrijvigheid in deze volksbuurt.",
                    "publisher": "Wijkmuseum Soesterkwartier"
                  },
                  {
                    "title": "Akten indemniteit Zegwaart en Zoetermeer",
                    "description": "Dataset bestaande uit de akten van indemniteit van Zegwaart en Zoetermeer.",
                    "publisher": "Gemeente Zoetermeer"
                  },
                  {
                    "title": "De Hollandse Cirkel - Beeldbank",
                    "description": "Een lijst van alle beschikbare Beelddocumenten van de stichting De Hollandse Cirkel.",
                    "publisher": "Stichting De Hollandse Cirkel"
                  },
                  {
                    "title": "Demo objecten LOD",
                    "description": "Demo objecten LOD",
                    "publisher": "DEVENTit Demo"
                  },
                  {
                    "title": "Gazelle Beelddocumenten",
                    "description": "no description",
                    "publisher": "Gazelle"
                  },
                  {
                    "title": "Geheugen van Zoetermeer - Beelddocumenten",
                    "description": "no description",
                    "publisher": "Gemeente Zoetermeer"
                  },
                  {
                    "title": "Geheugen van Zoetermeer - Boeken",
                    "description": "no description",
                    "publisher": "Gemeente Zoetermeer"
                  },
                  {
                    "title": "Geheugen van Zoetermeer - Krantenpagina's",
                    "description": "Gemeente Zoetermeer",
                    "publisher": ""
                  },
                  {
                    "title": "Gouda Timemachine Linked Open Data 💡",
                    "description": "A knowledge graph of data about Gouda places (plots, buildings, addresses, bridges, watercourses, etc.) and people (residents, owners, administrators, etc.) linked to source information such as SAMH & Cadastre and thesauri such as GemeenteGeschiedenis and the Cultural Historical Thesaurus (RCE). Vocabularies used include schema.org, Records-in-context (RiCo), Persons-in-context (PiCo), PROV, Histograph and a proprietary Gouda Time Machine ontology.",
                    "publisher": "Gouda Time Machine"
                  },
                  {
                    "title": "IHLIA - LGBTI Heritage - Museum objecten",
                    "description": "Een lijst van alle beschikbare museale objecten van IHLIA - LGBTI Heritage.",
                    "publisher": "IHLIA - LGBTI Heritage"
                  },
                  {
                    "title": "Molens",
                    "description": "Linked Data mapping downloaded from the Dutch [Nationaal Georegister](https://nationaalgeoregister.nl/geonetwork/srv/dut/catalog.search#/metadata/291afe4b-4f4b-497c-8026-fb437c4e9c7e). Created as a reaction to a keynote on [Data Matters conference](https://tinyurl.com/mr4yrmna).",
                    "publisher": "Mark Lindeman"
                  },
                  {
                    "title": "Museum objecten 2.0 (Nog in test - Arman)",
                    "description": "no description",
                    "publisher": "DEVENTit Demo"
                  },
                  {
                    "title": "Oorlogsbronnen",
                    "description": "no description",
                    "publisher": "IHLIA - LGBTI Heritage"
                  },
                  {
                    "title": "Oorlogsbronnen - Archiefstukken",
                    "description": "no descrption",
                    "publisher": "Peel en Maas"
                  },
                  {
                    "title": "Oorlogsbronnen - Bestanddelen",
                    "description": "no descrption",
                    "publisher": "Peel en Maas"
                  },
                  {
                    "title": "Reconstruction Civil Registry Gouda",
                    "description": "Via burgerLinker vervaardigde dataset op basis van akten van de Burgerlijke Stand van Gouda.",
                    "publisher": "Gouda Time Machine"
                  },
                  {
                    "title": "RKD-Knowledge-Graph",
                    "description": "We manage unique archives, documentation and photographic material and the largest art historical library on Western art from the Late Middle Ages to the present, with the focus on Netherlandish art. Our collections cover not only paintings, drawings and sculptures, but also monumental art, modern media and design. The collections are present in both digital and analogue form (the latter in our study rooms). This [knowledge graph](https://en.wikipedia.org/wiki/Knowledge_graph) represents our collection as Linked Data, primarily using the [CIDOC-CRM](https://cidoc-crm.org/html/cidoc_crm_v7.1.1.html) and [LinkedArt](https://linked.art/model/) vocabularies.",
                    "publisher": "RKD"
                  },
                  {
                    "title": "Valkhof museum - Museum objecten",
                    "description": "Een lijst van alle beschikbare museale objecten van Valkhof museum.",
                    "publisher": "Valkhof Museum"
                  },
                  {
                    "title": "Zeeuws erfgoedplatform - Museum objecten",
                    "description": "Een lijst van alle beschikbare museale objecten van het Zeeuws erfgoedplatform.",
                    "publisher": "VZM"
                  },
                  {
                    "title": "De Hollandse Cirkel - Museum objecten",
                    "description": "Een overzicht van in Nederland door de Stichting De Hollandse Cirkel geïnventariseerde instrumenten die zijn toegepast bij het geodetisch proces van inwinning, verwerking en presentatie.",
                    "publisher": "Stichting De Hollandse Cirkel"
                  }
                ]
                
                
                """;

        String userMessage = String.format("""
                Here are the terms to find datasets for:
                %s
                
                Return all matching datasets from the provided list that are relevant to these terms.\s
                Match terms by meaning as well as by keywords in the title and description.
                """, userQuery);

        return chatClient.prompt()
                .system(prompt)
                .user(userMessage)
                .call()
                .entity(DatasetResponse.class);
    }

}
