package com.demo;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class WelcomeController {

//	@Autowired
//	RedirectStrategy redirect;
	
	@Autowired
	HttpSession session;
	
	@GetMapping("/login")
	public String main(User user, Model model) { // Main login view
		model.addAttribute("message", "Welcome to thymleaf");
		return "login"; // view
	}
	
	@PostMapping("/auth")
	public ModelAndView authenticate(User user, ModelMap map, HttpServletRequest request, HttpServletResponse response) throws IOException {
		System.out.println("------------");
		System.out.println(user.getEmailAddress());
//		request.setAttribute("currentUser", user.getEmailAddress());
//		redirect.sendRedirect(request, response, "/oauth2/authorization/okta");
//		map.addAttribute("currentUser", user.getEmailAddress());
		session.setAttribute("currentUser", user.getEmailAddress());
		return new ModelAndView("redirect:" + "/oauth2/authorization/okta"); // view
	}
	
	@GetMapping("/homePage")
	public String main(@AuthenticationPrincipal OidcUser principal) {
		System.out.println("Logged user !!!" + principal.getEmail());
		return "homePage"; // view
	}
	
}
