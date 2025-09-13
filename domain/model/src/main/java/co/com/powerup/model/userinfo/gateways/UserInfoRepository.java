package co.com.powerup.model.userinfo.gateways;

import co.com.powerup.model.userinfo.UserInfo;
import reactor.core.publisher.Mono;

public interface UserInfoRepository {
    Mono<UserInfo> selfSearch(String authorization);
}
