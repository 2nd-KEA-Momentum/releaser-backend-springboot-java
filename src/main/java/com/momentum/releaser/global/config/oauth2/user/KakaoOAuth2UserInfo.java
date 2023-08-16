package com.momentum.releaser.global.config.oauth2.user;

import java.util.Map;

public class KakaoOAuth2UserInfo  extends OAuth2UserInfo {

    private final Map<String, Object> kakaoAccountAttributes;
    private final Map<String, Object> profileAttributes;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
        this.kakaoAccountAttributes = (Map<String, Object>) attributes.get("kakao_account");
        this.profileAttributes = (Map<String, Object>) attributes.get("profile");
    }

    @Override
    public String getId() {
        return attributes.get("sub").toString();
    }

    @Override
    public String getName() {
        return profileAttributes.get("nickname").toString();
    }

    @Override
    public String getEmail() {
        return kakaoAccountAttributes.get("email").toString();
    }

    @Override
    public String getImageUrl() {
        return profileAttributes.get("image").toString();
    }
}
