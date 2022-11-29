package br.com.hioktec.portauthserver.domain.model;

import lombok.Getter;

@Getter
public enum ScopeType {
	
	openid ("openid", "ler openid"),
	profile ("profile", "ler perfil"),
	email ("email", "ler e-mail");
	
	private String scope;
	private String description;
	
	private ScopeType(String scope, String description) {
		this.scope = scope;
		this.description = description;
	}
	
}
