package kp.resource.server.b.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

/**
 * REST controller for managing document classifications and access levels.
 * This controller provides endpoints for retrieving documents
 * based on their classification levels, such as Official, Restricted, Confidential, and Secret.
 */
@RestController
@RequestMapping("/api/document")
public class DocumentController {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());
    /**
     * The official document.
     */
    static final String OFFICIAL_DOCUMENT = "The official information.";
    /**
     * The restricted document.
     */
    static final String RESTRICTED_DOCUMENT = "The restricted information.";
    /**
     * The confidential document.
     */
    static final String CONFIDENTIAL_DOCUMENT = "The confidential information.";
    /**
     * The secret document.
     */
    static final String SECRET_DOCUMENT = "The secret information.";

    /**
     * Retrieves the document classified as "Official".
     * <p>
     * This endpoint is secured and requires the user to have the {@code ROLE_OFFICIAL} role.
     * Users with this role can access documents that are classified as "Official".
     * </p>
     *
     * @return A string indicating successful access to the "Official" document.
     */
    @PreAuthorize("hasRole('ROLE_OFFICIAL')")
    @GetMapping("/official")
    public String getOfficialDocument() {

        logger.info("getOfficialDocument(): document[{}]", OFFICIAL_DOCUMENT);
        return OFFICIAL_DOCUMENT;
    }

    /**
     * Retrieves the document classified as "Restricted".
     * <p>
     * This endpoint is secured and requires the user to have the {@code ROLE_RESTRICTED} role.
     * Users with this role can access documents that are classified as "Restricted".
     * </p>
     *
     * @return A string indicating successful access to the "Restricted" document.
     */
    @PreAuthorize("hasRole('ROLE_RESTRICTED')")
    @GetMapping("/restricted")
    public String getRestrictedDocument() {

        logger.info("getRestrictedDocument(): document[{}]", RESTRICTED_DOCUMENT);
        return RESTRICTED_DOCUMENT;
    }

    /**
     * Retrieves the document classified as "Confidential".
     * <p>
     * This endpoint is secured and requires the user to have the {@code ROLE_CONFIDENTIAL} role.
     * Users with this role can access documents that are classified as "Confidential".
     * </p>
     *
     * @return A string indicating successful access to the "Confidential" document.
     */
    @PreAuthorize("hasRole('ROLE_CONFIDENTIAL')")
    @GetMapping("/confidential")
    public String getConfidentialDocument() {

        logger.info("getConfidentialDocument(): document[{}]", CONFIDENTIAL_DOCUMENT);
        return CONFIDENTIAL_DOCUMENT;
    }

    /**
     * Retrieves the document classified as "Secret".
     * <p>
     * This endpoint is secured and requires the user to have the {@code ROLE_SECRET} role.
     * Users with this role can access documents that are classified as "Secret".
     * </p>
     *
     * @return A string indicating successful access to the "Secret" document.
     */
    @PreAuthorize("hasRole('ROLE_SECRET')")
    @GetMapping("/secret")
    public String getSecretDocument() {

        logger.info("getSecretDocument(): document[{}]", SECRET_DOCUMENT);
        return SECRET_DOCUMENT;
    }
}