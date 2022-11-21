package br.com.hioktec.portauthserver.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validated
@Component
@ConfigurationProperties("app.auth")
public class AppSecurityProperties {
	
	@NotBlank
	private String jwtBase64Jks;
	
	@NotBlank
  private String jwtJksPassword;
	
	@NotBlank
  private String jwtJksAlias;
	
	@NotBlank
  private String webClientId;
	
	@NotBlank
  private String webClientSecret;
	
	@NotBlank
  private String webRedirectUri;
	
}
