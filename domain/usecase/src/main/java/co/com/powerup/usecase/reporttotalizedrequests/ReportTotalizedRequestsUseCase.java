package co.com.powerup.usecase.reporttotalizedrequests;

import java.time.LocalDateTime;

import co.com.powerup.model.reporttotalizedrequests.ReportTotalizedRequests;
import co.com.powerup.model.reporttotalizedrequests.gateways.ReportTotalizedRequestsRepository;
import co.com.powerup.model.totalizedrequests.TotalizedRequests;
import co.com.powerup.model.totalizedrequests.enums.TypeTotal;
import co.com.powerup.model.totalizedrequests.gateways.TotalizedRequestsRepository;
import co.com.powerup.model.userinfo.UserInfo;
import co.com.powerup.model.userinfo.gateways.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ReportTotalizedRequestsUseCase implements IReportTotalizedRequestsUseCase {

    private final ReportTotalizedRequestsRepository reportTotalizedRequestsRepository;
    private final UserInfoRepository userInfoRepository;
    private final TotalizedRequestsRepository totalizedRequestsRepository;

    @Override
    public Mono<ReportTotalizedRequests> generatedReport(TypeTotal typeTotal, Long statusId, String authorization) {
        Mono<TotalizedRequests> totalizedMono = totalizedRequestsRepository.getTotalizedRequests(typeTotal, statusId, authorization)
                        .switchIfEmpty(Mono.error(new IllegalArgumentException(
                                "No se pudo obtener totales desde servicio de solicitudes")));;
        Mono<UserInfo> userInfoMono = userInfoRepository
                .selfSearch(authorization)
                .switchIfEmpty(Mono.error(
                        new IllegalArgumentException(
                                "Información del usuario no encontrada en servicioo de Autenticacion")));

        return Mono.zip(totalizedMono, userInfoMono)
                .flatMap(tuple -> {
                    TotalizedRequests totalized = tuple.getT1();
                    UserInfo userInfo = tuple.getT2();

                    ReportTotalizedRequests report = ReportTotalizedRequests.builder()
                            .reportForStatus(totalized.getStatus())
                            .reportType(totalized.getType())
                            .total(totalized.getValue())
                            .userGenerated(userInfo.getName() + " " + userInfo.getLastName())
                            .idUserGenerated(userInfo.getIdUser())
                            .emailUserGenerated(userInfo.getEmail())
                            .timestamp(LocalDateTime.now())
                            .build();
                    return reportTotalizedRequestsRepository.save(report);
                });
    }
}
