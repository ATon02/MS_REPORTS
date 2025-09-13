package co.com.powerup.model.reporttotalizedrequests;
import lombok.Builder;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ReportTotalizedRequests {
    private String reportForStatus;
    private String reportType;
    private Double total;
    private String userGenerated;
    private Long idUserGenerated;
    private String emailUserGenerated;
    private LocalDateTime timestamp;
}
