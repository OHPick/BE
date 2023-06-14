//package com.team11.shareoffice.email.validator;
//
//import com.team11.shareoffice.email.entity.Email;
//import com.team11.shareoffice.email.repository.EmailRepository;
//import com.team11.shareoffice.global.exception.CustomException;
//import com.team11.shareoffice.global.util.ErrorCode;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//@Component
//@RequiredArgsConstructor
//public class EmailValidator {
//
//    private final EmailRepository emailRepository;
//
//    //이메일 패턴 확인
//    public void validateEmailPattern(String email) {
//        String emailPattern = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";
//        Pattern pattern = Pattern.compile(emailPattern);
//        Matcher matcher = pattern.matcher(email);
//        if (!matcher.matches()) {
//            throw new CustomException(ErrorCode.INVALID_EMAIL_PATTERN);
//        }
//
//    }
//
//    //이메일이 DB에 존재 여부 확인
//    public Email validateIsExistEmail(String email) {
//        return emailRepository.findById(email).orElseThrow(() -> new CustomException(ErrorCode.WRONG_EMAIL));
//    }
//
//    //인증코드 일치 여부 확인
//    public void validateCode(Email email, String code) {
//        if (!email.getCode().equals(code)) {
//            throw new CustomException(ErrorCode.WRONG_EMAIL_CODE);
//        }
//    }
//}
