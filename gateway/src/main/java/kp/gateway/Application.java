package kp.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The application serves as an OAuth2 Client.
 * The client is responsible for:
 * <ol>
 * <li>acquiring tokens from the authorization server</li>
 * <li>authorizing its requests to resource servers</li>
 * </ol>
 * <p>
 * An API gateway is built on top of Spring Cloud Gateway.
 * It acts as an OAuth2 Client and OAuth2 Resource Server.
 * </p>
 */
@SpringBootApplication
public class Application {
    /**
     * The primary entry point for launching the application.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
