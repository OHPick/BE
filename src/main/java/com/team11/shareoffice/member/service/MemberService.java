package com.team11.shareoffice.member.service;

import com.team11.shareoffice.email.repository.EmailRepository;
import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.global.exception.CustomException;
import com.team11.shareoffice.global.jwt.JwtUtil;
import com.team11.shareoffice.global.jwt.dto.TokenDto;
import com.team11.shareoffice.global.jwt.entity.RefreshToken;
import com.team11.shareoffice.global.jwt.repository.RefreshTokenRepository;
import com.team11.shareoffice.global.security.UserDetailsImpl;
import com.team11.shareoffice.global.service.RedisService;
import com.team11.shareoffice.global.util.ErrorCode;
import com.team11.shareoffice.like.repository.LikeRepository;
import com.team11.shareoffice.member.dto.MemberRequestDto;
import com.team11.shareoffice.member.dto.ProfileCountDto;
import com.team11.shareoffice.member.dto.ProfileDto;
import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.member.repository.MemberRepository;
import com.team11.shareoffice.member.validator.MemberValidator;
import com.team11.shareoffice.post.entity.Post;
import com.team11.shareoffice.post.repository.PostRepository;
import com.team11.shareoffice.post.service.ImageService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import com.team11.shareoffice.reservation.entity.Reservation;
import com.team11.shareoffice.reservation.repository.ReservationRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.team11.shareoffice.global.dto.ResponseDto.setSuccess;

@RequiredArgsConstructor
@Service
@Transactional
@Builder
public class MemberService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailRepository emailRepository;
    private final PostRepository postRepository;
    private final MemberValidator memberValidator;
    private final ImageService imageService;
    private final RedisService redisService;
    private final LikeRepository likeRepository;
    private final ReservationRepository reservationRepository;


    // 회원가입
    public ResponseDto<?> signup(MemberRequestDto requestDto){
        String email = requestDto.getEmail();
        String password = passwordEncoder.encode(requestDto.getPassword());
        String nickname = requestDto.getNickname();

        // 비밀번호와 확인 비밀번호 일치 여부 판별
        memberValidator.validatePasswordCheck(requestDto);
        // 이메일 중복 검사
        memberValidator.validateEmailOverlapped(email);
        // 닉네임 중복 검사
        memberValidator.validateNicknameOverlapped(nickname);
//        인증된 이메일인지 검사
        memberValidator.validateEmailAuth(email);

        // 유저 등록
        Member member = Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .isDelete(false)
                .build();

        String basicImage = "https://shareoffice12.s3.ap-northeast-2.amazonaws.com/image.png";

//        Member member = new Member(email,password,nickname,basicImage);  //  이 부분

        memberRepository.save(member);
        emailRepository.deleteById(email);
        return setSuccess("회원가입에 성공했습니다.");
    }

    // 로그인
    public ResponseDto<?> login(MemberRequestDto requestDto, HttpServletResponse response){
        String email = requestDto.getEmail();
        String password = requestDto.getPassword();

        // 이메일 검사
        Member member = memberValidator.validateEmailExist(email);
        //삭제된 계정 여부
        memberValidator.validateEmailisDeleted(member);
        // 패스워드 검사
        memberValidator.passwordCheck(password, member);
        //Token 생성
//        TokenDto tokenDto = jwtUtil.createAllToken(email);
        //RefreshToken 있는지 확인
//        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByMember(member);
        //있으면 새 토큰 발급 후 업데이트  //없으면 새로 만들고 DB에 저장
//        if (refreshToken.isPresent()) {
//            refreshTokenRepository.save(refreshToken.get().updateToken(tokenDto.getRefreshToken()));
//            System.out.println("RTK있을때 if문 email값: " + email);
//
//        } else {
//            refreshTokenRepository.saveAndFlush(RefreshToken.saveToken(tokenDto.getRefreshToken(), member));
//            issueTokens(response, email);
//            System.out.println("RTK없을때 else문 email값: " + email);
//        }
        //header에 accesstoken, refreshtoken 추가
//        response.addHeader(JwtUtil.ACCESS_TOKEN, tokenDto.getAccessToken());
//        response.addHeader(JwtUtil.REFRESH_TOKEN, tokenDto.getRefreshToken());

        //엑세스, 리프레쉬 다 발급 + 리프레쉬 레디스 저장
        issueTokens(response, email);

        return setSuccess("로그인 성공");
    }

    public void issueTokens(HttpServletResponse response, String email){
        System.out.println("MemberService.issueTokens");
        TokenDto tokenDto = jwtUtil.createAllToken(email);
        response.addHeader(JwtUtil.ACCESS_TOKEN, tokenDto.getAccessToken());
        response.addHeader(JwtUtil.REFRESH_TOKEN, tokenDto.getRefreshToken());

        redisService.setValues(email, tokenDto.getRefreshToken(), Duration.ofDays(60));
    }

    //토큰 재발급
