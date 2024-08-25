package com.mysite.bank;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

public class CustomSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
	                                    Authentication authentication) throws IOException, ServletException {
	    
		String accountId = request.getParameter("accountId");
	    if (accountId != null) {
	        System.out.println("Custom_accountId: " + accountId);
	      
	        String redirectUrl = "/?accountId=" + encodeURIComponent(accountId);
	        response.sendRedirect(redirectUrl);
	    } else {
	        response.sendRedirect("/");
	    }
	}

	private String encodeURIComponent(String value) throws IOException {
	    return URLEncoder.encode(value, "UTF-8");
	}
}
