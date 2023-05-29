package com.team11.shareoffice.member.service;

import com.team11.shareoffice.email.entity.Email;
import com.team11.shareoffice.email.repository.EmailRepository;
import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.global.exception.CustomException;
import com.team11.shareoffice.global.jwt.JwtUtil;
import com.team11.shareoffice.global.jwt.dto.TokenDto;
import com.team11.shareoffice.global.jwt.entity.RefreshToken;
import com.team11.shareoffice.global.jwt.repository.RefreshTokenRepository;
import com.team11.shareoffice.global.util.ErrorCode;
import com.team11.shareoffice.member.dto.LoginRequestDto;
import com.team11.shareoffice.member.dto.ProfileDto;
import com.team11.shareoffice.member.dto.SignupRequestDto;
import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.member.repository.MemberRepository;
import com.team11.shareoffice.member.validator.MemberValidator;
import com.team11.shareoffice.post.entity.Post;
import com.team11.shareoffice.post.repository.PostRepository;
import com.team11.shareoffice.post.service.ImageService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

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
//    private final Member member;
    private final ImageService imageService;


    // 회원가입
    public ResponseDto<?> signup(SignupRequestDto requestDto){
        String email = requestDto.getEmail();
        String password = passwordEncoder.encode(requestDto.getPassword());
        String nickname = requestDto.getNickname();



        // 비밀번호와 확인 비밀번호 일치 확인
//        if(!Objects.equals(requestDto.getPassword(), requestDto.getPasswordCheck())){
//            throw new CustomException(ErrorCode.NOT_SAME_PASSWORD);
//        }
        memberValidator.validatePasswordCheck(requestDto);

        // 이메일 중복 검사
//        Optional<Member> foundByEmail = memberRepository.findByEmail(email);
//        if (foundByEmail.isPresent()){
//            throw new CustomException(ErrorCode.EXIST_EMAIL);
//        }
        memberValidator.validateEmailOverlapped(email);

        // 닉네임 중복 검사
//        Optional<Member> foundByUsername = memberRepository.findByNickname(nickname);
//        if (foundByUsername.isPresent()){
//            throw new CustomException(ErrorCode.EXIST_NICKNAME);
//        }
        memberValidator.validateNicknameOverlapped(nickname);

//        인증된 이메일인지 검사
//        Email validEmail =  emailRepository.findById(email).orElseThrow(() -> new CustomException(ErrorCode.WRONG_EMAIL));
//        if(!validEmail.isChecked()){
//            throw new CustomException(ErrorCode.WRONG_EMAIL);
//        }
//        memberValidator.validateEmailAuth(email);

        // 유저 등록
        Member member = Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .isDelete(false)
                .build();
//

        String basicImage = "https://shareoffice12.s3.ap-northeast-2.amazonaws.com/image.png";


        Member member = new Member(email,password,nickname,basicImage);

        memberRepository.save(member);
        emailRepository.deleteById(email);
        return setSuccess("회원가입에 성공했습니다.");

    }

    // 로그인
    public ResponseDto<?> login(SignupRequestDto.login requestDto, HttpServletResponse response){
        String email = requestDto.getEmail();
        String password = requestDto.getPassword();

        // 이메일 검사
//        Member member = memberRepository.findByEmail(email).orElse(null);
//        if (member == null){
//            throw new CustomException(ErrorCode.NOT_EXIST_EMAIL);
//        }
        memberValidator.validateEmailExist(email);

        //삭제된 계정 여부
//        if (member.isDelete()) {
//            throw new CustomException(ErrorCode.NOT_EXIST_EMAIL);
//        }
        memberValidator.validateEmailisDeleted(member);

        // 패스워드 검사
//        if (!passwordEncoder.matches(password, member.getPassword())){
//            throw new CustomException(ErrorCode.WRONG_PASSWORD);
//        }
        memberValidator.passwordCheck(password, member);

        //Token 생성
        TokenDto tokenDto = jwtUtil.createAllToken(member.getEmail());

        //RefreshToken 있는지 확인
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByMember(member);

        //있으면 새 토큰 발급 후 업데이트
        //없으면 새로 만들고 DB에 저장
        if (refreshToken.isPresent()) {
            refreshTokenRepository.save(refreshToken.get().updateToken(tokenDto.getRefreshToken()));
        } else {
            refreshTokenRepository.saveAndFlush(RefreshToken.saveToken(tokenDto.getRefreshToken(), member));
        }
        //header에 accesstoken, refreshtoken 추가
        response.addHeader(JwtUtil.ACCESS_TOKEN, tokenDto.getAccessToken());
        response.addHeader(JwtUtil.REFRESH_TOKEN, tokenDto.getRefreshToken());

        return setSuccess("로그인 성공");
    }

    //프로필조회
    @Transactional(readOnly = true)
    public ResponseDto<ProfileDto> profile(Member member) {
        String email = member.getEmail();
        String nickName = member.getNickname();
        String imageUrl = member.getImageUrl();

        ProfileDto profileDto = new ProfileDto(email,nickName, imageUrl);

        return ResponseDto.setSuccess("프로필 조회성공",profileDto);
    }

    // 프로필 수정
    public ResponseDto<ProfileDto> profileModify(ProfileDto profileDto, MultipartFile image, Member member) throws IOException {

        String nickName = profileDto.getNickName();

        // 닉네임 중복 검사
        Optional<Member> foundByUsername = memberRepository.findByNickname(nickName);
        if (foundByUsername.isPresent()){
            throw new CustomException(ErrorCode.EXIST_NICKNAME);
        }
        member.updateNickName(profileDto.getNickName());

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

//    public ResponseDto<?> logout(Member member) {
//        Optional<RefreshToken> isMemberLoggedIn = refreshTokenRepository.findByMember(member);
//        if(isMemberLoggedIn.isEmpty()) {
//            throw new  CustomException(ErrorCode.NOT_FOUND_USER_INFO);
//        }
//        // SecurityContext에서 인증 정보를 삭제
////        SecurityContextHolder.getContext().setAuthentication(null);
////        refreshTokenRepository.deleteByMember(member);
//        return ResponseDto.setSuccess("로그아웃 성공");
//    }

    public ResponseDto<?> signout(Member member, SignupRequestDto.signout request) {
        String password = request.getPassword();

        memberValidator.passwordCheck(password, member);

        memberValidator.validateToken(member);

        member.setDelete(true);
        memberRepository.save(member);

        List<Post> posts = postRepository.findAllByMemberId(member.getId());
        if (!posts.isEmpty()) {
            for (Post p : posts) {
                p.setDelete(true);
                postRepository.save(p);
            }
        }
        refreshTokenRepository.deleteByMember(member);

        return ResponseDto.setSuccess("회원탈퇴 성공");
    }
}

