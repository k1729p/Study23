package kp.gateway;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Contains constant values used in the tests.
 */
class TestConstants {
    /**
     * The realm import file.
     */
    static final String REALM_IMPORT_FILE = "realm-export.json";
    /**
     * The root path.
     */
    static final String ROOT_PATH = "/test";
    /**
     * The path for pinging.
     */
    static final String PING_PATH = "/ping";
    /**
     * The result.
     */
    static final String RESULT = "Hello!";
    /**
     * The supplier of the form data to write to the output message.
     */
    static final Supplier<MultiValueMap<String, String>> FORM_DATA_SUP = () -> {
        final MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.put("grant_type", Collections.singletonList("password"));
        formData.put("client_id", Collections.singletonList("spring-with-test-scope"));
        formData.put("username", Collections.singletonList("alice"));
        formData.put("password", Collections.singletonList("alice"));
        return formData;
    };
    /**
     * The token formatter.
     */
    static final String TOKEN_URI_FMT = "%s/realms/demo/protocol/openid-connect/token";
    /**
     * The registry consumer.
     */
    static final BiConsumer<KeycloakContainer, DynamicPropertyRegistry> REGISTRY_CONSUMER = (keycloak, registry) -> {
        registry.add("spring.security.oauth2.client.provider.keycloak.issuer-uri",
                () -> keycloak.getAuthServerUrl() + "/realms/demo");
        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
                () -> keycloak.getAuthServerUrl() + "/realms/demo/protocol/openid-connect/certs");
        registry.add("spring.cloud.gateway.routes[0].uri",
                () -> "http://localhost:8099");
        registry.add("spring.cloud.gateway.routes[0].id", () -> "callme-service");
        registry.add("spring.cloud.gateway.routes[0].predicates[0]", () -> "Path=/test/**");
    };

    /**
     * Private constructor to prevent instantiation.
     */
    private TestConstants() {
        throw new IllegalStateException("Utility class");
    }
}
