package com.demo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomAuthorizationRequestResolver.class);
	
	private OAuth2AuthorizationRequestResolver defaultResolver;

	private Environment env;

	private static final String REGISTRATION_ID = "okta";
	private static final String TOKEN_URI = "/v1/token";
	private static final String AUTHORIZATION_URI = "/v1/authorize";

	private static List<String> clients = Arrays.asList(REGISTRATION_ID);

	@Autowired
	HttpSession session;

	public CustomAuthorizationRequestResolver(Environment environment, String requestUri) {
		this.env = environment;
		ClientRegistrationRepository clientRepo = getClientRepo();
		defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(clientRepo, requestUri);
	}

	@Override
	public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
		LOGGER.debug("Current Request: {}", request.getRequestURI());
		OAuth2AuthorizationRequest req = defaultResolver.resolve(request);
		if (req != null) {
			Map<String, Object> extraParams = new HashMap<>();
			extraParams.putAll(req.getAdditionalParameters());
			extraParams.put("login_hint", session.getAttribute("currentUser"));
			return OAuth2AuthorizationRequest.from(req).additionalParameters(extraParams).build();
		}
		return req;
	}

	@Override
	public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
		return defaultResolver.resolve(request, clientRegistrationId);
	}

	public ClientRegistrationRepository getClientRepo() {
		List<ClientRegistration> registrations = clients.stream().map(this::getRegistration)
				.filter(Objects::nonNull).collect(Collectors.toList());
		return new InMemoryClientRegistrationRepository(registrations);
	}

	public ClientRegistration getRegistration(String client) {
		String oktaIssuer = env.getProperty("okta.oauth2.issuer");
		String oktaClientId = env.getProperty("okta.oauth2.client-id");
		String oktaSecretKey = env.getProperty("okta.oauth2.client-secret");

		return CommonOAuth2Provider.OKTA.getBuilder(client).registrationId(REGISTRATION_ID)
				.clientId(oktaClientId).clientSecret(oktaSecretKey)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.authorizationUri(oktaIssuer + AUTHORIZATION_URI).tokenUri(oktaIssuer + TOKEN_URI).build();
	}
}
