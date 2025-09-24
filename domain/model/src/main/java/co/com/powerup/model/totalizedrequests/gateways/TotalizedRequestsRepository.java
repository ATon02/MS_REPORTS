package co.com.powerup.model.totalizedrequests.gateways;

import java.util.List;

import co.com.powerup.model.totalizedrequests.TotalizedRequests;
import co.com.powerup.model.totalizedrequests.enums.TypeTotal;
import reactor.core.publisher.Mono;

public interface TotalizedRequestsRepository {

    Mono<TotalizedRequests> getTotalizedRequests(TypeTotal typeTotal, Long statusId, String authorization);
    Mono<List<TotalizedRequests>> totalsByStatus(Long statusId);
}
