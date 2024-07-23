package com.mysite.bank.category;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mysite.bank.accountinfo.AccountInfoService;
import com.mysite.bank.email.MailService;
import com.mysite.bank.users.UserService;

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
	public String selectAccountInfo() {
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
	public String accountPwd(@RequestParam("accountpwd") Long accountpwd ) {
		accountInfoService.create(accountpwd);
		
		return "redirect:/";
	}
}

