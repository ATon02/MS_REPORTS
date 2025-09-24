package co.com.powerup.sqs.listener;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import co.com.powerup.sqs.listener.helper.SQSListener;

@Log4j2
@Component
@RequiredArgsConstructor
public class SQSListenerRunner {

    private final SQSListener sqsListener;

    @PostConstruct
    public void init() {
        log.info("Iniciando listener de SQS...");
        sqsListener.start();
    }
}

