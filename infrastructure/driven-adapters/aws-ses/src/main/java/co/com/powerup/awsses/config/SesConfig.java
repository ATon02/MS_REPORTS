package co.com.powerup.awsses.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import co.com.powerup.awsses.EmailNotificationAdapter;


@Configuration
public class SesConfig {

    @Bean
    public SesClient sesClient() {
        return SesClient.builder()
                .region(Region.US_EAST_1)
                .build();
    }

    @Bean
    public EmailNotificationAdapter sesEmailAdapter(SesClient sesClient) {
        return new EmailNotificationAdapter(sesClient);
    }
}
