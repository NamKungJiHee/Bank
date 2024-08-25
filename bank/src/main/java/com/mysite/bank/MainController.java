package com.mysite.bank;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {

	@GetMapping("/")
	public String root(@RequestParam(value= "accountId", required = false) String accountId, Model model) {
		
		System.out.println("#####MainController: " + accountId);
		 if (accountId != null) {
	            model.addAttribute("accountId", accountId);
	        }
		return "homepage";
	}
}
