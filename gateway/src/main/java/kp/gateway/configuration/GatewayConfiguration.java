package kp.gateway.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * The security configuration class.
 * <p>
 * Configures the security settings for the gateway application.
 * </p>
 */
@Configuration
@EnableWebFluxSecurity
public class GatewayConfiguration {

    /**
     * Produces the {@link SecurityWebFilterChain} bean.
     * <p>
     * Configures the security filter chain for the application, including authentication and CSRF settings.
     * </p>
     *
     * @param serverHttpSecurity the {@link ServerHttpSecurity} instance to configure
     * @return the configured {@link SecurityWebFilterChain} bean
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity serverHttpSecurity) {

        return serverHttpSecurity
                .authorizeExchange(exchange ->
                        exchange.anyExchange().authenticated())
                .oauth2Login(withDefaults())
                .oauth2ResourceServer(resourceServer ->
                        resourceServer.jwt(Customizer.withDefaults()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }
}