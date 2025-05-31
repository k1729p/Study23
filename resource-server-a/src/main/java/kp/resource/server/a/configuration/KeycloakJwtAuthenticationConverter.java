package kp.resource.server.a.configuration;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * JWT Authentication Converter for Keycloak.
 * <p>
 * Converts a {@link Jwt} into an {@link AbstractAuthenticationToken}, including
 * extracting and adding Keycloak-specific roles as {@link GrantedAuthority}.
 * </p>
 */
public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        final Collection<GrantedAuthority> authorities = new JwtGrantedAuthoritiesConverter().convert(jwt);
        return new JwtAuthenticationToken(jwt, extractAndAddAuthorities(jwt, authorities));
    }

    /**
     * Extracts Keycloak-specific roles from the JWT claims and adds them to the existing authorities.
     *
     * @param jwt         the {@link Jwt} containing the claims
     * @param authorities the existing collection of {@link GrantedAuthority}
     * @return the updated collection of authorities
     */
    private Collection<GrantedAuthority> extractAndAddAuthorities(Jwt jwt, Collection<GrantedAuthority> authorities) {

        Optional.ofNullable(jwt)
                .map(Jwt::getClaims)
                .map(claims -> claims.get("realm_access"))
                .filter(Map.class::isInstance)
                .map(Map.class::cast)
                .filter(realmAccess -> realmAccess.containsKey("roles"))
                .map(realmAccess -> realmAccess.get("roles"))
                .filter(List.class::isInstance)
                .map(List.class::cast)
                .ifPresent(roles -> authorities.addAll(((List<?>) roles).stream()
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .filter(role -> role.startsWith("ROLE_"))
                        .map(SimpleGrantedAuthority::new)
                        .toList()));
        return authorities;
    }
}

