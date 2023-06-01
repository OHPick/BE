package com.team11.shareoffice.post.service;

import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.like.entity.Likes;
import com.team11.shareoffice.like.repository.LikeRepository;
import com.team11.shareoffice.like.service.LikeService;
import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.post.dto.PostRequestDto;
import com.team11.shareoffice.post.dto.PostResponseDto;
import com.team11.shareoffice.post.dto.PostUpdateRequestDto;
import com.team11.shareoffice.post.entity.Post;
import com.team11.shareoffice.post.repository.PostRepository;
import com.team11.shareoffice.post.validator.PostValidator;
import com.team11.shareoffice.reservation.entity.Reservation;
import com.team11.shareoffice.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostValidator postValidator;
    private final ImageService imageService;
    private final LikeRepository likeRepository;


    public ResponseDto<?> createPost(PostRequestDto postRequestDto, MultipartFile image, Member member) throws IOException {
        // 이미지 존재 확인
//         if (image == null || image.isEmpty()) {
//             throw new IllegalArgumentException();
//         }

        String imageUrl = imageService.uploadFile(image);
       
        
        Post post = new Post(postRequestDto, member);
        post.setPostImage(imageUrl);
        postRepository.save(post);
        return ResponseDto.setSuccess("게시글 작성 성공");
    }

    public ResponseDto<?> updatePost(Long id, PostUpdateRequestDto postRequestDto, MultipartFile image, Member member) throws IOException {
        //게시글 존재 확인.
        Post post = postValidator.validateIsExistPost(id);
        //게시글 작성자가 맞는지 확인.
        postValidator.validatePostAuthor(post, member);
        // 제목,내용,위치 수정.
        post.updatePost(postRequestDto);

        if (!image.isEmpty()) {
            //기존에 있던 이미지 파일 s3에서 삭제
            imageService.delete(post.getPostImage());
            //새로 등록한 사진 s3에 업로드
            String uploadFilename = imageService.uploadFile(image);
            //업로드 된 사진으로 수정
            post.setPostImage(uploadFilename);
        }

        return ResponseDto.setSuccess("게시글 수정 성공");
    }

    public ResponseDto<?> deletePost(Long id,Member member) {
        Post post = postValidator.validateIsExistPost(id);
        postValidator.validatePostAuthor(post, member);
        likeRepository.deleteLikesByPost(post);
        imageService.delete(post.getPostImage()); // 버켓의 이미지파일도 삭제
        postRepository.delete(post);
        return ResponseDto.setSuccess("게시글 삭제 성공");
    }

    // 상세 게시글 조회
    public ResponseDto<PostResponseDto> getPost(Long id,Member member) {
        Post post = postValidator.validateIsExistPost(id);
        PostResponseDto postResponseDto = getPostByUserDetails(member,post);
        return ResponseDto.setSuccess("상세 게시글 조회 성공", postResponseDto);
    }

    private PostResponseDto getPostByUserDetails(Member member, Post post) {
        PostResponseDto postResponseDto = new PostResponseDto(post, false);

        if (member != null) {
            for (Likes likes : likeRepository.findAllByPost(post)) {
                if (member.getEmail().equals(likes.getMember().getEmail())) {
                    postResponseDto.setLikeStatus(true);
                    break;
                }
            }
        }
        return postResponseDto;
    }

}
