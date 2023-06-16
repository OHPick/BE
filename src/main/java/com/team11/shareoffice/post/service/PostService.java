package com.team11.shareoffice.post.service;

import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.global.security.UserDetailsImpl;
import com.team11.shareoffice.image.service.ImageService;
import com.team11.shareoffice.like.entity.Likes;
import com.team11.shareoffice.like.repository.LikeRepository;
import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.post.dto.MainPageResponseDto;
import com.team11.shareoffice.post.dto.PostRequestDto;
import com.team11.shareoffice.post.dto.PostResponseDto;
import com.team11.shareoffice.post.dto.PostUpdateRequestDto;
import com.team11.shareoffice.post.entity.Post;
import com.team11.shareoffice.post.repository.PostRepository;
import com.team11.shareoffice.post.validator.PostValidator;
import com.team11.shareoffice.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final ReservationRepository reservationRepository;
    private final ImageRepository imageRepository;

    public Page<MainPageResponseDto> findPosts(UserDetailsImpl userDetails, String keyword, String district, String sorting, Pageable pageable) {
        return postRepository.FilteringAndPaging(userDetails, keyword, district, sorting, pageable);
    }


    public Long createPost(PostRequestDto postRequestDto, List<MultipartFile> imageFileList, Member member) throws IOException {

        Post post = postRepository.save(new Post(postRequestDto, member));

        List<String> imageUrls = imageService.uploadFile(imageFileList);

        List<String> imgs = new ArrayList<>();

        List<Image> images = new ArrayList<>();
        System.out.println("imageUrls.get(0) : "+ imageUrls.get(0));
        System.out.println("imageUrls.get(1) : "+ imageUrls.get(1));

        for (String imageUrl : imageUrls) {
            Image image = new Image(post, imageUrl);
            images.add(image);
            imgs.add(image.getImageUrl());
        }
        // imgaes는 image객체 리스트
//        for (String imageUrl : imageUrls) {
//            Image image = new Image(post, imageUrl);
//            imgs.add(image.getImageUrl());
//        }

        imageRepository.saveAll(images);

        // imgs 리스트는 image객체의 Url 리스트
        post.setPostImages(imgs);

        postRepository.save(post);
        return post.getId();
    }

    public void updatePost(Long id, PostUpdateRequestDto postRequestDto, List<MultipartFile> updateImages, Member member) throws IOException {
        //게시글 존재 확인.
        Post post = postValidator.validateIsExistPost(id);
        //게시글 작성자가 맞는지 확인.
        postValidator.validatePostAuthor(post, member);

        List<String> imageList = new ArrayList<>(post.getPostImagesCustom());

        System.out.println("$$$$$$$$$$$$"+imageList);

//        List<String> imageList = new ArrayList<>();
//        for (String images : post.getPostImages()) {
//            // Remove brackets and split by comma
//            String[] urls = images.substring(1, images.length() - 1).split(", ");
//            // Add all URLs to imageList
//            imageList.addAll(Arrays.asList(urls));
//        }

        System.out.println("&&&&&&&&&&&&&&& imageList: " + imageList); // &&&&&&&&&&&&&&& imageList: [d936f801-4d0a-4c0c-8090-10ded4481054_33.jpg, 360e7fba-bc54-4879-9ccd-8c44bc2438cb_우주1.jpg]
        System.out.println("imageList.get(0) : "+imageList.get(0));  // imageList.get(0) : [90e09703-96b4-4c2b-a203-27e3d0d5e428_33.jpg, 16aca9ce-9aea-4c22-9f64-3d5ea51f539d_우주1.jpg]
        System.out.println("post.getPostImages().get(0) : " +  post.getPostImagesCustom().get(0));  //post.getPostImages().get(0) : [90e09703-96b4-4c2b-a203-27e3d0d5e428_33.jpg, 16aca9ce-9aea-4c22-9f64-3d5ea51f539d_우주1.jpg]

        List<String> requestImageList = postRequestDto.getImageUrls(); // 선택한 이미지URL
        System.out.println("requestImageList.get(0) : "+requestImageList.get(0));  // imageList.get(0) : [90e09703-96b4-4c2b-a203-27e3d0d5e428_33.jpg, 16aca9ce-9aea-4c22-9f64-3d5ea51f539d_우주1.jpg]
        List<String> removeImgList = new ArrayList<>();


        for (String img : imageList) {
            if (requestImageList.contains(img)) {
                imageService.delete(img); // S3에서 해당 이미지 삭제
                System.out.println("버켓에서 삭제 완료");
                imageRepository.deleteByImageUrl(img);  // DB에서 해당 이미지 삭제
                System.out.println("DB 에서 삭제 완료");
                //수정할 이미지 담기
                removeImgList.add(img);
                System.out.println("****"+removeImgList);
            }
        }


        imageList.removeAll(removeImgList);
//         removeImgList에 담긴 수정 이미지 원래 Imglist에서 제거

        System.out.println("updateImages : " + updateImages);

        if (!updateImages.isEmpty()) {
            for (MultipartFile img : updateImages) {  // 새로운 이미지들
                if(!img.isEmpty()) {
                    String newUrl = imageService.uploadOneFile(img); // 수정할이미지 S3에 저장
                    imageList.add(newUrl);
                }
            }
        }
    }

    public ResponseDto<?> deletePost(Long id,Member member) {
        Post post = postValidator.validateIsExistPost(id);
        postValidator.validatePostAuthor(post, member);
        likeRepository.deleteLikesByPost(post);
        imageService.delete(post.getPostImage()); // 버켓의 이미지파일도 삭제
//        postRepository.delete(post);
        post.setDelete(true);
        postRepository.save(post);

        // post 삭제시 s3에 저장된 이미지도 삭제
        List<Image> imageList = imageRepository.findAllByPost(post);
        for (Image image : imageList) {
            imageRepository.delete(image);
            imageService.delete(image.getImageUrl());
        }

        postRepository.delete(post);
        return ResponseDto.setSuccess("게시글 삭제 성공");
    }

    // 상세 게시글 조회
    public PostResponseDto getPost(Long id,Member member) {
        Post post = postValidator.validateIsExistPost(id);
        PostResponseDto postResponseDto = getPostByUserDetails(member,post);
        return postResponseDto;
    }

    private PostResponseDto getPostByUserDetails(Member member, Post post) {
        PostResponseDto postResponseDto = new PostResponseDto(post, false, 0);

        if (member != null) {
            for (Likes likes : likeRepository.findAllByPost(post)) {  // 게시글에 달린 좋아요 객체 가져오기
                if (member.getEmail().equals(likes.getMember().getEmail())) {
                    postResponseDto.setLikeStatus(likes.isLikeStatus());
                    break;
                }
            }
            postResponseDto.setUserStatus(getUserStatus(member,post));
        }
        return postResponseDto;
    }

    private int getUserStatus(Member member, Post post){
        if(post.getMember().getEmail().equals(member.getEmail())){
            return 3;
        }
        else {
            if(reservationRepository.findByMemberAndPostAndNotFinished(member, post).isPresent()){
                return 2;
            }
            return 1;
        }
    }
}
