package com.team11.shareoffice.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.team11.shareoffice.global.exception.CustomException;
import com.team11.shareoffice.global.util.ErrorCode;
import com.team11.shareoffice.image.entity.Image;
import com.team11.shareoffice.image.repository.ImageRepository;
import com.team11.shareoffice.post.entity.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {
    private final AmazonS3 amazonS3;
    private final ImageRepository imageRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Transactional
    //파일을 s3에 업로드
    public List<String> uploadFile(List<MultipartFile> multipartFileList) throws IOException {
        if (multipartFileList == null || multipartFileList.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_IMAGE);
        }

        List<String> imageUrlList = new ArrayList<>();

        for (MultipartFile image : multipartFileList) {
        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(image.getSize());
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, image.getInputStream(), metadata));
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 실패: " + fileName, e);
        }
        imageUrlList.add(fileName);

        }
        return imageUrlList;
    }

    //파일을 s3에 업로드
    public String uploadOneFile(MultipartFile multipartFile) throws IOException {
        validateFileExists(multipartFile);

        String fileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
        log.info(fileName);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());

        amazonS3.putObject(new PutObjectRequest(bucket, fileName, multipartFile.getInputStream(), objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead));

        return fileName;
    }

    public void saveImageList(Post post, List<String> imageUrlList) {
        for (String imageUrl : imageUrlList) {
            imageRepository.saveAndFlush(new Image(post, imageUrl));
        }
    }

    public void delete(String fileName){
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }

    //파일 유 / 무 확인 메서드
    private void validateFileExists(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }

}

