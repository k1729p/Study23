package kp.resource.server.a.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The resource server info controller.
 */
@RestController
public class ResourceServerInfoController {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());
    private static final Function<JwtAuthenticationToken, String> USER_FUN =
            token -> token.getToken()
                    .getClaims().entrySet().stream()
                    .filter(entry -> "preferred_username".equals(entry.getKey()))
                    .map(Map.Entry::getValue).filter(String.class::isInstance).map(String.class::cast)
                    .findFirst().orElse("");
    private static final Function<JwtAuthenticationToken, String> GRANTED_AUTHORITIES_FUN =
            token -> token.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority).map("%n\t\t%s,"::formatted)
                    .sorted().collect(Collectors.joining());
    private static final Function<JwtAuthenticationToken, String> JWT_CLAIMS_FUN =
            token -> token.getTokenAttributes().entrySet().stream()
                    .map(entry -> "%n\t\t\"%s\" : \"%s\",".formatted(
                            entry.getKey(), entry.getValue()))
                    .sorted().collect(Collectors.joining());
    private static final String INFO_PAGE_FMT = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <title>Resource Server Information</title>
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
            <h2>Resource Server Information</h2>
            <table>
                <tr>
                    <td>Gateway Information</td>
                    <td><a href="http://localhost:9091/info">
                        http://localhost:9091/info</a>
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
     * Displays information extracted from the JWT authentication token.
     * <ul>
     * <li>
     * <a href="https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html#oauth2resourceserver-jwt-architecture">
     * How JWT Authentication Works</a>
     * </li>
     * <li>
     * <a href="https://datatracker.ietf.org/doc/html/rfc7519#section-4.1">
     * Registered Claim Names
     * </a>
     * </li>
     * </ul>
     *
     * @param jwtAuthenticationToken the {@link JwtAuthenticationToken}
     * @return the result
     */
    @GetMapping("/api/info")
    public String info(JwtAuthenticationToken jwtAuthenticationToken) {

        logger.info("info():");
        return INFO_PAGE_FMT.formatted("""
                preferred username[%s],
                granted authorities %s
                JWT claims %s
                JWT authentication token name[%s]""".formatted(
                USER_FUN.apply(jwtAuthenticationToken),
                GRANTED_AUTHORITIES_FUN.apply(jwtAuthenticationToken),
                JWT_CLAIMS_FUN.apply(jwtAuthenticationToken),
                jwtAuthenticationToken.getName()));
    }
}