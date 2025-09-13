package co.com.powerup.dynamodb.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;

@DynamoDbBean
@NoArgsConstructor 
@AllArgsConstructor
@Builder(toBuilder = true)
public class ReportTotalizedRequestsEntity {
    @Builder.Default
    private String id = UUID.randomUUID().toString();
    private String reportForStatus;
    private String reportType;
    private Double total;
    private String userGenerated;
    private Long idUserGenerated;
    private String emailUserGenerated;
    private LocalDateTime timestamp;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("id")
    public String getId() {
        return id;
    }

    public void setId(String id) { this.id = id; }

    @DynamoDbAttribute("reportForStatus")
    public String getReportForStatus() { return reportForStatus; }
    public void setReportForStatus(String reportForStatus) { this.reportForStatus = reportForStatus; }

    @DynamoDbAttribute("reportType")
    public String getReportTypee() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }

    @DynamoDbAttribute("total")
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    @DynamoDbAttribute("userGenerated")
    public String getUserGenerated() { return userGenerated; }
    public void setUserGenerated(String userGenerated) { this.userGenerated = userGenerated; }

    @DynamoDbAttribute("idUserGenerated")
    public Long getIdUserGenerated() { return idUserGenerated; }
    public void setIdUserGenerated(Long idUserGenerated) { this.idUserGenerated = idUserGenerated; }

    @DynamoDbSecondarySortKey(indexNames = "idx_emailUserGenerated")
    @DynamoDbAttribute("emailUserGenerated")
    public String getEmailUserGenerated() { return emailUserGenerated; }
    public void setEmailUserGenerated(String emailUserGenerated) { this.emailUserGenerated = emailUserGenerated; }

    @DynamoDbAttribute("timestamp")
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

}
