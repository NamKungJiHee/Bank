package com.mysite.bank.users;

import java.util.Optional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mysite.bank.users.UsersRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
	private final UsersRepository usersRepository;
	private final PasswordEncoder passwordEncoder;	
	
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
}
