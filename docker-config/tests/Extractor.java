import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The access token extractor.
 * <p>
 * Actions:
 * </p>
 * <ul>
 * <li>Obtain a JSON response from the Keycloak server.</li>
 * <li>Using regular expressions extract the access token from JSON response.</li>
 * </ul>
 */
public class Extractor {
    private static final UnaryOperator<String> KEYCLOAK_URL_FUN =
            "http://%s:8080/realms/spring/protocol/openid-connect/token"::formatted;
    private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    private static final String AUTHORIZATION =
            "Basic " + Base64.getEncoder().encodeToString("backend-service:secret".getBytes());
    private static final Function<String, HttpRequest.BodyPublisher> BODY_PUBLISHER_FUN =
            user -> HttpRequest.BodyPublishers.ofString(("username=%s&password=miki&" +
                                                         "grant_type=password&client_id=spring-with-test-scope&" +
                                                         "client_secret=IWLSnakHG8aNTWNaWuSj0a11UY4lzxd9"
            ).formatted(user));
    private static final TrustManager[] TRUST_ALL_CERTS = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
    };
    private static final Pattern PATTERN = Pattern.compile("\"access_token\":\"([^\"]+)\"");

    /**
     * The primary entry point for launching the helper application.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {

        final Optional<String> userOpt = Optional.ofNullable(args).filter(arr -> arr.length > 0)
                .map(arr -> arr[0]);
        final String host = Optional.ofNullable(args).filter(arr -> arr.length > 1)
                .map(arr -> arr[1]).orElse("keycloak");
        final Optional<String> tokenOpt = userOpt.isPresent() ? extractToken(userOpt.get(), host) : Optional.empty();
        // Directly print the token to the console in a batch-parsable way
        if (tokenOpt.isPresent()) {
            System.out.printf("ACCESS_TOKEN=%s%n", tokenOpt.get());
        } else {
            System.err.println("main(): failed to extract access token");
            System.exit(1);
        }
    }

    /**
     * Extracts the access token from Keycloak response.
     *
     * @param user the user
     * @param host the host
     * @return the access token optional
     */
    private static Optional<String> extractToken(String user, String host) {

        final HttpRequest.BodyPublisher bodyPublisher = BODY_PUBLISHER_FUN.apply(user);
        String responseBody = "";
        try (HttpClient client = HttpClient.newBuilder().sslContext(createInsecureSslContext()).build()) {
            final HttpRequest request = HttpRequest.newBuilder().uri(new URI(KEYCLOAK_URL_FUN.apply(host)))
                    .header("Content-Type", CONTENT_TYPE).header("Authorization", AUTHORIZATION)
                    .POST(bodyPublisher).build();
            responseBody = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) {
            System.err.printf("extractToken(): exception[%s]%n", e.getMessage());
            System.exit(1);
        }
        return Optional.of(PATTERN.matcher(responseBody)).filter(Matcher::find)
                .map(matcher -> matcher.group(1));
    }

    /**
     * Creates insecure SSL context to bypass SSL certificate validation.
     * It is not recommended for production.
     *
     * @return the {@link SSLContext}
     */
    private static SSLContext createInsecureSslContext() {

        try {
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, TRUST_ALL_CERTS, new java.security.SecureRandom());
            return sslContext;
        } catch (Exception e) {
            System.err.printf("createInsecureSslContext(): exception[%s]%n", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}