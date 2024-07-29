package com.mysite.bank.event;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mysite.bank.email.MailService;
import com.mysite.bank.users.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/bank")
public class EventController {
	
	private final EventService eventInfoService;
	
	@GetMapping("/roulette")
	public String roulette(Principal principal, Model model) {
		String userName = principal.getName();

		 boolean hasParticipated = eventInfoService.hasUserParticipated(userName);
		 
		 model.addAttribute("hasParticipated", hasParticipated);
		
		return "roulette_form";
	}
	
	// 룰렛돌리기 결과 정보 db에 저장
	@PostMapping("/saveResult") 
	@ResponseBody
	public String saveResult (@RequestBody Map<String, String> requestBody, Principal principal) {
		String result = requestBody.get("result");
		//System.out.println("Result:: " + result); //  {result=추가이자율 1%지급}
		String userName = principal.getName();
		// System.out.println("UseName:: " + userName); // judy
		int num = 1;
		Long numEvent = Long.valueOf(num);
		
		Event eventInfo = eventInfoService.eventInfo(userName, result, numEvent);
		
		return "roulette_form";
	}
	
}
