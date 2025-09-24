package co.com.powerup.usecase.reporttotalizedrequests;

import java.lang.reflect.Type;
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
import co.com.powerup.model.emailnotification.gateways.EmailNotificationRepository;

import java.util.List;

@RequiredArgsConstructor
public class ReportTotalizedRequestsUseCase implements IReportTotalizedRequestsUseCase {

    private final ReportTotalizedRequestsRepository reportTotalizedRequestsRepository;
    private final UserInfoRepository userInfoRepository;
    private final TotalizedRequestsRepository totalizedRequestsRepository;
    private final EmailNotificationRepository emailNotificationRepository;

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

    @Override
    public Mono<Void> sendReportEmail() {
        System.out.println("Ejecutando envio de email");
        Mono<List<UserInfo>> usersMono = userInfoRepository.findByRole(1L).collectList();
        Mono<List<TotalizedRequests>> totalsMono = totalizedRequestsRepository.totalsByStatus(2L);

        return Mono.zip(usersMono, totalsMono)
                .flatMap(tuple -> {
                    List<UserInfo> users = tuple.getT1();
                    List<TotalizedRequests> totals = tuple.getT2();

                    StringBuilder bodyBuilder = new StringBuilder();
                    bodyBuilder.append("Al dia de hoy se tiene los siguientes datos:\n");
                    for (TotalizedRequests total : totals) {
                        bodyBuilder.append(changeText(total.getType(), total.getValue()))
                                .append("\n");
                    }
                    String body = bodyBuilder.toString();
                    String subject = "REPORTE DIARIO DE SOLICITUDES APROBADAS";

                    return Mono.when(
                        users.stream()
                            .map(user -> emailNotificationRepository.sendEmail(user.getEmail(), subject, body))
                            .toList()
                    );
                });
    }

    private String changeText(String type, Double value) {
        if (TypeTotal.APPROVED_REQUESTS.name().equals(type)) {
            return "- Un total de: " + value.intValue() +" "+ TypeTotal.changeText(type);
        } else if (TypeTotal.APPROVED_AMOUNT.name().equals(type)) {
            return "- Un " + TypeTotal.changeText(type) + " de: $" + String.format("%.2f", value);
        }
        return type;
    }

}
