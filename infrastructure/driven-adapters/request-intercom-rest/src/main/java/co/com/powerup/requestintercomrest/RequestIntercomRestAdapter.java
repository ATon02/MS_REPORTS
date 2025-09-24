package co.com.powerup.requestintercomrest;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import co.com.powerup.model.totalizedrequests.TotalizedRequests;
import co.com.powerup.model.totalizedrequests.enums.TypeTotal;
import co.com.powerup.model.totalizedrequests.gateways.TotalizedRequestsRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class RequestIntercomRestAdapter implements TotalizedRequestsRepository {

    private final WebClient webClient;
    @Value("${SPRING_INTERNAL_JOB_TOKEN_REQUEST}")
    private String internalJobTokenRequest;

    public RequestIntercomRestAdapter(WebClient.Builder builder,
                                   @Value("${spring.intercom.requests.host}") String authHost) {
        this.webClient = builder.baseUrl(authHost).build();
    }

    @Override
    public Mono<TotalizedRequests> getTotalizedRequests(TypeTotal typeTotal, Long statusId, String authorization) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/request/total")
                                             .queryParam("type", typeTotal)
                                             .queryParam("statusId", statusId)
                                             .build())
                .header("Authorization", authorization)
                .retrieve()
                .bodyToMono(TotalizedRequests.class)
                .onErrorResume(e -> {
                    log.error("Error al invocar getTotalizedRequests: {}", e.getMessage());
                    return Mono.empty();
                });
    }

    @Override
    public Mono<List<TotalizedRequests>> totalsByStatus(Long statusId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/request/totals/internal")
                                            .queryParam("statusId", statusId)
                                            .build())
                .header("X-Job-Token", internalJobTokenRequest)
                .retrieve()
                .bodyToFlux(TotalizedRequests.class)
                .collectList()
                .onErrorResume(e -> {
                    log.error("Error al invocar totalsByStatus: {}", e.getMessage());
                    return Mono.just(List.of());
                });
    }

}
