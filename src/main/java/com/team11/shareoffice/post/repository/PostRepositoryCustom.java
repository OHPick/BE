package com.team11.shareoffice.post.repository;

import com.team11.shareoffice.post.dto.MainPageResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {
    Page<MainPageResponseDto> search(String keyword, String sorting, String district, Pageable pageable);
}
