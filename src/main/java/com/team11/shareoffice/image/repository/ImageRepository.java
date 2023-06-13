package com.team11.shareoffice.image.repository;

import com.team11.shareoffice.image.entity.Image;
import com.team11.shareoffice.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findAllByPost(Post post);
    void deleteByImageUrl(String imageUrl);

}