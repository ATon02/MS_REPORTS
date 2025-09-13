package co.com.powerup.api.reporttotalizedrequests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.powerup.api.mapper.ReportTotalizedRequestsDTOMapper;
import co.com.powerup.model.totalizedrequests.enums.TypeTotal;
import co.com.powerup.usecase.reporttotalizedrequests.IReportTotalizedRequestsUseCase;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportTotalizedRequestsHandler {
    
    private  final IReportTotalizedRequestsUseCase reportTotalizedRequestsUseCase;
    private final ReportTotalizedRequestsDTOMapper reportTotalizedRequestsDTOMapper;

    public Mono<ServerResponse> getReportTotalizedRequests(ServerRequest serverRequest) {
        log.info("➡️ Entró al handler getReportTotalizedRequests() de ReportTotalizedRequestsHandler");
        TypeTotal type = serverRequest.queryParam("type")
                .map(TypeTotal::valueOf)
                .orElseThrow(() -> new IllegalArgumentException("El parámetro 'type' es requerido"));
        Long statusId = serverRequest.queryParam("statusId")
                .map(Long::parseLong)
                .orElseThrow(() -> new IllegalArgumentException("El parámetro 'statusId' es requerido"));
        String token = serverRequest.headers().firstHeader("Authorization");
        return reportTotalizedRequestsUseCase.generatedReport(type, statusId,token)
                .map(reportTotalizedRequestsDTOMapper::toResponse)
                .flatMap(report -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(report));
    }

}
