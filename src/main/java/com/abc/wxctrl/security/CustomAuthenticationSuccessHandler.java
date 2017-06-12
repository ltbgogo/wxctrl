package com.abc.wxctrl.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
    	if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect!");
        } else {
        	redirectStrategy.sendRedirect(request, response, "/wx/home/listAccount");
        }
    }
}



