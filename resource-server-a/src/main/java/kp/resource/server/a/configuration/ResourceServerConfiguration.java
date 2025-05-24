package kp.resource.server.a.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.client.RestClient;

/**
 * The security configuration.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ResourceServerConfiguration {

    /**
     * Produces the {@link SecurityWebFilterChain} bean.
     *
     * @param httpSecurity the {@link ServerHttpSecurity}
     * @return the {@link SecurityWebFilterChain} bean
     * @throws Exception if an error occurs
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity.authorizeHttpRequests(registry ->
                        registry.anyRequest().authenticated())
                .oauth2ResourceServer(serverConfigurer ->
                        serverConfigurer.jwt(jwtConfigurer ->
                                jwtConfigurer.jwtAuthenticationConverter(new KeycloakJwtAuthenticationConverter())))
                .build();
    }

    /**
     * Produces the {@link RestClient} bean.
     *
     * @param bearerTokenInterceptor the {@link BearerTokenInterceptor}
     * @return the {@link RestClient}
     */
    @Bean
    public RestClient restClient(BearerTokenInterceptor bearerTokenInterceptor) {

        return RestClient.builder().requestInterceptor(bearerTokenInterceptor).build();
    }
}