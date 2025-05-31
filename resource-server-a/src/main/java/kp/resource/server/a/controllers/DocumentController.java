package kp.resource.server.a.controllers;

import kp.resource.server.a.model.ClassificationLevel;
import kp.resource.server.a.services.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final DocumentService documentService;

    /**
     * Parameterized constructor.
     *
     * @param documentService the {@link DocumentService} used for fetching documents.
     */
    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Retrieves the document classified as "Official".
     * <p>
     * This endpoint is secured and requires the user to have the {@code ROLE_OFFICIAL} role.
     * Users with this role can access documents that are classified as "Official".
     * </p>
     *
     * @param documentFormat the document format (text or HTML).
     * @return A string indicating successful access to the "Official" document.
     */
    @PreAuthorize("hasRole('ROLE_OFFICIAL')")
    @GetMapping({"/official", "/official/{documentFormat}"})
    public String getOfficialDocument(@PathVariable(required = false) String documentFormat) {

        final String document = documentService.findDocument(ClassificationLevel.OFFICIAL);
        logger.info("getOfficialDocument():");
        if ("html".equals(documentFormat)) {
            return ClassificationLevel.OFFICIAL.createHtmlPageWithDocument(document);
        } else {
            return document;
        }
    }

    /**
     * Retrieves the document classified as "Restricted".
     * <p>
     * This endpoint is secured and requires the user to have the {@code ROLE_RESTRICTED} role.
     * Users with this role can access documents that are classified as "Restricted".
     * </p>
     *
     * @param documentFormat the document format (text or HTML).
     * @return A string indicating successful access to the "Restricted" document.
     */
    @PreAuthorize("hasRole('ROLE_RESTRICTED')")
    @GetMapping({"/restricted", "/restricted/{documentFormat}"})
    public String getRestrictedDocument(@PathVariable(required = false) String documentFormat) {

        final String document = documentService.findDocument(ClassificationLevel.RESTRICTED);
        logger.info("getRestrictedDocument():");
        if ("html".equals(documentFormat)) {
            return ClassificationLevel.RESTRICTED.createHtmlPageWithDocument(document);
        } else {
            return document;
        }
    }

    /**
     * Retrieves the document classified as "Confidential".
     * <p>
     * This endpoint is secured and requires the user to have the {@code ROLE_CONFIDENTIAL} role.
     * Users with this role can access documents that are classified as "Confidential".
     * </p>
     *
     * @param documentFormat the document format (text or HTML).
     * @return A string indicating successful access to the "Confidential" document.
     */
    @PreAuthorize("hasRole('ROLE_CONFIDENTIAL')")
    @GetMapping({"/confidential", "/confidential/{documentFormat}"})
    public String getConfidentialDocument(@PathVariable(required = false) String documentFormat) {

        final String document = documentService.findDocument(ClassificationLevel.CONFIDENTIAL);
        logger.info("getConfidentialDocument():");
        if ("html".equals(documentFormat)) {
            return ClassificationLevel.CONFIDENTIAL.createHtmlPageWithDocument(document);
        } else {
            return document;
        }
    }

    /**
     * Retrieves the document classified as "Secret".
     * <p>
     * This endpoint is secured and requires the user to have the {@code ROLE_SECRET} role.
     * Users with this role can access documents that are classified as "Secret".
     * </p>
     *
     * @param documentFormat the document format (text or HTML).
     * @return A string indicating successful access to the "Secret" document.
     */
    @PreAuthorize("hasRole('ROLE_SECRET')")
    @GetMapping({"/secret", "/secret/{documentFormat}"})
    public String getSecretDocument(@PathVariable(required = false) String documentFormat) {

        final String document = documentService.findDocument(ClassificationLevel.SECRET);
        logger.info("getSecretDocument():");
        if ("html".equals(documentFormat)) {
            return ClassificationLevel.SECRET.createHtmlPageWithDocument(document);
        } else {
            return document;
        }
    }
}