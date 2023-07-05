package com.momentum.releaser.domain.user.domain;

import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.global.common.BaseTime;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE user SET status = 'N' WHERE user_id=?")
@Where(clause = "status = 'Y'")
@Table(name = "user")
@Entity
public class User extends BaseTime {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotNull
    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "email")
    private String email;

    @Column(name = "img")
    private String img;

    @NotNull
    @Column(name = "status")
    private char status;

    @OneToOne(mappedBy = "user")
    private AuthSocial authSocial;

    @OneToOne(mappedBy = "user")
    private AuthPassword authPassword;

    @OneToMany(mappedBy = "user")
    private List<ProjectMember> members = new ArrayList<>();


    @Builder
    public User(String name, String email, String img, char status) {
        this.name = name;
        this.email = email;
        this.img = img;
        this.status = status;
    }

    /**
     * insert 되기전 (persist 되기전) 실행된다.
     */
    @PrePersist
    public void prePersist() {
        this.status = (this.status == '\0') ? 'Y' : this.status;
    }
}
