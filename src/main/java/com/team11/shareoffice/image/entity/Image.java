package com.team11.shareoffice.image.entity;

import com.team11.shareoffice.post.entity.Post;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String imageUrl;

    @ManyToOne
    private Post post;

    public Image(Post post, String imageUrl) {
        this.post = post;
        this.imageUrl = imageUrl;
    }

}