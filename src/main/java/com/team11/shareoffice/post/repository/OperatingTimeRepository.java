package com.team11.shareoffice.post.repository;

import com.team11.shareoffice.post.entity.OperatingTime;
import com.team11.shareoffice.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperatingTimeRepository extends JpaRepository<OperatingTime,Long> {
    void deleteByPost(Post post);
}
