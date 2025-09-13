package co.com.powerup.api.dtos.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ReportTotalizedRequestsResponse {
    private String reportForStatus;
    private String reportType;
    private Double total;
    private String userGenerated;
    private LocalDateTime timestamp;
}
