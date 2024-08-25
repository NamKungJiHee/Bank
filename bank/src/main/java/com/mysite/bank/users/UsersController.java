package com.mysite.bank.users;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import javassist.tools.Callback;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.Principal;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mysite.bank.users.UserCreateForm;
import com.mysite.bank.users.UserPwdForm;
import com.mysite.bank.email.MailService;

@RequiredArgsConstructor
@Controller
@RequestMapping("/bank")
public class UsersController {
	private final UserService userService;
	private final MailService mailService;
	
	// 일반 로그인, 카카오 로그인(accountId)
	@GetMapping("/login")
	public String login(@RequestParam(value = "accountId", required = false) String accountId, Model model) {

		model.addAttribute("accountId", accountId); 
		
		return "login_form";
	}
	
	@GetMapping("/signup")
	public String signup(UserCreateForm userCreateForm) {
		return "signup_form";
	}
	
	@PostMapping("/signup")
	public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "signup_form";
		}
		if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
			bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 비밀번호가 일치하지 않습니다.");
			return "signup_form";
		}
		try {
		userService.create(userCreateForm.getUserName(), userCreateForm.getUserNickname(), userCreateForm.getEmail(), userCreateForm.getPassword1());
		} catch(DataIntegrityViolationException e) {
			e.printStackTrace();
			bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
			return "signup_form";
		} catch(Exception e) {
			e.printStackTrace();
			bindingResult.reject("signupFailed", e.getMessage());
			return "signup_form";
		}
		return "redirect:/";
	}
	
	// 중복ID 체크
	@GetMapping("/checkusername")
    public ResponseEntity<Boolean> checkUsername(@RequestParam("username") String username) {
		boolean isAvailable = userService.isUsernameAvailable(username);
		return ResponseEntity.ok(isAvailable);
	}
	
	// 아이디찾기
	@GetMapping("/findid")
	public String findid() {
		return "findid_form";
	}
	
	@PostMapping("/findid")
	   public String findid(Model model, @RequestParam("email") String email) {
	      String username = this.userService.findEmail(email);
	      if (username != null) {
	            model.addAttribute("username", username);
	        } else {
	            model.addAttribute("error", "존재하지 않는 이메일입니다. 다시 확인해주세요.");
	        }
        return "findid_form";
	}
	
	//비밀번호 찾기
   @GetMapping("/findpwd")
   public String findpwd() {
	   return "findpwd_form";
   }
   
   //비밀번호 변경
   @GetMapping("/changepwd")
   public String changepwd(UserPwdForm userPwdForm) {
	   return "change_pwd";
   }
   
   @PostMapping("/changepwd")
	public String changepwd(@RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("newPassword1") String newPassword1, @RequestParam("newPassword2") String newPassword2, @Valid UserPwdForm userPwdForm, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "change_pwd";
		}
		if (!userPwdForm.getNewPassword1().equals(userPwdForm.getNewPassword2())) {
			bindingResult.rejectValue("newPassword2", "passwordInCorrect", "2개의 비밀번호가 일치하지 않습니다.");
			return "change_pwd";
		}
		if (!this.userService.checkPassword(username, password)) {
	        return "change_pwd";
	    }
		this.mailService.updateNewPassword(username, password, newPassword1);
		return "redirect:/";
	}
  
}
