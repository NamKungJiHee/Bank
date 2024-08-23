package com.mysite.bank.friend;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mysite.bank.event.EventService;
import com.mysite.bank.groupaccounts.GroupService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/bank")
public class FriendController {
	
	 private final FriendService friendService;

	    public FriendController(FriendService friendService) {
	        this.friendService = friendService;
	    }
	   
	    @PostMapping("/inviteMsg")
	    public ResponseEntity<String> inviteFriend(@RequestParam("groupName") String groupName, @RequestParam("accountNum") String accountNum, @RequestParam("groupBalance") String groupBalance, @RequestParam("nickName") String nickName,@RequestParam("accountId") String accountId,  Model model, Principal principal) {
	        String userName = principal.getName();
	        friendService.save(userName);

	        return ResponseEntity.ok("Success");
	    }
}
