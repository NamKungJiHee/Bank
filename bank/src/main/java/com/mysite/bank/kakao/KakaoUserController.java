package com.mysite.bank.kakao;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysite.bank.users.UserService;
import com.mysite.bank.users.Users;
import com.mysite.bank.users.ApplicationEnvironmentConfig;
import com.mysite.bank.users.UsersRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class KakaoUserController {
	private final UserService userService;
	private final KakaoLoginService kakaoUserService;
	
	// Login with Kakao
   @GetMapping("/oauth2/authorization/kakao")
   public void naverLogin(HttpServletRequest request, HttpServletResponse response) throws MalformedURLException, UnsupportedEncodingException, URISyntaxException {
       String url = userService.getKakaoAuthorizeUrl("authorize");
       
       System.out.println("Url: " + url);
       // https://kauth.kakao.com/oauth/authorize?client_id=f9b97a640af704808f0f9599988f03ff&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Flogin%2Foauth2%2Fcode%2Fkakao&response_type=code
       try {
           response.sendRedirect(url);
           System.out.println("Resonse: " + url);
       // https://kauth.kakao.com/oauth/authorize?client_id=f9b97a640af704808f0f9599988f03ff&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Flogin%2Foauth2%2Fcode%2Fkakao&response_type=code
       } catch (Exception e) {
           e.printStackTrace();
           System.out.println("Error: " + e);
       }

   }
   
   @GetMapping("/login/oauth2/code/kakao")
	public String LoginKakao (HttpServletRequest request,@RequestParam("code") String code) throws JsonMappingException, JsonProcessingException {
		System.out.println("Code:  " + code);
		String accessToken = kakaoUserService.getAccessToken(code);
		System.out.println("accessToken:  " + accessToken);
		//{"access_token":"TjcnhoYl7oIoOGhltcEpwAr1d-6LC3b-AAAAAQoqJY4AAAGQr5Mc1m1lzvpaqIEo","token_type":"bearer","refresh_token":"ATo01myou7nlaq1KrSM3LGl4v579CXn-AAAAAgoqJY4AAAGQr5Mc021lzvpaqIEo","expires_in":21599,"scope":"profile_nickname","refresh_token_expires_in":5183999}
		
		String UserInfo = kakaoUserService.getUserInfo(accessToken);
		System.out.println("UserInfo" + UserInfo); 
		//{"id":3621318900,"connected_at":"2024-07-14T04:49:32Z","properties":{"nickname":"지히"},"kakao_account":{"profile_nickname_needs_agreement":false,"profile":{"nickname":"지히","is_default_nickname":false},"has_email":true,"email_needs_agreement":false,"is_email_valid":true,"is_email_verified":true,"email":"jihee9711@naver.com"}}
		
		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode userInfoNode = objectMapper.readTree(UserInfo);

       String email = userInfoNode.path("kakao_account").path("email").asText();
       String nickname = userInfoNode.path("properties").path("nickname").asText();
       System.out.println("Email: " + email);
       System.out.println("Nickname: " + nickname);
       
       // db에 존재하는 사용자일 경우에는 db저장 skip
       Optional<Users> existingUser = userService.findByEmail(email);
       System.out.println("existingUser:" + existingUser);
       Users user;
       if (existingUser.isPresent()) {
           user = existingUser.get();
           System.out.println("DB에 저장된 userName: " + user.getUserName());
           nickname= user.getUserName();
       } else {
           // 새로운 사용자 생성 및 저장
           System.out.println("새로운 사용자 생성 및 저장: " + nickname);
           kakaoUserService.saveUserInfo(email, nickname);
       }
    
       // 사용자를 현재 인증된 사용자로 설정
       UserDetails userDetails = userService.loadUserByUsername(nickname);

       // Authentication 객체 생성
       Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

       // SecurityContext에 Authentication 설정
       SecurityContextHolder.getContext().setAuthentication(authentication);

       // 세션에 Authentication 저장
       request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

       return "redirect:/";
	}
   
}
