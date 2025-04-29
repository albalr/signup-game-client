package dk.dtu.compute.course02324.part4.consuming_rest.controller;

import org.springframework.web.client.RestClient;

public class RestApiService {
    private static final String BASE_URL = "http://localhost:8080";
    private final RestClient restClient;

    public RestApiService() {
        this.restClient = RestClient.builder()
                .baseUrl(BASE_URL)
                .build();
    }

    public RestClient getClient() {
        return restClient;
    }
}