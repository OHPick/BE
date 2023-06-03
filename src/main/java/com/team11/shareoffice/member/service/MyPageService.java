package com.team11.shareoffice.member.service;

import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.global.exception.CustomException;
import com.team11.shareoffice.global.util.ErrorCode;
import com.team11.shareoffice.like.entity.Likes;
import com.team11.shareoffice.like.repository.LikeRepository;
import com.team11.shareoffice.member.dto.ProfileCountDto;
import com.team11.shareoffice.member.dto.ProfileDto;
import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.member.repository.MemberRepository;
import com.team11.shareoffice.member.validator.MemberValidator;
import com.team11.shareoffice.post.dto.PostResponseDto;
import com.team11.shareoffice.post.entity.Post;
import com.team11.shareoffice.post.repository.PostRepository;
import com.team11.shareoffice.post.service.ImageService;
import com.team11.shareoffice.reservation.entity.Reservation;
import com.team11.shareoffice.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MyPageService {
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final ImageService imageService;
    private final MemberValidator memberValidator;

    @Transactional(readOnly = true)
    public ResponseDto<List<PostResponseDto>> getMyPosts(Member member) {
        // 내가 쓴 게시글 리스트 찾기.
        List<Post> posts = postRepository.findAllByMemberOrderByCreatedAt(member);

        List<PostResponseDto> postResponseList = posts.stream()
                .map(post -> new PostResponseDto(post, isLikedByMember(post, member), 3))
                .collect(Collectors.toList());

        return ResponseDto.setSuccess("내 게시글목록 조회성공", postResponseList);
    }

    // 나의 좋아요 목록
    @Transactional(readOnly = true)
    public ResponseDto<List<PostResponseDto>> getMyLikes(Member member) {
        List<Post> postList = likeRepository.findAllByMemberAndLikeStatus(member, true).stream()
                .map(like -> like.getPost()) // Like 엔티티에서 Post 엔티티로 변환
                .collect(Collectors.toList());
        List<PostResponseDto> postResponseList = postList.stream()

                .map(post -> new PostResponseDto(post, isLikedByMember(post, member), getUserStatus(member, post)) )

                .collect(Collectors.toList());
        return ResponseDto.setSuccess("내 좋아요 목록 조회 성공", postResponseList);
    }

    //나의 예약 현황
    @Transactional(readOnly = true)
    public ResponseDto<List<PostResponseDto>> getMyReserves(Member member) {
        List<Post> reservations = reservationRepository.findAllByMember(member).stream().map(Reservation::getPost).toList();


        List<PostResponseDto> postResponseDtoList = reservations.stream().map(post -> new PostResponseDto(post, isLikedByMember(post, member), 2)).collect(Collectors.toList());

        return ResponseDto.setSuccess("나의 예약 현황 목록 조회 완료", postResponseDtoList);
    }

    private boolean isLikedByMember(Post post, Member member) {
        Likes like = likeRepository.findByMemberAndPost(member, post);
        return like != null;  // like가  null이 아니면 true를  반환.  아니면 false
    }

    private int getUserStatus(Member member, Post post){
        if(post.getMember().getEmail().equals(member.getEmail())){
            return 3;
        }
        else {
            if(reservationRepository.findByMemberAndPost(member,post).isPresent()){
                return 2;
            }
            return 1;
        }
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

        memberRepository.save(member); // ???
        ProfileDto modified = new ProfileDto(nickName);

        return ResponseDto.setSuccess("프로필 수정 성공",modified);
    }
}
