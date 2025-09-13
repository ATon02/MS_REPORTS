package co.com.powerup.model.totalizedrequests;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TotalizedRequests {
    private String status;
    private String type;
    private Double value;
}
