package com.team11.shareoffice.member.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
public class SignupRequestDto {

    @NotBlank
    private String email;

    @Size(min = 2, max = 10)
    @NotBlank
    private String nickname;

    @Size(min = 4, max = 15)
    @NotBlank
    private String password;

    @NotBlank
    private String passwordCheck;
}
