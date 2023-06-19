package com.team11.shareoffice.member.validator;

import com.team11.shareoffice.global.exception.CustomException;
import com.team11.shareoffice.global.service.RedisService;
import com.team11.shareoffice.global.util.ErrorCode;
import com.team11.shareoffice.member.dto.SignupRequestDto;
import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.member.repository.MemberRepository;
import com.team11.shareoffice.reservation.entity.Reservation;
import com.team11.shareoffice.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class MemberValidator {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReservationRepository reservationRepository;

    //회원가입 - 비밀번호 확인 일치 여부
    public void validatePasswordCheck(SignupRequestDto requestDto) {
        if (!Objects.equals(requestDto.getPassword(), requestDto.getPasswordCheck())) {
            throw new CustomException(ErrorCode.NOT_SAME_PASSWORD);
        }
        //"비밀번호는 8-15자리, 최소 하나의 영어 대소문자, 숫자, 특수문자(@$!%*?&()_)를 포함해야 합니다."
        String passwordPattern = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&()_])[A-Za-z\\d@$!%*?&()_]{8,15}$";
        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher matcher = pattern.matcher(requestDto.getPassword());

        if (!matcher.matches()) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD_PATTERN);
        }
    }

    //회원가입 - 이메일 중복 검사
    public void validateEmailOverlapped(String email) {
        Optional<Member> foundByEmail = memberRepository.findByEmail(email);
        if (foundByEmail.isPresent()) {
            if (null != foundByEmail.get().getKakaoId()) {
                throw new CustomException(ErrorCode.EXIST_KAKAO_EMAIL);
            }

            if (!foundByEmail.get().isDelete()) {
                throw new CustomException(ErrorCode.EXIST_EMAIL);
            }
        }
    }

    //회원가입 - 닉네임 중복 검사
    public void validateNickname(String nickname) {
        if(nickname.length()<2 || nickname.length()>10){
            throw new CustomException(ErrorCode.INVALID_NICKNAME_PATTERN);
        }

        Optional<Member> foundByUsername = memberRepository.findByNickname(nickname);
        if (foundByUsername.isPresent() && !foundByUsername.get().isDelete()) {  // 탈퇴안한경우 false -> 즉 !false => true
            throw new CustomException(ErrorCode.EXIST_NICKNAME);
        }
    }

    //회원가입 - 인증된 이메일인지 검사
    public void validateEmailAuth(String email, RedisService redisService) {
        if (!redisService.isEmailVerified(email)) {
            throw new CustomException(ErrorCode.EMAIL_UNVERIFIED);
        }
    }

    //로그인 - Email 존재하는지 검사
    public Member validateEmailExist(String email) {
        Member member = memberRepository.findByEmail(email).orElse(null);
        if (member == null) {
            throw new CustomException(ErrorCode.NOT_EXIST_EMAIL);
        }
        return member;
    }

    //로그인 - 삭제된 계정인지 검사
    public void validateEmailisDeleted(Member member) {
        if (member.isDelete()) {
            throw new CustomException(ErrorCode.NOT_EXIST_EMAIL);
        }
    }

    //로그인, 회원탈퇴 - 패스워드 검사
    public void passwordCheck(String password, Member member) {
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new CustomException(ErrorCode.WRONG_PASSWORD);
        }
    }

    //회원탈퇴 - 내가 올린 게시물에 아직 완료되지 않은 예약이 있을 경우
    public void unfinishedMyPostReservationCheck(Member member){
        List<Reservation> unfinishedPostReservations = reservationRepository.findByPost_MemberAndIsFinishedFalse(member);
        if (!unfinishedPostReservations.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FINISHED_MYPOST_RESERVATION);
        }

    }

    //회원탈퇴 - 아직 완료되지 않은 예약이 있을 경우
    public void unfinishedMyReservationCheck(Member member){
        List<Reservation> unfinishedMemberReservations = reservationRepository.findByMemberAndIsFinishedFalse(member);
        if (!unfinishedMemberReservations.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FINISHED_MYRESERVATION);
        }

    }
}
