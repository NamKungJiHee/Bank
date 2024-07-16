package com.mysite.bank.category;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mysite.bank.email.MailService;
import com.mysite.bank.users.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/bank")
public class CategoryController {
	@GetMapping("/category")
	public String categoryList() {
		return "category_form";
	}
}

