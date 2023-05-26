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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostValidator postValidator;
    private final ImageService imageService;
    private final LikeRepository likeRepository;

    public ResponseDto<Long> createPost(PostRequestDto postRequestDto, MultipartFile image, Member member) throws IOException {
        // 이미지 존재 확인
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException();
        }

        String imageUrl = imageService.uploadFile(image);

        Post post = new Post(postRequestDto, member);
        post.setPostImage(imageUrl);
        postRepository.save(post);
        return ResponseDto.setSuccess(null);
    }

    public ResponseDto<Long> updatePost(Long id, PostUpdateRequestDto postRequestDto, MultipartFile image, Member member) throws IOException {
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

        return ResponseDto.setSuccess(null);
    }

    public ResponseDto<Long> deletePost(Long id,Member member) {
        Post post = postValidator.validateIsExistPost(id);
        postValidator.validatePostAuthor(post, member);
        likeRepository.deleteLikesByPost(post);
        imageService.delete(post.getPostImage()); // 버켓의 이미지파일도 삭제
        postRepository.delete(post);
        return ResponseDto.setSuccess(null);
    }

    @Transactional(readOnly = true)
    public ResponseDto<List<PostResponseDto>> getMyPosts(Member member) {
        // 내가 쓴 게시글 리스트 찾기.
        List<Post> posts = postRepository.findAllByMemberOrderByCreatedAt(member);

        List<PostResponseDto> postResponseList = posts.stream()
                .map(post -> new PostResponseDto(post, isLikedByMember(post, member)))
                .collect(Collectors.toList());

        return ResponseDto.setSuccess("내 게시글목록 조회성공", postResponseList);
    }

    private boolean isLikedByMember(Post post, Member member) {
        Likes like = likeRepository.findByMemberAndPost(member, post);
        return like != null;  // like가  null이 아니면 true를  반환.  아니면 false
    }

    // 나의 좋아요 목록
    @Transactional(readOnly = true)
    public ResponseDto<List<PostResponseDto>> getMyLikes(Member member) {
        List<Post> postList = likeRepository.findAllByMemberAndLikeStatus(member, true).stream()
                .map(like -> like.getPost()) // Like 엔티티에서 Post 엔티티로 변환
                .collect(Collectors.toList());
        List<PostResponseDto> postResponseList = postList.stream()
                .map(post -> new PostResponseDto(post, isLikedByMember(post, member)) )
                .collect(Collectors.toList());
        return ResponseDto.setSuccess("내 좋아요 목록 조회 성공", postResponseList);
    }
}
