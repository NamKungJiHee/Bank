package com.mysite.bank.users;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Optional;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.mysite.bank.users.UsersRepository;
import com.mysite.bank.users.Users;
import com.mysite.bank.IncorrectPasswordException;
import com.mysite.bank.UserNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
	private final UsersRepository usersRepository;
	private final PasswordEncoder passwordEncoder;	
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	// 중복ID 체크
	public boolean isUsernameAvailable(String username) {
		boolean isAvailable;
	    Optional<Users> userOptional = this.usersRepository.findByUserName(username);
	    if (!userOptional.isPresent()) {
	    	isAvailable = true;
	    } else {
	    	isAvailable = false;
	    }
	    return isAvailable;
	}
	
	public Users create(String username, String usernickname, String email, String password) {
		Users user = new Users();
		user.setUserName(username);
		user.setUserNickname(usernickname);
		user.setEmail(email);
		String encryptedPassword = passwordEncoder.encode(password); // 비밀번호를 암호화
		user.setPassword(encryptedPassword);
		this.usersRepository.save(user);
		return user;
	}
	
   public String findEmail(String email) {
	      Optional<Users> _siteUser = this.usersRepository.findByEmail(email);
	      if (!_siteUser.isPresent()) {
	    	  return null; 
	      }
	      String username = _siteUser.get().getUserName();
	      return username;
	   }
   
   // 임시 비번이 맞는지 확인
   public boolean checkPassword(String username, String password) {
	    Optional<Users> siteUserOptional = this.usersRepository.findByUserName(username);
	    if (siteUserOptional.isPresent()) {
	    	Users siteUser = siteUserOptional.get();
	        String storedPassword = siteUser.getPassword();
	        if (bCryptPasswordEncoder.matches(password, storedPassword)) {
	            return true;
	        } else {
	            throw new IncorrectPasswordException("비밀번호가 일치하지 않습니다.");
	        }
	    } else {
	        throw new UserNotFoundException("해당 사용자 이름으로 등록된 사용자가 없습니다.");
	    }
	}
   
   // 카카오 로그인
   private final ApplicationEnvironmentConfig envConfig;
   
   public String getKakaoAuthorizeUrl(String type) throws URISyntaxException, MalformedURLException, UnsupportedEncodingException {

       String baseUrl = "https://kauth.kakao.com/oauth/authorize";
       String clientId = envConfig.getKakaoClientId();
       String redirectUrl = envConfig.getKakaoRedirectUrl();

       String encodedRedirectUrl = URLEncoder.encode(redirectUrl, "UTF-8");
       
       UriComponents uriComponents = UriComponentsBuilder
               .fromUriString(baseUrl)
         
               .queryParam("client_id", clientId)
               .queryParam("redirect_uri", encodedRedirectUrl)
               .queryParam("response_type", "code")
               .build();
	        return uriComponents.toUriString();
	    }
   
   public Optional<Users> findByEmail(String email) {
       return usersRepository.findByEmail(email);
   }
   
   public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       Optional<Users> userOptional = usersRepository.findByUserName(username); 
       Users user = userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
       return new CustomUserDetails(
               user.getUserName(),
               "", 
               user.getUserNickname(),
               AuthorityUtils.createAuthorityList("ROLE_USER") // 권한
       );
   }
}
