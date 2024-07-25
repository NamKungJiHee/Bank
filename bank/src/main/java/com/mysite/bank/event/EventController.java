package com.mysite.bank.event;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mysite.bank.email.MailService;
import com.mysite.bank.users.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/bank")
public class EventController {
	
	@GetMapping("/roulette")
	public String roulette() {
		return "roulette_form";
	}
}
