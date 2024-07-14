package com.mysite.bank.users;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationEnvironmentConfig {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUrl;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoClientSecret;

    
    public String getKakaoClientId() {
        return kakaoClientId;
    }

    public String getKakaoClientSecret() {
        return kakaoClientSecret;
    }
    
    public String getKakaoRedirectUrl() {
        return kakaoRedirectUrl;
    }
}