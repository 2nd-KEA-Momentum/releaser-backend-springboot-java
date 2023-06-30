package com.momentum.releaser.domain.user.domain;

import com.momentum.releaser.global.common.BaseTime;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "status = 'Y'")
@Table(name = "auth_social")
@Entity
public class AuthSocial extends BaseTime {

    @Id
    @Column(name = "auth_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authId;

    @NotNull
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Column(name = "type")
    private String type;

    @NotNull
    @Column(name = "token")
    private String token;

    @Builder
    public AuthSocial(User user, String type, String token) {
        this.user = user;
        this.type = type;
        this.token = token;
    }
}
