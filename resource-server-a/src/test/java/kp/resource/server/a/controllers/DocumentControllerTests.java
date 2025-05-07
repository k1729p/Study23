package kp.resource.server.a.controllers;

import kp.resource.server.a.model.ClassificationLevel;
import kp.resource.server.a.services.DocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for the Document Controller, verifying access control
 * to document endpoints based on user roles and authentication.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, args = "--NEXT_RESOURCE_SERVER_URL=")
@AutoConfigureMockMvc
class DocumentControllerTests {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private DocumentService documentService;

    private static final String OFFICIAL_TEST_DOCUMENT = "official test";
    private static final String RESTRICTED_TEST_DOCUMENT = "restricted test";
    private static final String CONFIDENTIAL_TEST_DOCUMENT = "confidential test";
    private static final String SECRET_TEST_DOCUMENT = "secret test";
    private static final Function<String, SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor>
            AUTHORITIES_FUN = role -> jwt().authorities(List.of(new SimpleGrantedAuthority(role)));
    private static final MediaType PLAIN_TEXT_UTF_8 = new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8);

    /**
     * Sets up the mock behavior for the DocumentService before each test.
     * Maps classification levels to their corresponding test documents.
     */
    @BeforeEach
    void setup() {
        Map.of(
                ClassificationLevel.OFFICIAL, OFFICIAL_TEST_DOCUMENT,
                ClassificationLevel.RESTRICTED, RESTRICTED_TEST_DOCUMENT,
                ClassificationLevel.CONFIDENTIAL, CONFIDENTIAL_TEST_DOCUMENT,
                ClassificationLevel.SECRET, SECRET_TEST_DOCUMENT
        ).forEach((key, value) -> Mockito.when(
                documentService.findDocument(key)).thenReturn(value));
    }

    /**
     * Verifies that an authorized user with the specified role can access
     * the document endpoint and receive the expected response.
     *
     * @param classificationLevel the classification level of the document
     * @param role                the role of the user
     * @throws Exception if an error occurs during the request or response validation
     */
    @ParameterizedTest
    @CsvSource({
            "official, ROLE_OFFICIAL",
            "restricted, ROLE_RESTRICTED",
            "confidential, ROLE_CONFIDENTIAL",
            "secret, ROLE_SECRET"
    })
    @DisplayName("🟩 should get document")
    void shouldGetDocument(String classificationLevel, String role) throws Exception {
        // GIVEN
        final RequestBuilder requestBuilder = get("/api/document/%s".formatted(classificationLevel))
                .with(AUTHORITIES_FUN.apply(role));
        // WHEN
        final ResultActions resultActions = mockMvc.perform(requestBuilder);
        // THEN
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.content().contentType(PLAIN_TEXT_UTF_8));
        final String expectedDocument = switch (classificationLevel) {
            case "official" -> OFFICIAL_TEST_DOCUMENT;
            case "restricted" -> RESTRICTED_TEST_DOCUMENT;
            case "confidential" -> CONFIDENTIAL_TEST_DOCUMENT;
            case "secret" -> SECRET_TEST_DOCUMENT;
            default -> throw new IllegalArgumentException(
                    "Unknown classification level[%s]".formatted(classificationLevel));
        };
        resultActions.andExpect(MockMvcResultMatchers.content().string(expectedDocument));
        logger.info("shouldGetDocument(): classificationLevel[{}], role[{}]",
                classificationLevel, role);
    }

    /**
     * Verifies that an authorized user with an incorrect role is forbidden
     * from accessing the document endpoint and receives an HTTP 403 Forbidden status.
     *
     * @param classificationLevel the classification level of the document
     * @param role                the incorrect role of the user
     * @throws Exception if an error occurs during the request or response validation
     */
    @ParameterizedTest
    @CsvSource({
            "official, ROLE_RESTRICTED",
            "official, ROLE_CONFIDENTIAL",
            "official, ROLE_SECRET",
            "restricted, ROLE_OFFICIAL",
            "restricted, ROLE_CONFIDENTIAL",
            "restricted, ROLE_SECRET",
            "confidential, ROLE_OFFICIAL",
            "confidential, ROLE_RESTRICTED",
            "confidential, ROLE_SECRET",
            "secret, ROLE_OFFICIAL",
            "secret, ROLE_RESTRICTED",
            "secret, ROLE_CONFIDENTIAL"
    })
    @DisplayName("🟥 should not get document when incorrect role provided")
    void shouldNotGetDocumentWhenIncorrectRoleProvided(String classificationLevel, String role) throws Exception {
        // GIVEN
        final RequestBuilder requestBuilder = get("/api/document/%s".formatted(classificationLevel))
                .with(AUTHORITIES_FUN.apply(role));
        // WHEN
        final ResultActions resultActions = mockMvc.perform(requestBuilder);
        // THEN
        resultActions.andExpect(status().isForbidden());
        logger.info("shouldNotGetDocumentWhenIncorrectRoleProvided(): classificationLevel[{}], role[{}]",
                classificationLevel, role);
    }

    /**
     * Verifies that an unauthorized user without any role is forbidden
     * from accessing the document endpoint and receives an HTTP 403 Forbidden status.
     *
     * @param classificationLevel the classification level of the document
     * @throws Exception if an error occurs during the request or response validation
     */
    @ParameterizedTest
    @CsvSource({"official", "restricted", "confidential", "secret"})
    @DisplayName("🟥 should not get document when no role provided")
    void shouldNotGetDocumentWhenNoRoleProvided(String classificationLevel) throws Exception {
        // GIVEN
        final RequestBuilder requestBuilder =
                get("/api/document/%s".formatted(classificationLevel)).with(jwt());
        // WHEN
        final ResultActions resultActions = mockMvc.perform(requestBuilder);
        // THEN
        resultActions.andExpect(status().isForbidden());
        logger.info("shouldNotGetDocumentWhenNoRoleProvided(): classificationLevel[{}]",
                classificationLevel);
    }
}
