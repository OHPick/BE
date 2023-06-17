package com.team11.shareoffice.post.service;

import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.global.security.UserDetailsImpl;
import com.team11.shareoffice.image.entity.Image;
import com.team11.shareoffice.image.repository.ImageRepository;
import com.team11.shareoffice.image.service.ImageService;
import com.team11.shareoffice.like.entity.Likes;
import com.team11.shareoffice.like.repository.LikeRepository;
import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.post.dto.*;
import com.team11.shareoffice.post.entity.Amenities;
import com.team11.shareoffice.post.entity.OperatingTime;
import com.team11.shareoffice.post.entity.Post;
import com.team11.shareoffice.post.repository.AmenitiesRepository;
import com.team11.shareoffice.post.repository.OperatingTimeRepository;
import com.team11.shareoffice.post.repository.PostRepository;
import com.team11.shareoffice.post.validator.PostValidator;
import com.team11.shareoffice.reservation.entity.Reservation;
import com.team11.shareoffice.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


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
    private final OperatingTimeRepository operatingTimeRepository;
    private final AmenitiesRepository amenitiesRepository;

    public Page<MainPageResponseDto> findPosts(UserDetailsImpl userDetails, String keyword, String district, String sorting, Pageable pageable) {
        return postRepository.FilteringAndPaging(userDetails, keyword, district, sorting, pageable);
    }


    public Long createPost(PostRequestDto postRequestDto,  List<MultipartFile> imageFileList, Member member) throws IOException {

        OperatingTime operatingTime = operatingTimeRepository.save(new OperatingTime(postRequestDto.getOperatingTime()));
        Amenities amenities = amenitiesRepository.save(new Amenities(postRequestDto.getAmenities()));
        Post post = postRepository.save(new Post(postRequestDto, amenities, operatingTime, member));

        amenities.setPost(post);
        operatingTime.setPost(post);

        List<String> imageUrls = imageService.uploadFile(imageFileList);

        List<String> imgs = new ArrayList<>();

        List<Image> images = new ArrayList<>();

        for (String imageUrl : imageUrls) {
            Image image = new Image(post, imageUrl);
            images.add(image);
            imgs.add(image.getImageUrl());
        }

        imageRepository.saveAll(images);

        // imgs 리스트는 image객체의 Url 리스트
        post.setPostImages(imgs);

        postRepository.save(post);
        return post.getId();
    }

    public void updatePost(Long id, PostUpdateRequestDto postRequestDto,  List<MultipartFile> updateImages, Member member) throws IOException {
        //게시글 존재 확인.
        Post post = postValidator.validateIsExistPost(id);
        //게시글 작성자가 맞는지 확인.
        postValidator.validatePostAuthor(post, member);
        OperatingTime operatingTime =  post.getOperatingTime();
        operatingTime.updateOperatingTime(postRequestDto.getOperatingTime());
        Amenities amenities = post.getAmenities();
        amenities.updateAmenities(postRequestDto.getAmenities());

        List<String> imageList = new ArrayList<>(post.getPostImagesCustom());
        List<String> requestImageList = postRequestDto.getImageUrls(); // 선택한 이미지URL
        List<String> removeImgList = new ArrayList<>();

        for (String img : imageList) {
            if (requestImageList.contains(img)) {
                imageService.delete(img); // S3에서 해당 이미지 삭제
                imageRepository.deleteByImageUrl(img);  // DB에서 해당 이미지 삭제
                //수정할 이미지 담기
                removeImgList.add(img);
            }
        }


        imageList.removeAll(removeImgList);

        if (!updateImages.isEmpty()) {
            for (MultipartFile img : updateImages) {  // 새로운 이미지들
                if(!img.isEmpty()) {
                    String newUrl = imageService.uploadOneFile(img); // 수정할이미지 S3에 저장
                    imageList.add(newUrl);
                }
            }
        }

        post.updatePost(postRequestDto, amenities, operatingTime);

    }

    public void deletePost(Long id,Member member) {
        Post post = postValidator.validateIsExistPost(id);
        postValidator.validatePostAuthor(post, member);
        likeRepository.deleteLikesByPost(post);
        operatingTimeRepository.deleteByPost(post);
        amenitiesRepository.deleteByPost(post);

       //완료되지 않은 예약 있는지 확인
        postValidator.unfinishedReservationCheck(post);

        // post 삭제시 s3에 저장된 이미지도 삭제
        List<Image> imageList = imageRepository.findAllByPost(post);
        for (Image image : imageList) {
            imageRepository.delete(image);
            imageService.delete(image.getImageUrl());
        }
        post.setDelete(true);
        postRepository.save(post);
    }

    // 상세 게시글 조회
    public PostResponseDto getPost(Long id,Member member) {
        Post post = postValidator.validateIsExistPost(id);
        return getPostByUserDetails(member,post);
    }

    private PostResponseDto getPostByUserDetails(Member member, Post post) {
        PostResponseDto postResponseDto = new PostResponseDto(post, false, 0, post.getOperatingTime(), post.getAmenities());

        if (member != null) {
            for (Likes likes : likeRepository.findAllByPost(post)) {  // 게시글에 달린 좋아요 객체 가져오기
                if (member.getEmail().equals(likes.getMember().getEmail())) {
                    postResponseDto.setLikeStatus(likes.isLikeStatus());
                    break;
                }
            }
            postResponseDto.setUserStatus(getUserStatus(member,post));
        }
        List<Image> images = imageRepository.findAllByPost(post);
        List<String> imageUrls = new ArrayList<>();
        for (Image image : images) {
            imageUrls.add(image.getImageUrl());
        }
        postResponseDto.setImageUrl(imageUrls);
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
