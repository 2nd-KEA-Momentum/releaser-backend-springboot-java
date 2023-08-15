package com.momentum.releaser.global.config.oauth2.user;


import com.momentum.releaser.global.jwt.AuthProvider;
import com.momentum.releaser.global.security.OAuth2AuthenticationProcessingException;

import java.util.Map;

public class OAuth2UserInfoFactory {

    private OAuth2UserInfoFactory(){}

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if(registrationId.equalsIgnoreCase(AuthProvider.GOOGLE.toString())) {
            return new GoogleOAuth2UserInfo(attributes);
        }
        else if (registrationId.equalsIgnoreCase(AuthProvider.KAKAO.toString())) {
            return new KakaoOAuth2UserInfo(attributes);
        }

        throw new OAuth2AuthenticationProcessingException("죄송합니다. " + registrationId + " 로그인은 지원하지 않습니다.");
    }
}