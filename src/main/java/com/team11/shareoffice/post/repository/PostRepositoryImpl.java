package com.team11.shareoffice.post.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team11.shareoffice.post.dto.MainPageResponseDto;
import com.team11.shareoffice.post.entity.Post;
import com.team11.shareoffice.post.entity.QPost;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PostRepositoryImpl extends QuerydslRepositorySupport implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;



    public PostRepositoryImpl(JPAQueryFactory queryFactory) {
        super(Post.class);
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<MainPageResponseDto> search(String keyword, String sorting, String district, Pageable pageable) {
        QPost post = QPost.post;
        BooleanBuilder builder = new BooleanBuilder();

        if (!StringUtils.isEmpty(keyword)) {
            builder.and(post.title.contains(keyword)
                    .or(post.content.contains(keyword))
                    .or(post.location.contains(keyword)));
        }

        if (!StringUtils.isEmpty(district)) {
            builder.and(post.location.contains(district));
        }

        OrderSpecifier<?> sortOrder = getSortOrder(sorting, post);

        QueryResults<Post> results = queryFactory
                .selectFrom(post)
                .where(builder)
                .orderBy(sortOrder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<MainPageResponseDto> dtos = results.getResults().stream()
                .map(MainPageResponseDto::new) // Post를 MainPageResponseDto로 변환
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, results.getTotal());
    }

    private OrderSpecifier<?> getSortOrder(String sorting, QPost post) {
        switch (sorting) {
            case "최근 게시물 순":
                return post.createdAt.desc();
            case "높은 가격 순":
                return post.price.desc();
            case "낮은 가격 순":
                return post.price.asc();
            default:
                return post.likeCount.desc().nullsLast();
        }

    }
}
