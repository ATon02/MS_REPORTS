package co.com.powerup.authintercomrest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import co.com.powerup.model.userinfo.gateways.UserInfoRepository;
import lombok.extern.slf4j.Slf4j;
import co.com.powerup.model.userinfo.UserInfo;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Slf4j
@Component
public class AuthIntercomRestAdapter implements UserInfoRepository {

    private final WebClient webClient;

    @Value("${SPRING_INTERNAL_JOB_TOKEN_AUTH}")
    private String internalJobTokenAuth;

    public AuthIntercomRestAdapter(WebClient.Builder builder,
                                   @Value("${spring.intercom.auth.host}") String authHost) {
        this.webClient = builder.baseUrl(authHost).build();
    }

    @Override
    public Mono<UserInfo> selfSearch(String authorization) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/users/find-by-email/self")
                                             .build())
                .header("Authorization", authorization)
                .retrieve()
                .bodyToMono(UserInfo.class)
                .onErrorResume(e -> {
                    log.error("Error al invocar selfSearch: {}", e.getMessage());
                    return Mono.empty();
                });
    }

    @Override
    public Flux<UserInfo> findByRole(Long roleId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/users/find-by-role")
                                            .queryParam("roleId", roleId)
                                            .build())
                .header("X-Job-Token", internalJobTokenAuth)
                .retrieve()
                .bodyToFlux(UserInfo.class)
                .onErrorResume(e -> {
                    log.error("Error al invocar findByRole: {}", e.getMessage());
                    return Flux.empty();
                });
    }
}
