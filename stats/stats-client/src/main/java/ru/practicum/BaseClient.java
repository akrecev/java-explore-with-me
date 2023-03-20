package ru.practicum;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<List<ViewStatsDto>> get(String path, Map<String, Object> parameters) {
        return makeAndSendRequestGet(path, parameters);
    }

    protected <T> ResponseEntity<Object> post(String path, T body) {
        return makeAndSendRequest(path, body);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(String path, T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body);
        ResponseEntity<Object> statsResponse;
        try {
            statsResponse = rest.exchange(path, HttpMethod.POST, requestEntity, Object.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(statsResponse);
    }

    private <T> ResponseEntity<List<ViewStatsDto>> makeAndSendRequestGet(String path,
                                                                      Map<String, Object> parameters) {

        HttpEntity<T> requestEntity = new HttpEntity<>(defaultHeaders());
        ResponseEntity<List<ViewStatsDto>> statsResponse;
        try {
            statsResponse = rest.exchange(path, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {},
                    parameters);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(null);
        }
        return prepareGatewayResponseGet(statsResponse);
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());
        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }

    private static ResponseEntity<List<ViewStatsDto>> prepareGatewayResponseGet(ResponseEntity<List<ViewStatsDto>> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());
        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }
        return responseBuilder.build();
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}