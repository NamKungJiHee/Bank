package com.mysite.bank;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.mysite.bank.accountinfo.AccountInfoRepository;
import com.mysite.bank.event.EventRepository;
import com.mysite.bank.friend.FriendRepository;
import com.mysite.bank.friend.FriendService;
import com.mysite.bank.groupaccountmembers.GroupAccountMembersRepository;
import com.mysite.bank.groupaccounts.GroupAccountRepository;
import com.mysite.bank.safelockers.SafeLockersRepository;
import com.mysite.bank.users.CustomUserDetails;
import com.mysite.bank.users.Users;
import com.mysite.bank.users.UsersRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Optional;

public class CustomSuccessHandler implements AuthenticationSuccessHandler {
	private UsersRepository usersRepository;
    private FriendService friendService;

    public CustomSuccessHandler(UsersRepository usersRepository, FriendService friendService) {
        this.usersRepository = usersRepository;
        this.friendService = friendService;
    }

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
	                                    Authentication authentication) throws IOException, ServletException {
	    
		CustomUserDetails auth= (CustomUserDetails) authentication.getPrincipal();
		String userName = auth.getUsername();
		String accountId = request.getParameter("accountId");

		 if (accountId != null && !accountId.trim().isEmpty()) { 
		        try {
		            Long accountIdLong = Long.parseLong(accountId);
		            friendService.saveInvitedFriend(userName, accountIdLong);
		            
		            String redirectUrl = "/?accountId=" + encodeURIComponent(accountId);
		            response.sendRedirect(redirectUrl);
		        } catch (NumberFormatException e) {
		            response.sendRedirect("/errorPage"); 
		        }
		    } else {
		        response.sendRedirect("/");
		    }
	}

	private String encodeURIComponent(String value) throws IOException {
	    return URLEncoder.encode(value, "UTF-8");
	}
}
