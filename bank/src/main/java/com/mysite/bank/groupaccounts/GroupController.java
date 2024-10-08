package com.mysite.bank.groupaccounts;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mysite.bank.accountinfo.AccountInfo;
import com.mysite.bank.accountinfo.AccountInfoService;
import com.mysite.bank.friend.Friend;
import com.mysite.bank.friend.FriendService;
import com.mysite.bank.groupaccountmembers.GroupAccountMembers;
import com.mysite.bank.transfer.TransferService;
import com.mysite.bank.users.Users;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/bank")
public class GroupController {
	private final AccountInfoService accountInfoService;
	private final GroupService groupService;
	private final FriendService friendService;
	private final TransferService transferService;
//	private boolean noLocker = true;
    private boolean premiumNoLocker = true; 

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
	public String saveGroupName(@RequestParam("groupname") String groupName, @RequestParam("accountId") Long accountId, Principal principal,  HttpSession session) {
		String userName = principal.getName(); 
		groupService.save(groupName, accountId, userName);
		
		session.setAttribute("accountId", accountId);
		
	    return "redirect:/bank/selectLocker";
	}
	
	@GetMapping("/selectLocker")
	public String selectLocker(@SessionAttribute("accountId") Long accountId) {

		return "selectLocker_form";
	}
	
	@GetMapping("/lockerInfo")
	public String lockerInfo() {
		return "lockerInfo_form";
	}
	
	// safeLocker type db저장
	@PostMapping("selectlockerType")
	public String saveLockerType(@RequestParam("lockerType") String lockerType, @SessionAttribute("accountId") Long accountId) {
		groupService.saveLocker(lockerType, accountId);
		
		if (lockerType.equals("None")) {
			return "redirect:/bank/groupAccountInfo";
		} else {
			return "redirect:/bank/setBalance"; 
		}
//		return "redirect:/bank/setBalance";
	}
	
	@GetMapping("/setBalance")
	public String setBalance() {
		return "setLockerBalance_form";
	}
	
	@PostMapping("/setStandard")
	public String setStandard(@RequestParam("moveSafeLocker") Long moveSafeLocker, @RequestParam("alertSafeLocker") Long alertSafeLocker, @SessionAttribute("accountId") Long accountId, Principal principal) {	
		String userName = principal.getName(); 
		groupService.lockerStandard(moveSafeLocker, alertSafeLocker, accountId, userName);
		return "redirect:/bank/applyEvent";
	}
	
	@GetMapping("/applyEvent")
	public String applyEvent(Model model, Principal principal) {
		String userName = principal.getName(); 
		
		String eventName = groupService.eventResult(userName);
		model.addAttribute("eventName", eventName);
		return "applyEvent_form";
	}
	
	@PostMapping("/watchAccount")
	public String watchAccount() {
		return "redirect:/bank/groupAccountInfo";
	}
	
