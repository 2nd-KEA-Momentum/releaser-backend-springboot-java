package com.momentum.releaser.domain.user.domain;

import com.momentum.releaser.global.common.BaseTime;
import com.momentum.releaser.global.config.oauth2.OAuth2UserInfo;
import com.momentum.releaser.global.jwt.AuthProvider;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE auth_social SET status = 'N' WHERE auth_id=?")
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

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @Column(name = "oauth2Id")
    private String oauth2Id;

    @NotNull
    @Column(name = "status")
    private char status;

    @Builder
    public AuthSocial(Long authId, User user, AuthProvider provider, String oauth2Id, char status) {
        this.authId = authId;
        this.user = user;
        this.provider = provider;
        this.oauth2Id = oauth2Id;
        this.status = status;
    }



    /**
     * insert 되기전 (persist 되기전) 실행된다.
     */
    @PrePersist
    public void prePersist() {
        this.status = (this.status == '\0') ? 'Y' : this.status;
    }


    /**
     * 삭제를 위한 status ='N' 변경
     */
    public void statusToInactive() {
        this.status = 'N';
    }

    public void updateInfo(OAuth2UserInfo oAuth2UserInfo) {
        this.oauth2Id = oAuth2UserInfo.getOAuth2Id();
    }

}
