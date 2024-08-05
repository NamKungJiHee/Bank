package com.mysite.bank.groupaccounts;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mysite.bank.accountinfo.AccountInfoService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/bank")
public class GroupController {
	private final AccountInfoService accountInfoService;
	private final GroupService groupService;
	
	@GetMapping("/creategroupAccount")
	public String createGroupAccount() {
		
		return "createGroupAccount_form";
	}
	
	@GetMapping("/selectAccount")
	public String selectGroupAccount(HttpServletRequest request) {
		
		// 현재 URL
        String currentUrl = request.getRequestURL().toString();
//        System.out.println("CurrentUrl:: " + currentUrl); // http://localhost:8080/bank/selectAccount
        HttpSession session = request.getSession();

        List<String> urlList = (List<String>) session.getAttribute("urlList");
        
        if (urlList == null) {
            urlList = new ArrayList<>();
        }
        urlList.add(currentUrl);
        
        // session에 저장
        session.setAttribute("urlList", urlList);
        
		return "selectGroupAccount_form";
	}
	
	// 계좌 선택하기
	@PostMapping("/chooseAccount")
    public String selectAccount(@RequestParam("accountId") Long accountId, RedirectAttributes redirectAttributes) {
        accountInfoService.updateIsGroupaccount(accountId);
        redirectAttributes.addFlashAttribute("accountId", accountId);
        return "redirect:/bank/groupName";     // # redirect 시 model로 저장한 값은 파라미터로 안넘어감.
//        return "groupname_form";
    }
	 
	@GetMapping("/groupName")
	public String groupName() {
		return "groupname_form";
	}
	
	@PostMapping("/chooseGroupName")
	public String saveGroupName(@RequestParam("groupname") String groupName, @RequestParam("accountId") Long accountId, Principal principal) {
		String userName = principal.getName(); 
		groupService.save(groupName, accountId, userName);
	    return "redirect:/bank/selectLocker";
	}
	
	@GetMapping("/selectLocker")
	public String selectLocker() {
		return "selectLocker_form";
	}
	
	@GetMapping("/lockerInfo")
	public String lockerInfo() {
		return "lockerInfo_form";
	}
	
	// safeLocker type db저장

}
