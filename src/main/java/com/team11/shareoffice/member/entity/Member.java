package com.team11.shareoffice.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

@Entity(name = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@Where(clause = "is_delete = false")
public class Member{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column
    private Long kakaoId;

    @Column
    private boolean isDelete;

//    @OneToMany(mappedBy = "member", orphanRemoval = true)
//    private List<Post> posts;
//
//    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
//    private List<Likes> likes;

    public Member(String email, Long kakaoId, String password, String nickname){
        this.email = email;
        this.kakaoId = kakaoId;
        this.password = password;
        this.nickname = nickname;
    }

    @Builder
    public Member(String email, String password, String nickname){
        this.email = email;
        this.password = password;
        this.nickname = nickname;
//        this.isDelete = isDelete;
    }

    public Member kakaoIdUpdate(Long kakaoId) {
        this.kakaoId = kakaoId;
        return this;
    }
//    public void softDelete() {
//        this.isDelete = true;
//    }

}
