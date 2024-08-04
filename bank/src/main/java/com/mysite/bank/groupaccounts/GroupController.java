package com.mysite.bank.groupaccounts;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	@PostMapping("/selectAccount")
    public String selectAccount(@RequestParam("accountId") Long accountId, Model model) {
        accountInfoService.updateIsGroupaccount(accountId);
        model.addAttribute("accountId", accountId); 
//        return "redirect:/bank/groupName";     # redirect 시 model로 저장한 값이 파라미터로 안넘어감.
        return "groupname_form";
    }
	 
	@GetMapping("/groupName")
	public String groupName() {
		return "groupname_form";
	}
	
	@PostMapping("/groupName")
	public String saveGroupName(@RequestParam("groupname") String groupName, @RequestParam("accountId") Long accountId) {
		groupService.save(groupName, accountId);
	    return "selectLocker_form";
	}
}
