package com.team11.shareoffice.post.entity;

import com.team11.shareoffice.member.entity.Member;
import com.team11.shareoffice.post.dto.PostRequestDto;
import com.team11.shareoffice.post.dto.PostUpdateRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Entity(name = "posts")
@Getter
@Setter
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

    @Column(nullable = false)
    private int capacity;  //
    @Column(nullable = false)
    private String operatingTime; //
    @Column(nullable = false)
    private String contentDetails; //
    @Column(nullable = false)
    private String amenities; //

    //좋아요 개수
    @Column(nullable = false)
    @ColumnDefault("0")
    private int likeCount;

    @Column(columnDefinition = "LONGTEXT")
    @Lob
    private String postImage;

    @Column
    private boolean isDelete;

    public Post (PostRequestDto requestDto, Member member){
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.location = requestDto.getLocation();
        this.price = requestDto.getPrice();
        this.capacity = requestDto.getCapacity();
        this.operatingTime = requestDto.getOperatingTime().replace("\\n", "\n");
        this.contentDetails = requestDto.getContentDetails().replace("\\n", "\n");
        this.amenities = requestDto.getAmenities().replace("\\n", "\n");
        this.member = member;
    }

    public void updatePost (PostUpdateRequestDto requestDto){
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.location = requestDto.getLocation();
        this.price = requestDto.getPrice();
        this.capacity = requestDto.getCapacity();
        this.operatingTime = requestDto.getOperatingTime();
        this.contentDetails = requestDto.getContentDetails();
        this.amenities = requestDto.getAmenities();
    }

    public void updateLike(Boolean likeOrDislike) {
        this.likeCount = Boolean.TRUE.equals(likeOrDislike) ? this.likeCount + 1 : this.likeCount - 1;
    }
}
