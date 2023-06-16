package com.team11.shareoffice.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {
    @NotBlank
    private String email;

    @NotBlank
    private String nickname;

    private String password;

    @NotBlank
    private String passwordCheck;
}
