package co.com.powerup.model.emailnotification.gateways;

import reactor.core.publisher.Mono;

public interface EmailNotificationRepository {
    Mono<Void> sendEmail(String to, String subject ,String body);
}
