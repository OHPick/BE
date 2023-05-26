package com.team11.shareoffice.post.controller;


import com.team11.shareoffice.post.dto.MainPageResponseDto;
import com.team11.shareoffice.post.service.MainPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MainPageController {
//메인페이지 드랍박스 : 인기순(좋아요순서), 가격 높은순, 낮은순, 최신순..?
//검색 누르면 지역 필터링(시단위) + 검색으로 title, content, 지역명 키워드로  -> 총 n건의 게시물 리턴  ..
// 검색에는 지역만 ⇒ 지역필터는 필수로 (?) 검색은 그럼 지역만??

    private final MainPageService mainPageService;

    @GetMapping("/api/posts")
    public Page<MainPageResponseDto> getPosts(@RequestParam(required = false) String keyword,
                                              @RequestParam(required = false) String district,
                                              @RequestParam(required = false) String sorting,
                                              Pageable pageable) {
        return mainPageService.findPosts(keyword, district, sorting, pageable);
    }
}
