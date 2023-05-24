package com.team11.shareoffice.image.entity;

import com.team11.shareoffice.post.entity.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)           // image 경로
    private String imgUrl;

    @OneToOne(fetch = FetchType.LAZY)
    private Post post;


//    public ImageFile(String imgUrl, Post post) {
//        this.imgUrl = imgUrl;
//        this.post = post;
//    }
}