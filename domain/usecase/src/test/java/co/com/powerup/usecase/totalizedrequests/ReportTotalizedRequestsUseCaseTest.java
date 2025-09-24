package co.com.powerup.usecase.totalizedrequests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.com.powerup.model.emailnotification.gateways.EmailNotificationRepository;
import co.com.powerup.model.reporttotalizedrequests.ReportTotalizedRequests;
import co.com.powerup.model.reporttotalizedrequests.gateways.ReportTotalizedRequestsRepository;
import co.com.powerup.model.totalizedrequests.TotalizedRequests;
import co.com.powerup.model.totalizedrequests.enums.TypeTotal;
import co.com.powerup.model.totalizedrequests.gateways.TotalizedRequestsRepository;
import co.com.powerup.model.userinfo.UserInfo;
import co.com.powerup.model.userinfo.gateways.UserInfoRepository;
import co.com.powerup.usecase.reporttotalizedrequests.ReportTotalizedRequestsUseCase;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import reactor.core.publisher.Flux;
import org.mockito.Mockito;
import org.mockito.ArgumentCaptor;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class ReportTotalizedRequestsUseCaseTest {

    @Mock
    private TotalizedRequestsRepository totalizedRequestsRepository;
    @Mock
    private UserInfoRepository userInfoRepository;
    @Mock
    private ReportTotalizedRequestsRepository reportTotalizedRequestsRepository;
    @Mock    
    private EmailNotificationRepository emailNotificationRepository;

    @InjectMocks
    private ReportTotalizedRequestsUseCase reportTotalizedRequestsUseCase;

    @BeforeEach
    void setUp() {

    }

    @Test
    void generatedReport_shouldSaveReportSuccessfully() {
        TotalizedRequests totalized = new TotalizedRequests();
        totalized.setStatus("APPROVED");
        totalized.setType("APPROVED_AMOUNT");
        totalized.setValue(123.45);

        UserInfo userInfo = new UserInfo();
        userInfo.setName("Anderson");
        userInfo.setLastName("Tonusco");
        userInfo.setIdUser(1L);
        userInfo.setEmail("anderson@example.com");

        when(totalizedRequestsRepository.getTotalizedRequests(any(), any(), any()))
                .thenReturn(Mono.just(totalized));
        when(userInfoRepository.selfSearch(any()))
                .thenReturn(Mono.just(userInfo));
        when(reportTotalizedRequestsRepository.save(any()))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        Mono<ReportTotalizedRequests> result = reportTotalizedRequestsUseCase.generatedReport(
                TypeTotal.APPROVED_AMOUNT, 1L, "auth-token");

        StepVerifier.create(result)
                .assertNext(report -> {
                    assert report.getReportForStatus().equals("APPROVED");
                    assert report.getReportType().equals("APPROVED_AMOUNT");
                    assert report.getTotal().equals(123.45);
                    assert report.getUserGenerated().equals("Anderson Tonusco");
                    assert report.getIdUserGenerated().equals(1L);
                    assert report.getEmailUserGenerated().equals("anderson@example.com");
                    assert report.getTimestamp() != null;
                })
                .verifyComplete();
    }

    @Test
    void generatedReport_shouldFailWhenTotalizedRequestsIsEmpty() {
        when(totalizedRequestsRepository.getTotalizedRequests(any(), any(), any()))
                .thenReturn(Mono.empty());
        when(userInfoRepository.selfSearch(any()))
                .thenReturn(Mono.just(new UserInfo()));

        Mono<ReportTotalizedRequests> result = reportTotalizedRequestsUseCase.generatedReport(
                TypeTotal.APPROVED_AMOUNT, 1L, "auth-token");

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("No se pudo obtener totales desde servicio de solicitudes"))
                .verify();
    }

    @Test
    void generatedReport_shouldFailWhenUserInfoIsEmpty() {
        TotalizedRequests totalized = new TotalizedRequests();
        when(totalizedRequestsRepository.getTotalizedRequests(any(), any(), any()))
                .thenReturn(Mono.just(totalized));
        when(userInfoRepository.selfSearch(any()))
                .thenReturn(Mono.empty());

        Mono<ReportTotalizedRequests> result = reportTotalizedRequestsUseCase.generatedReport(
                TypeTotal.APPROVED_AMOUNT, 1L, "auth-token");

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage()
                                .equals("Información del usuario no encontrada en servicioo de Autenticacion"))
                .verify();
    }

    @Test
    void sendReportEmail_usersAndTotals_emailsSentWithCorrectBody() {
        UserInfo user = new UserInfo();
        user.setEmail("user@test.com");
        user.setName("User");

        TotalizedRequests total1 = new TotalizedRequests();
        total1.setType("APPROVED_REQUESTS");
        total1.setValue(3.0);

        TotalizedRequests total2 = new TotalizedRequests();
        total2.setType("APPROVED_AMOUNT");
        total2.setValue(1500.0);

        Mockito.when(userInfoRepository.findByRole(1L)).thenReturn(Flux.just(user));
        Mockito.when(totalizedRequestsRepository.totalsByStatus(2L))
            .thenReturn(Mono.just(List.of(total1, total2)));
        Mockito.when(emailNotificationRepository.sendEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
            .thenReturn(Mono.empty());

        Mono<Void> result = reportTotalizedRequestsUseCase.sendReportEmail();

        StepVerifier.create(result).verifyComplete();

        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(emailNotificationRepository).sendEmail(
                emailCaptor.capture(),
                subjectCaptor.capture(),
                bodyCaptor.capture()
        );

        assertEquals("user@test.com", emailCaptor.getValue());
        assertEquals("REPORTE DIARIO DE SOLICITUDES APROBADAS", subjectCaptor.getValue());
        String body = bodyCaptor.getValue();
        assertTrue(body.contains("Al dia de hoy se tiene los siguientes datos:"));
        assertTrue(body.contains("- Un total de: 3 Solicitudes aprobadas"));

    }

    @Test
    void sendReportEmail_noUsers_noEmailsSent() {
        Mockito.when(userInfoRepository.findByRole(1L)).thenReturn(Flux.empty());
        Mockito.when(totalizedRequestsRepository.totalsByStatus(2L)).thenReturn(Mono.just(List.of()));

        Mono<Void> result = reportTotalizedRequestsUseCase.sendReportEmail();

        StepVerifier.create(result).verifyComplete();

        Mockito.verify(emailNotificationRepository, Mockito.never())
            .sendEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void sendReportEmail_noTotals_sendsEmailWithEmptyTotals() {
        UserInfo user = new UserInfo();
        user.setEmail("user@test.com");
        user.setName("User");

        Mockito.when(userInfoRepository.findByRole(1L)).thenReturn(Flux.just(user));
        Mockito.when(totalizedRequestsRepository.totalsByStatus(2L)).thenReturn(Mono.just(List.of()));
        Mockito.when(emailNotificationRepository.sendEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
            .thenReturn(Mono.empty());

        Mono<Void> result = reportTotalizedRequestsUseCase.sendReportEmail();

        StepVerifier.create(result).verifyComplete();

        Mockito.verify(emailNotificationRepository, Mockito.times(1))
            .sendEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

}
