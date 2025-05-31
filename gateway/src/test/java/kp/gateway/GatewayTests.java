package kp.gateway;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URISyntaxException;

import static kp.gateway.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Gateway tests.
 * These tests validate the behavior of the Gateway application.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GatewayTests {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());
    private static String accessToken;

    @Autowired
    private WebTestClient webTestClient;

    @Container
    private static final KeycloakContainer keycloak = new KeycloakContainer()
            .withRealmImportFile(REALM_IMPORT_FILE)
            .withExposedPorts(8080, 9000);

    /**
     * Registers the resource server issuer property.
     *
     * @param registry the {@link DynamicPropertyRegistry} used to set dynamic properties
     */
    @DynamicPropertySource
    static void registerResourceServerIssuerProperty(DynamicPropertyRegistry registry) {
        REGISTRY_CONSUMER.accept(keycloak, registry);
    }

    /**
     * Validates that accessing the ping endpoint redirects to the login page.
     */
    @Test
    @Order(1)
    @DisplayName("🟩 should redirect to login page")
    void shouldRedirectToLoginPage() {
        // GIVEN
        final WebTestClient.RequestHeadersSpec<?> requestSpec = webTestClient.get().uri(ROOT_PATH + PING_PATH);
        // WHEN
        final WebTestClient.ResponseSpec responseSpec = requestSpec.exchange();
        // THEN
        responseSpec.expectStatus().is3xxRedirection();
        logger.info("shouldRedirectToLoginPage():");
    }

    /**
     * Obtains an access token by sending a request to the Keycloak authorization server.
     *
     * @throws URISyntaxException the URI syntax exception
     */
    @Test
    @Order(2)
    @DisplayName("🟩 should obtain access token")
    void shouldObtainAccessToken() throws URISyntaxException {
        // GIVEN
        final URI authorizationURI = new URIBuilder(
                TOKEN_URI_FMT.formatted(keycloak.getAuthServerUrl())).build();
        final WebClient.RequestHeadersSpec<?> requestSpec = WebClient.builder().build()
                .post().uri(authorizationURI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(FORM_DATA_SUP.get()));
        // WHEN
        final String jsonResult = requestSpec.retrieve().bodyToMono(String.class).block();
        // THEN
        accessToken = new JacksonJsonParser().parseMap(jsonResult).get("access_token").toString();
        assertNotNull(accessToken, "Access token should not be null.");
        logger.info("shouldObtainAccessToken():");
    }

    /**
     * Validates that the access token is used to successfully access the ping endpoint.
     */
    @Test
    @Order(3)
    @DisplayName("🟩 should return access token")
    void shouldReturnAccessToken() {
        // GIVEN
        final WebTestClient.RequestHeadersSpec<?> requestSpec = webTestClient.get().uri(ROOT_PATH + PING_PATH)
                .header("Authorization", "Bearer " + accessToken);
        // WHEN
        final WebTestClient.ResponseSpec responseSpec = requestSpec.exchange();
        // THEN
        responseSpec.expectStatus().is2xxSuccessful()
                .expectBody(String.class).isEqualTo(RESULT);
        logger.info("shouldReturnAccessToken():");
    }
}
