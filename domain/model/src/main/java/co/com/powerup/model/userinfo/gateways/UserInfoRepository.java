package co.com.powerup.model.userinfo.gateways;

import co.com.powerup.model.userinfo.UserInfo;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

public interface UserInfoRepository {
    Mono<UserInfo> selfSearch(String authorization);
    Flux<UserInfo> findByRole(Long roleId);
}
