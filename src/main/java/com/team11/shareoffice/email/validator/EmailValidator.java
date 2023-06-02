package com.team11.shareoffice.email.validator;

import com.team11.shareoffice.email.entity.Email;
import com.team11.shareoffice.email.repository.EmailRepository;
import com.team11.shareoffice.global.exception.CustomException;
import com.team11.shareoffice.global.util.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailValidator {

    private final EmailRepository emailRepository;

    //이메일이 DB에 존재 여부 확인
    public Email validateIsExistEmail(String email) {
        return emailRepository.findById(email).orElseThrow(() -> new CustomException(ErrorCode.WRONG_EMAIL));
    }

    //인증코드 일치 여부 확인
    public void validateCode(Email email, String code) {
        if (!email.getCode().equals(code)) {
            throw new CustomException(ErrorCode.WRONG_EMAIL_CODE);
        }
    }
}
