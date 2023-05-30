package com.team11.shareoffice.post.repository;

import com.team11.shareoffice.global.security.UserDetailsImpl;
import com.team11.shareoffice.post.dto.MainPageResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {
    Page<MainPageResponseDto> FilteringAndPaging(UserDetailsImpl userDetails, String keyword, String district, String sorting, Pageable pageable);
}
