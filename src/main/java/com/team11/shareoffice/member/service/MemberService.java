package com.team11.shareoffice.member.service;

import com.team11.shareoffice.global.jwt.JwtUtil;
import com.team11.shareoffice.global.jwt.dto.TokenDto;
import com.team11.shareoffice.global.security.UserDetailsImpl;
import com.team11.shareoffice.global.service.RedisService;
import com.team11.shareoffice.image.service.ImageService;
import com.team11.shareoffice.like.repository.LikeRepository;
import com.team11.shareoffice.member.dto.LoginRequestDto;
import com.team11.shareoffice.member.dto.SignoutRequestDto;
import com.team11.shareoffice.member.dto.SignupRequestDto;
import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.member.repository.MemberRepository;
import com.team11.shareoffice.member.validator.MemberValidator;
import com.team11.shareoffice.post.entity.Post;
import com.team11.shareoffice.post.repository.PostRepository;
import com.team11.shareoffice.reservation.repository.ReservationRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
@Builder
public class MemberService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final PostRepository postRepository;
    private final MemberValidator memberValidator;
    private final ImageService imageService;
    private final RedisService redisService;
    private final LikeRepository likeRepository;
    private final ReservationRepository reservationRepository;


    // 회원가입
    public void signup(SignupRequestDto requestDto){
        String email = requestDto.getEmail();
        String password = passwordEncoder.encode(requestDto.getPassword());
        String nickname = requestDto.getNickname();

        // 비밀번호와 확인 비밀번호 일치 여부 판별
        memberValidator.validatePasswordCheck(requestDto);
        // 닉네임 패턴 및 중복 검사
        memberValidator.validateNickname(nickname);
//        인증된 이메일인지 검사
        memberValidator.validateEmailAuth(email, redisService);

        // 유저 등록
        Member member = Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .isDelete(false)
                .build();
        memberRepository.save(member);
    }

    // 로그인
    public void login(LoginRequestDto requestDto, HttpServletResponse response){
        String email = requestDto.getEmail();
        String password = requestDto.getPassword();

        // 이메일 검사
        Member member = memberValidator.validateEmailExist(email);
        //삭제된 계정 여부
        memberValidator.validateEmailisDeleted(member);
        // 패스워드 검사
        memberValidator.passwordCheck(password, member);

        //엑세스, 리프레쉬 다 발급 + 리프레쉬 레디스 저장
        issueTokens(response, email);
    }

    public void issueTokens(HttpServletResponse response, String email){
        TokenDto tokenDto = jwtUtil.createAllToken(email);
        response.addHeader(JwtUtil.ACCESS_TOKEN, tokenDto.getAccessToken());
        response.addHeader(JwtUtil.REFRESH_TOKEN, tokenDto.getRefreshToken());
//        Cookie cookieAccessToken = new Cookie(JwtUtil.ACCESS_TOKEN, tokenDto.getAccessToken());
//        cookieAccessToken.setHttpOnly(true);
//        cookieAccessToken.setSecure(true); // Set the Secure attribute to true
//        response.addCookie(cookieAccessToken);
//        Cookie cookieRefreshToken = new Cookie(JwtUtil.REFRESH_TOKEN, tokenDto.getRefreshToken());
//        cookieRefreshToken.setHttpOnly(true);
//        cookieRefreshToken.setSecure(true); // Set the Secure attribute to true
//        response.addCookie(cookieRefreshToken);
        redisService.setValues(email, tokenDto.getRefreshToken(), Duration.ofDays(1));
    }

    public void logout(Member member, HttpServletRequest request){
        String accessToken = jwtUtil.resolveToken(request,JwtUtil.ACCESS_TOKEN);
        redisService.setBlackList(accessToken);
        redisService.delValues(member.getEmail());
    }



    //회원탈퇴
    public void signout(UserDetailsImpl userDetails, SignoutRequestDto request) {
        String password = request.getPassword();
        Member member = memberValidator.validateEmailExist(userDetails.getMember().getEmail());
        memberValidator.passwordCheck(password, member);

        //내 게시물에 미완의 예약 있을경우
        memberValidator.unfinishedMyPostReservationCheck(member);

        //나의 예약 내역중 미완의 예약이 있을 경우
        memberValidator.unfinishedMyReservationCheck(member);

        // 탈퇴시 이메일 닉네임 수정
        String signoutUser = "(탈퇴한 회원 No." + member.getId() +")";
        member.setEmail(member.getEmail() + signoutUser);
        member.setNickname(member.getNickname() + signoutUser);

        member.setDelete(true);
        memberRepository.save(member);

        List<Post> posts = postRepository.findAllByMemberId(member.getId());
        if (!posts.isEmpty()) {
            for (Post p : posts) {
                p.setDelete(true);
                postRepository.save(p);
            }
        }
    }
}

