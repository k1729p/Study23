package kp.gateway.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.WebSession;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The gateway info controller.
 */
@RestController
public class GatewayInfoController {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());

    private static final Function<WebSession, String> USERNAME_FUN =
            webSession -> Optional.of(webSession)
                    .map(session -> session.getAttributes().get("SPRING_SECURITY_CONTEXT"))
                    .filter(SecurityContextImpl.class::isInstance)
                    .map(SecurityContextImpl.class::cast)
                    .map(SecurityContextImpl::getAuthentication)
                    .filter(OAuth2AuthenticationToken.class::isInstance)
                    .map(OAuth2AuthenticationToken.class::cast)
                    .map(token -> token.getPrincipal().getAttributes().get("preferred_username"))
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .orElse("unknown");

    private static final Function<WebSession, String> AUTHORITIES_FUN =
            webSession -> Optional.of(webSession)
                    .map(session -> session.getAttributes().get("SPRING_SECURITY_CONTEXT"))
                    .filter(SecurityContextImpl.class::isInstance)
                    .map(SecurityContextImpl.class::cast)
                    .map(SecurityContextImpl::getAuthentication)
                    .filter(OAuth2AuthenticationToken.class::isInstance)
                    .map(OAuth2AuthenticationToken.class::cast)
                    .map(OAuth2AuthenticationToken::getAuthorities)
                    .stream()
                    .flatMap(Collection::stream)
                    .map(GrantedAuthority::getAuthority)
                    .map("%n\t\t%s,"::formatted)
                    .sorted()
                    .collect(Collectors.joining());

    private static final Function<OAuth2AuthorizedClient, String> SCOPES_FUN =
            authorizedClient -> authorizedClient.getAccessToken().getScopes().stream()
                    .map("%n\t\t%s,"::formatted)
                    .sorted()
                    .collect(Collectors.joining());

    private static final String INFO_PAGE_FMT = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <title>Gateway Information</title>
                <style>
                    body {
                      background-color: wheat;
                    }
                    th, td {
                      border: 2px solid black;
                      font-size: 1.5em;
                      padding: 5px;
                    }
                </style>
            </head>
            <body>
            <h2>Gateway Information</h2>
            <table>
                <tr>
                    <td>Resource Server Information</td>
                    <td><a href="http://localhost:9091/api/info">
                        http://localhost:9091/api/info</a>
                    </td>
                </tr>
                <tr>
                    <td>Gateway</td>
                    <td><a href="http://localhost:9091/">
                        http://localhost:9091/</a>
                    </td>
                </tr>
            </table>
            <hr>
            <pre>%s<pre>
            <hr>
            </body>
            </html>
            """;

    /**
     * Displays information extracted from the web session and the authorized client.
     * <p>
     * A client is considered "authorized" when the End-User (Resource Owner)
     * has granted authorization to the client to access its protected resources.
     * </p>
     *
     * @param webSession       the {@link WebSession}
     * @param authorizedClient the {@link OAuth2AuthorizedClient}
     * @return the info page
     */
    @GetMapping("/info")
    public String info(WebSession webSession,
                       @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient) {

        logger.info("info():");
        return INFO_PAGE_FMT.formatted("""
                preferred username[%s],
                granted authorities%s
                scopes%s""".formatted(
                USERNAME_FUN.apply(webSession),
                AUTHORITIES_FUN.apply(webSession),
                SCOPES_FUN.apply(authorizedClient)));
    }
}