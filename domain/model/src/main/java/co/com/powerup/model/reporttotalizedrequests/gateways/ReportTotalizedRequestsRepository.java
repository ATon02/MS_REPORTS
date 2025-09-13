package co.com.powerup.model.reporttotalizedrequests.gateways;

import co.com.powerup.model.reporttotalizedrequests.ReportTotalizedRequests;
import reactor.core.publisher.Mono;

public interface ReportTotalizedRequestsRepository {
    Mono<ReportTotalizedRequests> save(ReportTotalizedRequests report);
}
