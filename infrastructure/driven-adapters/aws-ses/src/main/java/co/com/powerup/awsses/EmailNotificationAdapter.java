package co.com.powerup.awsses;

import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;
import co.com.powerup.model.emailnotification.gateways.EmailNotificationRepository;

import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class EmailNotificationAdapter implements EmailNotificationRepository {

    private final SesClient sesClient;

    @Value("${SPRING_SOURCE_EMAIL}")
    private String sourceEmail;


    @Override
        public Mono<Void> sendEmail(String to, String subject, String body) {
        return Mono.fromRunnable(() -> {
                Destination destination = Destination.builder()
                        .toAddresses(to)
                        .build();

                Message message = Message.builder()
                        .subject(Content.builder().data(subject).charset("UTF-8").build())
                        .body(Body.builder()
                                .text(Content.builder().data(body).charset("UTF-8").build())
                                .build())
                        .build();

                SendEmailRequest request = SendEmailRequest.builder()
                        .destination(destination)
                        .message(message)
                        .source(sourceEmail)
                        .build();

                sesClient.sendEmail(request);
        }).subscribeOn(Schedulers.boundedElastic()).then();
        }

}
