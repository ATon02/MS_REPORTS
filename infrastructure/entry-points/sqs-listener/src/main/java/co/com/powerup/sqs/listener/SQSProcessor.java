package co.com.powerup.sqs.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import co.com.powerup.usecase.reporttotalizedrequests.IReportTotalizedRequestsUseCase;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class SQSProcessor implements Function<Message, Mono<Void>> {
    
    private final IReportTotalizedRequestsUseCase reportTotalizedRequestsUseCase;

    @Override
    public Mono<Void> apply(Message message) {
        log.info("Mensaje recibido: {}", message.body());
        return reportTotalizedRequestsUseCase.sendReportEmail()
                .doOnError(e -> log.error("Error enviando reporte: ", e))
                .onErrorResume(e -> Mono.empty());
    }
}