	// 내 모임통장 보기
	@GetMapping("/groupAccountInfo")
	public String groupAccountInfo(Model model, Principal principal) {
	    String userName = principal.getName();
	    Long userId = groupService.userId(userName);

	    List<Map<String, Object>> groupAccountInfos = new ArrayList<>();
	
	    List<Friend> inviteUsers = friendService.findInvitedId(userId);
	    List<GroupAccountMembers> groupAccountMembers = groupService.findGroupAccountMembers(userName);

	    if ((!inviteUsers.isEmpty()) && (groupAccountMembers != null)) {
	    	
	    	for (Friend inviteUser : inviteUsers) {
	            Long groupAccountId = inviteUser.getGroupAccountId().getGroupAccountId();

	            GroupAccount invitedAccountInfo = groupService.getGroupAccountInfoByGroupAccountId(groupAccountId);
	            Long currentBalanceInfo = groupService.getCurrentBalance(invitedAccountInfo);

	            Map<String, Object> invitedAccountMap = new HashMap<>();
	            invitedAccountMap.put("safeLockerType", invitedAccountInfo.getSafelockerType());
	            invitedAccountMap.put("groupBalance", invitedAccountInfo.getBalance());
	            invitedAccountMap.put("safeLockerThreshold", invitedAccountInfo.getSafelockerThreshold());
	            invitedAccountMap.put("groupName", invitedAccountInfo.getGroupName());
	            invitedAccountMap.put("alertThreshold", invitedAccountInfo.getAlertThreshold());
	            invitedAccountMap.put("accountNum", invitedAccountInfo.getAccountInfo().getAccountNum());
	            invitedAccountMap.put("accountId", invitedAccountInfo.getAccountInfo().getAccountId());
	            invitedAccountMap.put("currentBalance", currentBalanceInfo);

	            groupAccountInfos.add(invitedAccountMap);

	        }
            
            List<Map<String, Object>> additionalGroupAccounts = groupService.getGroupAccountInfosByUserId(userId);
            groupAccountInfos.addAll(additionalGroupAccounts);
            
	    } else {
	        groupAccountInfos = groupService.getGroupAccountInfosByUserId(userId);
	    }

	    model.addAttribute("groupAccountInfos", groupAccountInfos);
	    
	    Map<Long, Boolean> noLockerStatus = new HashMap<>(); 
	    
	    boolean noLocker = false;
	    for (Map<String, Object> result : groupAccountInfos) {
	        String lockerType = (String) result.get("safeLockerType");
	        Long groupBalance = (Long) result.get("groupBalance");
	        Long safeLockerThreshold = (Long) result.get("safeLockerThreshold");
	        Long currentBalance = (Long) result.get("currentBalance");
	        Long accountId = (Long) result.get("accountId");
	        
	        if ("None".equals(lockerType)) {
	            noLocker = true;
	        } else if ("Flex".equals(lockerType)) {
	            noLocker = false;
	        } else if ("Premium".equals(lockerType)) {
	            noLocker = premiumNoLocker;
	        }
		    
	        noLockerStatus.put(accountId, noLocker);
	        
	        if ("Flex".equals(lockerType) || "Premium".equals(lockerType)) {
	            if (groupBalance >= safeLockerThreshold) {
	                Long updatedBalance = groupBalance + currentBalance;
	                Long initGroupBalance = 0L;

	                model.addAttribute("groupBalance", initGroupBalance);
	                model.addAttribute("currentBalance", updatedBalance);

	                groupService.updateBalance(initGroupBalance, updatedBalance, (Long) result.get("accountId"));
	            }
	        }
	    }
	    model.addAttribute("noLockerStatus", noLockerStatus);
	    return "accountInfo_form";
	}
	
  @Scheduled(cron = "0 0 0 1 * ?") // 매달 1일에 false로 설정
  	public void resetNoLockerMonthly() {
        premiumNoLocker = false;
    }
	
  @Scheduled(cron = "0 0 1 1 * ?") // 매달 1일 1시 이후로 true로 설정
	public void setNoLockerTrueAfterFirst() {
        premiumNoLocker = true;
    }
  
  	// 자세히 파트
	@GetMapping("/specific")
	public String specific(Model model, Principal principal, @RequestParam("accountId") Long accountId) {
		String userName = principal.getName(); 
		String currentNickName = groupService.userNickName(userName);
		Map<String, Object> result = groupService.groupAccountInfo(accountId);
		
		List<String> inviteMember = groupService.getGroupAccount(accountId); // 모임원(inviter 입장)
		List<String> originMemberName = groupService.getOriginMember(accountId); // 모임주
		List<String> invitedMembers = Arrays.asList(currentNickName); // 모임원(invited 입장)
		
	    List<String> allMembers = new ArrayList<>(originMemberName);
	    allMembers.addAll(invitedMembers);
	    allMembers.addAll(inviteMember);
	    allMembers = allMembers.stream().distinct().collect(Collectors.toList());
	    
		model.addAttribute("groupName", result.get("groupName"));
		model.addAttribute("groupBalance", result.get("groupBalance"));
		model.addAttribute("accountNum", result.get("accountNum"));
		
		model.addAttribute("members", allMembers);
		model.addAttribute("accountId", accountId);
		
		// specific 거래내역
		List<Map<String, Object>>  history = transferService.transactionHistory(accountId);
		model.addAttribute("transactions", history);
		
		return "specific_form";
	}
	
}
