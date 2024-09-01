package com.mysite.bank.event;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

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
		String eventName = null;
		
		 boolean hasParticipated = eventInfoService.hasUserParticipated(userName);
		 System.out.println("hasParticipated: " + hasParticipated);
		 
		 Optional<Event> event = eventInfoService.eventName(userName);
		 System.out.println("getEventName: " + event);
		 
		 if (!event.isEmpty()) {
			 eventName = event.get().getEventName();
			 System.out.println("EventName: " + eventName);
		 } else {
			 eventName = null;
			 System.out.println("EventName1: " + eventName);
		 }
		 
		 model.addAttribute("hasParticipated", hasParticipated);
		 model.addAttribute("eventName", eventName);
		
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
