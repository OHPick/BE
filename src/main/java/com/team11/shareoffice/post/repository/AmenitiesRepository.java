package com.team11.shareoffice.post.repository;

import com.team11.shareoffice.post.entity.Amenities;
import com.team11.shareoffice.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmenitiesRepository extends JpaRepository<Amenities ,Long> {

    void deleteByPost(Post post);
}
