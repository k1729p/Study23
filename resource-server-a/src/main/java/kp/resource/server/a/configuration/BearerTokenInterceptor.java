package kp.resource.server.a.configuration;

import jakarta.annotation.Nonnull;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

/**
 * The Bearer Token Interceptor to include the Bearer token in the request headers.
 */
@Component
public class BearerTokenInterceptor implements ClientHttpRequestInterceptor {

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public ClientHttpResponse intercept(HttpRequest request,
                                        @Nonnull byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {

        final String bearerToken = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(JwtAuthenticationToken.class::isInstance)
                .map(JwtAuthenticationToken.class::cast)
                .map(auth -> Optional.ofNullable(auth.getCredentials())
                        .filter(Jwt.class::isInstance)
                        .map(Jwt.class::cast)
                        .map(jwt -> "Bearer " + jwt.getTokenValue())
                        .orElseThrow(() -> new IllegalStateException("No JWT token found in security context")))
                .orElseThrow(() -> new IllegalStateException("No JWT authentication token found in security context"));
        request.getHeaders().add("Authorization", bearerToken);
        return execution.execute(request, body);
    }
}