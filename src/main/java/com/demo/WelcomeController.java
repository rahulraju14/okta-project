package com.demo;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class WelcomeController {

	private static final Logger LOGGER = LoggerFactory.getLogger(WelcomeController.class);
	
	@Autowired
	HttpSession session;
	
	@GetMapping("/login")
	public String main(User user, Model model) { // Main login view
		model.addAttribute("message", "Welcome to thymleaf");
		return "login"; // view
	}
	
	@PostMapping("/auth")
	public ModelAndView authenticate(User user, ModelMap map, HttpServletRequest request, HttpServletResponse response) throws IOException {
		LOGGER.debug("------------");
		LOGGER.debug("Email Address: {}", user.getEmailAddress());
		session.setAttribute("currentUser", user.getEmailAddress());
		return new ModelAndView("redirect:" + "/oauth2/authorization/okta"); // view
	}
	
	@GetMapping("/homePage")
	public String main(@AuthenticationPrincipal OidcUser principal, Model model) {
		LOGGER.debug("User Logged In: {} ", principal.getEmail());
		System.out.println("User Logged In : "+ principal.getEmail());
		model.addAttribute("currentUser", principal.getEmail());
		return "homePage"; // view
	}
	
}
