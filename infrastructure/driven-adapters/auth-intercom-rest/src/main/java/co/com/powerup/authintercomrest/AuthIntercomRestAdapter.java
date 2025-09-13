package co.com.powerup.authintercomrest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import co.com.powerup.model.userinfo.gateways.UserInfoRepository;
import co.com.powerup.model.userinfo.UserInfo;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AuthIntercomRestAdapter implements UserInfoRepository {

    private final WebClient webClient;

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
                .bodyToMono(UserInfo.class);
    }
}
