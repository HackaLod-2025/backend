package org.mekluppie.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.weaviate.client.WeaviateClient;
import io.weaviate.client.base.Result;
import io.weaviate.client.v1.graphql.model.GraphQLResponse;
import io.weaviate.client.v1.schema.model.DataType;
import io.weaviate.client.v1.schema.model.Property;
import io.weaviate.client.v1.schema.model.WeaviateClass;
import jakarta.annotation.PostConstruct;
import org.mekluppie.services.model.DatasetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service to initialize Weaviate cluster and create schema for DatasetResponse.
 * Uses Spring Boot's Weaviate starter for client configuration.
 */
@Service
public class InitializeWeaviate {

    private static final Logger log = LoggerFactory.getLogger(InitializeWeaviate.class);
    private static final String CLASS_NAME = "Dataset";
    
    private final WeaviateClient client;
    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;

    /**
     * Constructor using Spring Boot's autoconfigured WeaviateClient.
     * Configure in application.properties:
     * spring.ai.vectorstore.weaviate.host=localhost:8080
     * spring.ai.vectorstore.weaviate.scheme=http
     * spring.ai.vectorstore.weaviate.api-key= (optional)
     * 
     * @param client Autowired WeaviateClient from Spring Boot starter
     */
    public InitializeWeaviate(WeaviateClient client, ResourceLoader resourceLoader) {
        this.client = client;
        this.objectMapper = new ObjectMapper();
        this.resourceLoader = resourceLoader;
    }

    /**
     * Creates the schema for Dataset class in Weaviate based on DatasetResponse structure.
     * 
     * @return true if schema was created successfully, false otherwise
     */
    @PostConstruct
    public boolean createSchema() {
        try {
            // Check if class already exists
            Result<WeaviateClass> existingClass = client.schema().classGetter()
                .withClassName(CLASS_NAME)
                .run();
            
            if (existingClass.getResult() != null) {
                log.info("Schema '{}' already exists. Skipping creation.", CLASS_NAME);
                return true;
            }
        } catch (Exception e) {
            log.debug("Class does not exist yet, will create: {}", e.getMessage());
        }

        // Define properties based on DatasetResponse.DatasetItem
        Property titleProperty = Property.builder()
            .name("title")
            .description("The title of the dataset")
            .dataType(Arrays.asList(DataType.TEXT))
            .build();

        Property descriptionProperty = Property.builder()
            .name("description")
            .description("The description of the dataset")
            .dataType(Arrays.asList(DataType.TEXT))
            .build();

        Property publisherProperty = Property.builder()
            .name("publisher")
            .description("The publisher of the dataset")
            .dataType(Arrays.asList(DataType.TEXT))
            .build();

        // Create the class
        WeaviateClass datasetClass = WeaviateClass.builder()
            .className(CLASS_NAME)
            .description("A dataset representing cultural heritage and library data")
            .properties(Arrays.asList(titleProperty, descriptionProperty, publisherProperty))
            .vectorizer("text2vec-openai") // Using OpenAI embeddings
            .build();

        try {
            Result<Boolean> result = client.schema().classCreator()
                .withClass(datasetClass)
                .run();

            if (result.hasErrors()) {
                log.error("Error creating schema: {}", result.getError());
                return false;
            }

            log.info("Schema '{}' created successfully", CLASS_NAME);
            importDataFromJson("list.json");
            return true;
        } catch (Exception e) {
            log.error("Exception while creating schema", e);
            return false;
        }
    }

