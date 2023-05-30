package com.team11.shareoffice.member.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberRequestDto {

    @NotBlank
    private String email;

    @Size(min = 2, max = 10)
    @NotBlank
    private String nickname;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@!%*?&()_])[A-Za-z\\d$@!%*?&()_]{8,15}$", message = "비밀번호는 최소 8자 이상 15자 이하이며 알파벳 대소문자, 숫자와 특수문자로 구성되어야 합니다.")
    private String password;

    @NotBlank
    private String passwordCheck;

//    @Getter
//    public static class login {
//        private String email;
//        private String password;
//    }
//
//    @Getter
//    public static class signout {
//        private String email;
//        private String password;
//    }

}
