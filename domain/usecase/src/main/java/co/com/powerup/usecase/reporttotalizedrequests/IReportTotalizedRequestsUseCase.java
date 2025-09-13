package co.com.powerup.usecase.reporttotalizedrequests;

import co.com.powerup.model.reporttotalizedrequests.ReportTotalizedRequests;
import co.com.powerup.model.totalizedrequests.enums.TypeTotal;
import reactor.core.publisher.Mono;

public interface IReportTotalizedRequestsUseCase {

    Mono<ReportTotalizedRequests> generatedReport(TypeTotal typeTotal, Long statusId, String authorization);

}
