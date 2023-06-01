package com.team11.shareoffice.post.service;

import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.like.entity.Likes;
import com.team11.shareoffice.like.repository.LikeRepository;
import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.post.dto.PostResponseDto;
import com.team11.shareoffice.post.entity.Post;
import com.team11.shareoffice.post.repository.PostRepository;
import com.team11.shareoffice.post.validator.PostValidator;
import com.team11.shareoffice.reservation.entity.Reservation;
import com.team11.shareoffice.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MyPageService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final ReservationRepository reservationRepository;
    private final PostValidator postValidator;

    @Transactional(readOnly = true)
    public ResponseDto<List<PostResponseDto>> getMyPosts(Member member) {
        // 내가 쓴 게시글 리스트 찾기.
        List<Post> posts = postRepository.findAllByMemberOrderByCreatedAt(member);

        List<PostResponseDto> postResponseList = posts.stream()
                .map(post -> new PostResponseDto(member, post, isLikedByMember(post, member)))
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
                .map(post -> new PostResponseDto(member, post, isLikedByMember(post, member)) )
                .collect(Collectors.toList());
        return ResponseDto.setSuccess("내 좋아요 목록 조회 성공", postResponseList);
    }

    //나의 예약 현황
    @Transactional(readOnly = true)
    public ResponseDto<List<PostResponseDto>> getMyReserves(Member member) {
        List<Post> reservations = reservationRepository.findAllByMember(member).stream().map(Reservation::getPost).toList();

        List<PostResponseDto> postResponseDtoList = reservations.stream().map(post -> new PostResponseDto(member, post, isLikedByMember(post, member))).collect(Collectors.toList());

        return ResponseDto.setSuccess("나의 예약 현황 목록 조회 완료", postResponseDtoList);
    }

    private boolean isLikedByMember(Post post, Member member) {
        Likes like = likeRepository.findByMemberAndPost(member, post);
        return like != null;  // like가  null이 아니면 true를  반환.  아니면 false
    }
}
