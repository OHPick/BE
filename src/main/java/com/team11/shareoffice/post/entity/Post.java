package com.team11.shareoffice.post.entity;

import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.post.dto.PostRequestDto;
import com.team11.shareoffice.post.dto.PostUpdateRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity(name = "posts")
@Getter
@NoArgsConstructor
public class Post extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    // 게시글 제목
    @Column(nullable = false)
    private String title;
    // 게시글 내용
    @Column(nullable = false)
    private String content;
    // 주소
    @Column(nullable = false)
    private String location;
    // 임대료
    @Column(nullable = false)
    private int price;
    //좋아요 개수
    @Column(nullable = false)
    @ColumnDefault("0")
    private int likeCount;

    public Post (PostRequestDto requestDto, Member member){
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.location = requestDto.getLocation();
        this.price = requestDto.getPrice();
        this.member = member;
    }

    public void updatePost (PostUpdateRequestDto requestDto){
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.location = requestDto.getLocation();
    }
}
