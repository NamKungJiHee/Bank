package com.mysite.bank.kakao;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import com.mysite.bank.kakao.KakaoLoginService;
import com.mysite.bank.users.UserService;
import com.mysite.bank.users.ApplicationEnvironmentConfig;
import com.mysite.bank.users.UsersRepository;
import com.mysite.bank.users.Users;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KakaoLoginService {
	private final UsersRepository userRepository;
	private final ApplicationEnvironmentConfig envConfig;
	private final RestTemplate restTemplate = new RestTemplate();
	private final UserDetailsService userDetailsService;
	
	public String getAccessToken(String code) {
		// 카카오로부터 AccessToken 발급요청
		String tokenRequestUrl = "https://kauth.kakao.com/oauth/token";
		String clientId = envConfig.getKakaoClientId();
		String clientSecret = envConfig.getKakaoClientSecret();
		String redirectUrl = envConfig.getKakaoRedirectUrl();
		
		LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.set("grant_type", "authorization_code");
		params.set("client_id", clientId);
		params.set("redirect_uri", redirectUrl);
		params.set("client_secret", clientSecret);
		params.set("code", code);
		
		// HTTP 요청 보내기
		String response = restTemplate.postForObject(tokenRequestUrl, params, String.class);
		return response;
	}
	
	public String getUserInfo(String accessToken) {
		// 사용자 정보 return
		String RequestUrl = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
 
        // JSON 문자열을 JsonObject로 변환
        JsonObject jsonObject = JsonParser.parseString(accessToken).getAsJsonObject();

        // "access_token" 키에 해당하는 값 추출
        String parseToken = jsonObject.get("access_token").getAsString();

        headers.set("Authorization", "Bearer " + parseToken);
        System.out.println("headers: " + headers);
    
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        String response = restTemplate.postForEntity(RequestUrl, request, String.class).getBody();

        return response;
	}
	
	public void saveUserInfo(String email, String name) {
		Users user = new Users();
		user.setEmail(email);
		user.setUserName(name);
		user.setUserNickname(name);
		user.setPassword("defaultPassword");
		
		this.userRepository.save(user);
	}
}