    /**
     * Deletes the schema if it exists.
     * 
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteSchema() {
        try {
            Result<Boolean> result = client.schema().classDeleter()
                .withClassName(CLASS_NAME)
                .run();

            if (result.hasErrors()) {
                log.error("Error deleting schema: {}", result.getError());
                return false;
            }

            log.info("Schema '{}' deleted successfully", CLASS_NAME);
            return true;
        } catch (Exception e) {
            log.error("Exception while deleting schema", e);
            return false;
        }
    }

    /**
     * Imports data from JSON file into Weaviate.
     * 
     * @param jsonFilePath Path to the JSON file containing DatasetResponse data
     * @return Number of items imported
     * @throws IOException if file cannot be read
     */
    public int importDataFromJson(String jsonFilePath) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + jsonFilePath);
        DatasetResponse.DatasetItem[] items = objectMapper.readValue(
                resource.getInputStream(),
                DatasetResponse.DatasetItem[].class
        );

        return importData(Arrays.asList(items));
    }

    /**
     * Imports a list of dataset items into Weaviate.
     * 
     * @param items List of DatasetItem objects to import
     * @return Number of items imported successfully
     */
    public int importData(List<DatasetResponse.DatasetItem> items) {
        int successCount = 0;

        for (DatasetResponse.DatasetItem item : items) {
            try {
                Map<String, Object> dataObject = new HashMap<>();
                dataObject.put("title", item.title());
                dataObject.put("description", item.description());
                dataObject.put("publisher", item.publisher());

                Result<io.weaviate.client.v1.data.model.WeaviateObject> result = client.data().creator()
                    .withClassName(CLASS_NAME)
                    .withProperties(dataObject)
                    .run();

                if (result.hasErrors()) {
                    log.error("Error importing item '{}': {}", item.title(), result.getError());
                } else {
                    successCount++;
                }
            } catch (Exception e) {
                log.error("Exception importing item '{}'", item.title(), e);
            }
        }

        log.info("Imported {} out of {} items", successCount, items.size());
        return successCount;
    }

    /**
     * Checks if the Weaviate cluster is reachable.
     * 
     * @return true if cluster is reachable, false otherwise
     */
    public boolean isReady() {
        try {
            Result<Boolean> result = client.misc().readyChecker().run();
            return result.getResult() != null && result.getResult();
        } catch (Exception e) {
            log.error("Error checking Weaviate readiness", e);
            return false;
        }
    }

    /**
     * Gets the Weaviate client instance.
     * 
     * @return WeaviateClient instance
     */
    public WeaviateClient getClient() {
        return client;
    }

    /**
     * Queries the Dataset collection in Weaviate.
     *
     * @param query The search query text
     * @param limit Maximum number of results to return
     * @return List of matching dataset items with their similarity scores
     */
    public List<QueryResult> queryDatasets(String query, int limit) {
        try {
            io.weaviate.client.v1.graphql.query.fields.Field[] fields = new io.weaviate.client.v1.graphql.query.fields.Field[]{
                io.weaviate.client.v1.graphql.query.fields.Field.builder().name("title").build(),
                io.weaviate.client.v1.graphql.query.fields.Field.builder().name("description").build(),
                io.weaviate.client.v1.graphql.query.fields.Field.builder().name("publisher").build(),
                io.weaviate.client.v1.graphql.query.fields.Field.builder().name("_additional { distance }").build()
            };
            
            Result<GraphQLResponse> result = client.graphQL().get()
                    .withClassName(CLASS_NAME)
                    .withFields(fields)
                    .withNearText(io.weaviate.client.v1.graphql.query.argument.NearTextArgument.builder()
                            .concepts(new String[]{query})
                            .build())
                    .withLimit(limit)
                    .run();

            if (result.hasErrors()) {
                log.error("Error querying datasets: {}", result.getError());
                return List.of();
            }

            return parseQueryResults(result.getResult());
        } catch (Exception e) {
            log.error("Exception while querying datasets", e);
            return List.of();
        }
    }

    /**
     * Parses GraphQL response into QueryResult objects.
     */
    private List<QueryResult> parseQueryResults(io.weaviate.client.v1.graphql.model.GraphQLResponse response) {
        List<QueryResult> results = new java.util.ArrayList<>();

        if (response.getData() == null) {
            return results;
        }

        Map<String, Object> data = (Map<String, Object>) response.getData();
        Map<String, Object> get = (Map<String, Object>) data.get("Get");
        List<Map<String, Object>> datasets = (List<Map<String, Object>>) get.get(CLASS_NAME);

        if (datasets != null) {
            for (Map<String, Object> dataset : datasets) {
                String title = (String) dataset.get("title");
                String description = (String) dataset.get("description");
                String publisher = (String) dataset.get("publisher");

                Map<String, Object> additional = (Map<String, Object>) dataset.get("_additional");
                Double distance = additional != null ? (Double) additional.get("distance") : null;

                results.add(new QueryResult(title, description, publisher, distance));
            }
        }

        log.info("Found {} results for query", results.size());
        return results;
    }

    /**
     * Result object for query responses.
     */
    public record QueryResult(String title, String description, String publisher, Double distance) {}

}
