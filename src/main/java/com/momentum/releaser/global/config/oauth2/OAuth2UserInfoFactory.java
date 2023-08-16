package com.momentum.releaser.global.config.oauth2;


import com.momentum.releaser.global.config.oauth2.social.GoogleOAuth2User;
import com.momentum.releaser.global.config.oauth2.social.KakaoOAuth2User;
import com.momentum.releaser.global.jwt.AuthProvider;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(AuthProvider authProvider, Map<String, Object> attributes) {
        switch (authProvider) {
            case GOOGLE: return new GoogleOAuth2User(attributes);
            case KAKAO: return new KakaoOAuth2User(attributes);

            default: throw new IllegalArgumentException("Invalid Provider Type.");
        }
    }
}