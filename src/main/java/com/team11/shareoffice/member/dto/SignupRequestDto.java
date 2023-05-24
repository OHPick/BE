package com.team11.shareoffice.member.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequestDto {

    @NotBlank
    private String email;

    @Size(min = 2, max = 10,message = "[닉네임은 2글자~10자 사이로 입력해주세요]")
    @NotBlank
    private String nickname;

    @Size(min = 4, max = 15, message = "비밀번호는 8 이상, 15 이하만 가능합니다.")
    @NotBlank
    private String password;
}
