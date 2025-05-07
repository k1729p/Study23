package kp.resource.server.a.services;

import kp.resource.server.a.model.ClassificationLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.lang.invoke.MethodHandles;

/**
 * Provides access to the next resource server within this local service.
 * This service is responsible for retrieving documents based on the classification level.
 */
@Service
public class DocumentService {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());
    private final RestClient restClient;
    @Value("${custom.next-resource-server-url}")
    private String nextResourceServerUrl;

    /**
     * Parameterized constructor.
     *
     * @param restClient the {@link RestClient} used for communicating with the resource server
     */
    @Autowired
    public DocumentService(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Retrieves the document from the resource server endpoint based on the classification level.
     *
     * @param classificationLevel the {@link ClassificationLevel} used to specify the document's classification
     * @return the document content as a string
     */
    public String findDocument(ClassificationLevel classificationLevel) {

        if (classificationLevel == null) {
            final String message = "Classification level cannot be null";
            logger.error("findDocument(): {}", message);
            throw new IllegalArgumentException(message);
        }
        final String document = restClient.get()
                .uri("%s/api/document/%s".formatted(nextResourceServerUrl, classificationLevel.name().toLowerCase()))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().toEntity(String.class).getBody();
        logger.info("findDocument(): document [{}], classificationLevel[{}]", document, classificationLevel);
        return document;
    }
}
