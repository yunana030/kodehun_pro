package org.ssh.team2.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class MyPageDTO {
    private Long id;

    @NotBlank @Size(min=2, max=100)
    private String name;

    @Email
    private String email;

    @Size(max=30)
    private String phone; // DB mphone와 매핑(서비스에서 설정)

}








