package com.mysite.bank.safelockers;

import java.security.Principal;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/bank")
public class SafeLockersController {
    private final SafeLockersService safeLockerService;

    @GetMapping("/withdraw")
    public String getWithdrawPage(@RequestParam("accountId") Long accountId, Model model) {
      
        Map<String, Object> lockerInfo = safeLockerService.getLockerType(accountId);
        
        model.addAttribute("lockerType", lockerInfo.get("lockerType"));
        model.addAttribute("currentBalance", lockerInfo.get("currentBalance"));
        model.addAttribute("accountId", accountId); 
        
        return "withdrawGroupAccount"; 
    }
    
    @PostMapping("/sendBalance")
    public String postWithdraw(@RequestParam("withdrawBalance") Long withdrawBalance, @RequestParam("accountId") Long accountId, Model model, Principal principal) {
    	String userName = principal.getName();
        Long afterBalanceInterest = safeLockerService.sendBalance(withdrawBalance, accountId, userName);
    
        model.addAttribute("currentBalance", afterBalanceInterest);     
        model.addAttribute("accountId", accountId); 

        return "withdrawGroupAccount"; 
    }
}
