package br.com.hioktec.portauthserver.security.controller;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.hioktec.portauthserver.domain.model.ScopeType;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthorizationConsentController {
	
	private final RegisteredClientRepository registeredClientRepository;
	private final OAuth2AuthorizationConsentService oAuth2AuthorizationConsentService;
	
	@GetMapping("/oauth2/consent")
	private String consent(
			Principal principal,
			Model model,
			@RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
			@RequestParam(OAuth2ParameterNames.SCOPE) String scope,
			@RequestParam(OAuth2ParameterNames.STATE) String state) {
		
		Set<ScopeType> scopesToApprove = new HashSet<>();
		for (String requestScope : StringUtils.delimitedListToStringArray(scope, " ")) {
			scopesToApprove.add(ScopeType.valueOf(requestScope));
		}
				
		RegisteredClient registeredClient = registeredClientRepository.findByClientId(clientId);
		if (registeredClient == null) {
			throw new AccessDeniedException("Client not found with informed client ID");
		}
		OAuth2AuthorizationConsent currentOAuth2AuthorizationConsent = oAuth2AuthorizationConsentService
				.findById(registeredClient.getId(), principal.getName());
		
		Set<ScopeType> previouslyApprovedScopes = new HashSet<>();
		if (currentOAuth2AuthorizationConsent != null) {
			currentOAuth2AuthorizationConsent.getScopes()
				.forEach(scopeApproved -> previouslyApprovedScopes.add(ScopeType.valueOf(scopeApproved)));
			scopesToApprove.removeAll(previouslyApprovedScopes);
		}
		
		model.addAttribute("clientId", clientId);
		model.addAttribute("state", state);
		model.addAttribute("principalName", principal.getName());
		model.addAttribute("scopesToApprove", scopesToApprove);
		model.addAttribute("previouslyApprovedScopes", previouslyApprovedScopes);

		return "pages/consent";
	}
}
