package com.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@SuppressWarnings("deprecation")
@org.springframework.context.annotation.Configuration
public class Configuration extends WebSecurityConfigurerAdapter {

	@Autowired
	public Environment env;
	
	public static String AUTHORIZATION_BASE_URI = "/oauth2/authorization";
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/auth").permitAll().anyRequest().authenticated().and()
		.formLogin()
		.loginPage("/login").permitAll().and()
		.logout().permitAll().and()
		.oauth2Login()
		.loginPage("/login")
		.authorizationEndpoint()
        .authorizationRequestResolver(getCustomAuthResolver()).and()
		.successHandler(successRedirectHandler());
	}

	@Bean
	public AuthenticationSuccessHandler successRedirectHandler() {
		return new CustomAuthenticationSuccessHandler();
	}
	
	@Bean
	public CustomAuthorizationRequestResolver getCustomAuthResolver() {
		return new CustomAuthorizationRequestResolver(env, AUTHORIZATION_BASE_URI);
	}
}
