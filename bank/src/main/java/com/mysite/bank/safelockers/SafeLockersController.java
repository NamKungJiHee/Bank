package com.mysite.bank.safelockers;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.mysite.bank.accountinfo.AccountInfoService;
import com.mysite.bank.groupaccounts.GroupService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/bank")
public class SafeLockersController {
	private final GroupService groupService;
	private final SafeLockersService safeLockerService;
	
	@GetMapping("/withdraw")
	public String setBalance(@SessionAttribute("accountId") Long accountId, Model model) {
		Map<String, Object> result = safeLockerService.getLockerType(accountId);
		
		model.addAttribute("lockerType", result.get("lockerType"));
		model.addAttribute("currentBalance", result.get("currentBalance"));
		
		return "withdrawGroupAccount";
	}
	
	@PostMapping("/sendBalance")
	public String sendBalance(@RequestParam("withdrawBalance") Long withdrawBalance, Model model, @SessionAttribute("accountId") Long accountId) {
		Long afterBalanceInterest = safeLockerService.sendBalance(withdrawBalance, accountId);
		
		model.addAttribute("currentBalance", afterBalanceInterest);
		return "withdrawGroupAccount";
	}
}
