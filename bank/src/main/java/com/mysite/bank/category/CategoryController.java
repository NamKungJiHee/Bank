package com.mysite.bank.category;

import java.security.Principal;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import com.mysite.bank.accountinfo.AccountInfo;
import com.mysite.bank.accountinfo.AccountInfoService;
import com.mysite.bank.email.MailService;
import com.mysite.bank.users.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/bank")
public class CategoryController {
	private final AccountInfoService accountInfoService;
	
	@GetMapping("/category")
	public String categoryList() {
		return "category_form";
	}
	
	@GetMapping("/accountInfo")
    public String selectAccountInfo(Model model, Principal principal, HttpServletRequest request) {
        String userName = principal.getName(); 
        String referer = request.getHeader("Referer"); // 이전 url
  
        List<AccountInfo> accountInfos = accountInfoService.findAccountsByUsername(userName);
        model.addAttribute("accountInfos", accountInfos);
        
        if (referer.contains("/selectAccount") || (referer.contains("/agreementsetting"))) {
        	// System.out.println("Referer1::   " + referer);
        	boolean show = false;
        	model.addAttribute("showBtn", show);
        } else {
        	// System.out.println("Referer2::   " + referer);
        	boolean show = true;
        	model.addAttribute("showBtn", show);
        }
        return "selectAccount_form";
    }
	
	// 입출금통장 개설
	@GetMapping("/checkingAccount")
	public String checkingAccount() {
		return "checkAccount_form";
	}
	
	@GetMapping("/agreement")
	public String agreement() {
		return "agreement_form";
	}
	
	@GetMapping("/agreementsetting")
	public String agreementSetting() {
		return "agreementSet_form";
	}
	
	// 통장 비밀번호
	 @PostMapping("/accountpwd")
	    public String accountPwd(@RequestParam("accountpwd") Long accountpwd, Principal principal, Model model, HttpServletRequest request) {
	        String userName = principal.getName(); 
	        AccountInfo accountInfo = accountInfoService.create(accountpwd, userName);
	        
	        HttpSession session = request.getSession();

	        // 세션에서 URL 리스트 가져오기
	        List<String> urlList = (List<String>) session.getAttribute("urlList");

	        // 2가지 루트
	        // 1. selectAccount(모임통장 만들기) url을 거쳤다면, /bank/accountInfo로
	        // 2. 그 이외의 경우, 홈으로 redirect
	        String redirectUrl;
	        if (urlList != null && urlList.contains(request.getRequestURL().toString().replace("accountpwd", "selectAccount"))) {
	            redirectUrl = "redirect:/bank/accountInfo";
	        } else {
	            redirectUrl = "redirect:/";
	        }

	        return redirectUrl;
	 }
}

