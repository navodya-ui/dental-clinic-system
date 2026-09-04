package lk.icbt.dentalclinic.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Thin HTTP client used by every JavaFX controller to call the REST API
 * (AppointmentResource / BillResource) started by ServerLauncher. Keeping
 * this in one place means the UI never talks to the database or service
 * layer directly - it only ever speaks HTTP/JSON, which is what makes
 * the presentation tier and the web-service tier genuinely separate
 * processes rather than just separate classes.
 */
public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080/api";

    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    public ApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public ApiResponse post(String path, Object body) throws IOException, InterruptedException {
        String json = mapper.writeValueAsString(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return new ApiResponse(response.statusCode(), response.body());
    }

    /**
     * POST with no request body - for endpoints like /bills/{appointmentNo}
     * where every parameter is already in the URL path.
     */
    public ApiResponse post(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return new ApiResponse(response.statusCode(), response.body());
    }

    public ApiResponse get(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return new ApiResponse(response.statusCode(), response.body());
    }

    public ApiResponse put(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return new ApiResponse(response.statusCode(), response.body());
    }

    /** Simple holder for an HTTP status code + raw JSON body. */
    public static class ApiResponse {
        public final int statusCode;
        public final String body;

        public ApiResponse(int statusCode, String body) {
            this.statusCode = statusCode;
            this.body = body;
        }

        public boolean isSuccess() {
            return statusCode >= 200 && statusCode < 300;
        }
    }
}