package app.service.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

public class AuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	@Override
	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
		// Get the role of logged in user
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String role = auth.getAuthorities().toString();
		
		request.getSession().setAttribute("currentUser", auth.getName());
		
		boolean isManager = false; 
		String targetUrl = "";
		if (role.contains("ADMIN")) {
			targetUrl = "/"; 
			isManager = true; 
		} else if (role.contains("USER")) {
			targetUrl = "/";
		}
		request.getSession().setAttribute("isManager", isManager);
		return targetUrl;
	}

} 