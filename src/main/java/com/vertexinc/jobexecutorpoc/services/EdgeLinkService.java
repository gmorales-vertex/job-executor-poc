package com.vertexinc.jobexecutorpoc.services;

import com.vertexinc.jobexecutorpoc.models.Request;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class EdgeLinkService {
  private final WebClient client = WebClient.create("https://9vzq6.wiremockapi.cloud");

  public Mono<Request> getNextRequest(String clusterId) {
    return client
        .get()
        .uri("/clusters/{clusterId}/next-request", clusterId)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(Request.class);
  }
}
