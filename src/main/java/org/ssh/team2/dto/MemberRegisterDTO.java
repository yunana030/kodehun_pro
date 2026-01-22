package org.ssh.team2.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberRegisterDTO {
    private Long id;
    private String username;
    private String name;
    private String password;
    private String passwordConfirm;
    private String email;
    private String mphone;
    private String role;
}
