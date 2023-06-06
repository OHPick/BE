package com.team11.shareoffice.email.service;

import com.team11.shareoffice.email.dto.CodeRequestDto;
import com.team11.shareoffice.email.dto.EmailRequestDto;
import com.team11.shareoffice.email.entity.Email;
import com.team11.shareoffice.email.repository.EmailRepository;
import com.team11.shareoffice.email.validator.EmailValidator;
import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.global.exception.CustomException;
import com.team11.shareoffice.global.util.ErrorCode;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
@PropertySource("classpath:application-secret.yml")
public class EmailServiceImpl implements EmailService {

    private final EmailRepository emailRepository;
    private final JavaMailSender javaMailSender;
    private final EmailValidator emailValidator;

    @Value("${spring.mail.username}")
    private String id;

    private MimeMessage createMessage(String to, String code)throws Exception{
        log.info("보내는 대상 : "+ to);
        log.info("인증 번호 : "+ code);
        MimeMessage message = javaMailSender.createMimeMessage();

        message.addRecipients(Message.RecipientType.TO, to);//보내는 대상
        message.setSubject("Share Office 회원가입 인증 코드");//제목

        String msg="";
        msg+= "<div style='margin:20px;'>";
        msg+= "<h1> 안녕하세요 ShareOffice 입니다. </h1>";
        msg+= "<br>";
        msg+= "<p>아래 인증코드를 복사해 회원가입 화면에 입력해주세요<p>";
        msg+= "<br>";
        msg+= "<p>감사합니다.<p>";
        msg+= "<br>";
        msg+= "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msg+= "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>";
        msg+= "<div style='font-size:130%'>";
        msg+= "CODE : <strong>";
        msg+= code+"</strong><div><br/> ";
        msg+= "</div>";
        message.setText(msg, "utf-8", "html");//내용
        message.setFrom(new InternetAddress( id,"test"));//보내는 사람

        return message;
    }

    public static String createKey() {
        StringBuilder key = new StringBuilder();
        SecureRandom rnd = new SecureRandom();

        for (int i = 0; i < 8; i++) { // 인증코드 8자리
            int index = rnd.nextInt(3); // 0~2 까지 랜덤

            switch (index) {
                case 0:
                    key.append((char) ((rnd.nextInt(26)) + 97));
                    //  a~z  (ex. 1+97=98 => (char)98 = 'b')
                    break;
                case 1:
                    key.append((char) ((rnd.nextInt(26)) + 65));
                    //  A~Z
                    break;
                case 2:
                    key.append((rnd.nextInt(10)));
                    // 0~9
                    break;
            }
        }
        return key.toString();
    }
    @Override
    public ResponseDto<?> sendMessage(EmailRequestDto requestDto)throws Exception {
        String code = createKey();
        emailValidator.validateEmailPattern(requestDto.getEmail());
        Email email = Email.saveEmail(requestDto);
        MimeMessage message = createMessage(email.getEmail(),code);
        try{//예외처리
            javaMailSender.send(message);
        }catch(MailException e){
            e.printStackTrace();
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
        }
        email.updateCode(code);
        emailRepository.save(email);
        return ResponseDto.setSuccess(code);
    }


    /*
        DB에 Email테이블에서 인증코드와 일치하는지 체크,
        일치여부와 상관없이 해당 데이터는 무조건 삭제
     */
    @Override
    public ResponseDto<?> codeCheck(CodeRequestDto requestDto)throws Exception {
        Email email = emailValidator.validateIsExistEmail(requestDto.getEmail());
        emailValidator.validateCode(email,requestDto.getCode());
        email.updateChecked(true);
        return ResponseDto.setSuccess(null);
    }
}
