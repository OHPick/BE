package com.team11.shareoffice.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {
    @NotBlank
    private String email;

    @Size(min = 2, max = 10)
    @NotBlank
    private String nickname;

    private String password;

    @NotBlank
    private String passwordCheck;
}
