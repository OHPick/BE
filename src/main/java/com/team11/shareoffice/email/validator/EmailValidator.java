package com.team11.shareoffice.email.validator;

import com.team11.shareoffice.email.entity.Email;
import com.team11.shareoffice.email.repository.EmailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class EmailValidator {

    private final EmailRepository emailRepository;

    //이메일이 DB에 존재 여부 확인
    public Email validateEmail(String email) {
        return emailRepository.findById(email).orElseThrow(() -> new NoSuchElementException("인증을 요청한 이메일이 아닙니다."));
    }

    //인증코드 일치 여부 확인
    public void validateCode(Email email, String code) {
        if (!email.getCode().equals(code)) {
            throw new IllegalArgumentException("인증 코드가 일치하지 않습니다.");
        }
    }
}