//    public ResponseDto<?> reissueToken(HttpServletRequest request, HttpServletResponse response) {
//        String refreshTokenFromRequest = request.getHeader(JwtUtil.REFRESH_TOKEN); //요청헤더에서 온 RTK
//        String token = jwtUtil.resolveToken(request,JwtUtil.ACCESS_TOKEN); //요청헤더에서 온 ATK(bearer 제외)
//        Claims info = jwtUtil.getClaimsFromToken(token); //ATK에서 body가지고 옴
//        String email = info.getSubject(); //가지고온 body에서 subject 빼오기 = email
//
//        String refreshTokenFromRedis = redisService.getValues(email);
//
//        if(refreshTokenFromRequest.equals(refreshTokenFromRedis)){
//            jwtUtil.validateToken(refreshTokenFromRequest);
//            issueTokens(response, email);
//            return setSuccess("토큰 재발급 성공");
//        } else {
//            throw new CustomException(ErrorCode.NOT_MATCH_REFRESHTOKEN);
//        }
//    }

    public void logout(Member member, HttpServletRequest request){
        String accessToken = jwtUtil.resolveToken(request,JwtUtil.ACCESS_TOKEN);
        redisService.setBlackList(accessToken);
        redisService.delValues(member.getEmail());
    }

    //프로필조회
    @Transactional(readOnly = true)
    public ResponseDto<ProfileCountDto> profile(Member member) {

        memberValidator.validateEmailExist(member.getEmail());

        String email = member.getEmail();
        String nickName = member.getNickname();
        String imageUrl = member.getImageUrl();

        // 내가 쓴 게시글 리스트 찾기.
        List<Post> myPosts = postRepository.findAllByMemberOrderByCreatedAt(member);
        int postCount = myPosts.size();

        // 내가 좋아요한 게시글 리스트 찾기.
        List<Post> myLikes = likeRepository.findAllByMemberAndLikeStatus(member, true).stream()
                .map(like -> like.getPost()) // Like 엔티티에서 Post 엔티티로 변환
                .collect(Collectors.toList());
        int likeCount = myLikes.size();

        // 내가 예약한 게시글 리스트 찾기.
        List<Post> myReservations = reservationRepository.findAllByMember(member).stream().map(Reservation::getPost).toList();
        int reserveCount = myReservations.size();

        ProfileCountDto profileCountDto = new ProfileCountDto(email,nickName, imageUrl, postCount, likeCount, reserveCount);

        return ResponseDto.setSuccess("프로필 조회성공",profileCountDto);
    }

    // 프로필 수정
    public ResponseDto<ProfileDto> profileModify(ProfileDto profileDto, MultipartFile image, Member member) throws IOException {
                
        String nickName = profileDto.getNickname();

        // 닉네임 중복 검사
        Optional<Member> foundByUsername = memberRepository.findByNickname(nickName);
        if (foundByUsername.isPresent()){
            throw new CustomException(ErrorCode.EXIST_NICKNAME);
        }
        member.updateNickName(profileDto.getNickname());

        //기존에 있던 이미지 파일 s3에서 삭제
        imageService.delete(member.getImageUrl());
        //새로 등록한 사진 s3에 업로드
        String uploadFilename = imageService.uploadFile(image);
        //업로드 된 사진으로 수정
        member.updateImageUrl(uploadFilename);

        memberRepository.save(member); // ??????????????????
        ProfileDto modified = new ProfileDto(member.getEmail(), nickName,uploadFilename);

        return ResponseDto.setSuccess("프로필 수정 성공",modified);
    }

    //회원탈퇴
    public ResponseDto<?> signout(UserDetailsImpl userDetails, MemberRequestDto request) {
        String password = request.getPassword();

        Member member = memberValidator.validateEmailExist(userDetails.getMember().getEmail());

        memberValidator.passwordCheck(password, member);

//        memberValidator.validateToken(member);

        member.setDelete(true);
        memberRepository.save(member);

        List<Post> posts = postRepository.findAllByMemberId(member.getId());
        if (!posts.isEmpty()) {
            for (Post p : posts) {
                p.setDelete(true);
                postRepository.save(p);
            }
        }
//        refreshTokenRepository.deleteByMember(member);

        return ResponseDto.setSuccess("회원탈퇴 성공");
    }
}

