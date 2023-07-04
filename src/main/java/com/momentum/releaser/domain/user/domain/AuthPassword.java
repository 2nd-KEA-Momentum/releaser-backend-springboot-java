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
@Table(name = "auth_password")
@Entity
public class AuthPassword extends BaseTime {

    @Id
    @Column(name = "security_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long securityId;

    @NotNull
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Column(name = "password")
    private String password;

    @NotNull
    @Column(name = "status")
    private char status;

    @Builder
    public AuthPassword(User user, String password, char status) {
        this.user = user;
        this.password = password;
        this.status = status;
    }
}
