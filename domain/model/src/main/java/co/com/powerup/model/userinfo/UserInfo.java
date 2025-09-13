package co.com.powerup.model.userinfo;
import lombok.Builder;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserInfo {

    private Long idUser;
    private String name;
    private String lastName;
    private String email;
    private String identityDocument;
    private String phone;
    private Long roleId;
    private Double baseSalary;
    private LocalDate dateOfBirth;
    private String address;
}
