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

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static com.team11.shareoffice.global.dto.ResponseDto.setSuccess;

@RequiredArgsConstructor
@Service
@Transactional
public class MemberService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailRepository emailRepository;
    private final ImageService imageService;


    // 회원가입
    public ResponseDto<?> signup(SignupRequestDto signupRequestDto){
        String email = signupRequestDto.getEmail();
        String password = passwordEncoder.encode(signupRequestDto.getPassword());
        String nickname = signupRequestDto.getNickname();

        // 비밀번호와 확인 비밀번호 일치 확인
        if(!Objects.equals(signupRequestDto.getPassword(), signupRequestDto.getPasswordCheck())){
            throw new CustomException(ErrorCode.NOT_SAME_PASSWORD);
        }

        // 이메일 중복 검사
        Optional<Member> foundByEmail = memberRepository.findByEmail(email);
        if (foundByEmail.isPresent()){
            throw new CustomException(ErrorCode.EXIST_EMAIL);
        }

        // 닉네임 중복 검사
        Optional<Member> foundByUsername = memberRepository.findByNickname(nickname);
        if (foundByUsername.isPresent()){
            throw new CustomException(ErrorCode.EXIST_NICKNAME);
        }

        //인증된 이메일인지 검사
        Email validEmail =  emailRepository.findById(email).orElseThrow(() -> new CustomException(ErrorCode.WRONG_EMAIL));
        if(!validEmail.isChecked()){
            throw new CustomException(ErrorCode.WRONG_EMAIL);
        }

        String basicImage = "https://shareoffice12.s3.ap-northeast-2.amazonaws.com/image.png";

//        // 유저 등록
//        Member member = Member.builder()
//                .email(validEmail.getEmail())
//                .password(password)
//                .nickname(nickname)
//                .imageUrl(basicImage)
//                .build();

        Member member = new Member(email,password,nickname,basicImage);

        memberRepository.save(member);
        emailRepository.deleteById(email);
        return setSuccess("회원가입에 성공했습니다.");

    }

    // 로그인
    public ResponseDto<?> login(LoginRequestDto loginRequestDto, HttpServletResponse response){
        String email = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();

        // 이메일 검사
        Member member = memberRepository.findByEmail(email).orElse(null);
        if (member == null){
            throw new CustomException(ErrorCode.NOT_EXIST_EMAIL);
        }

        // 패스워드 검사
        if (!passwordEncoder.matches(password, member.getPassword())){
            throw new CustomException(ErrorCode.WRONG_PASSWORD);
        }

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
}
