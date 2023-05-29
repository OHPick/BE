package com.team11.shareoffice.post.service;

import com.team11.shareoffice.post.dto.MainPageResponseDto;
import com.team11.shareoffice.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class MainPageService {

    @Autowired
    private PostRepository postRepository;
    public Page<MainPageResponseDto> findPosts(String keyword, String district, String sorting, Pageable pageable) {
        return postRepository.FilteringAndPaging(keyword, district, sorting, pageable);
    }
}
