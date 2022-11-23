package br.com.hioktec.portauthserver.security.config;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.time.Duration;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import br.com.hioktec.portauthserver.domain.model.UserAccount;
import br.com.hioktec.portauthserver.domain.repository.UserAccountRepository;

@Configuration
public class AuthorizationServerConfig {

	@Bean
	@Order(1)
	public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
		OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
		http.getConfigurer(OAuth2AuthorizationServerConfigurer.class).oidc(Customizer.withDefaults());
		http
			.exceptionHandling(exceptions ->
				exceptions.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")))
			.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
		return http.build();
	}
	
	@Bean
	@Order(2)
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(authorize ->
					authorize
						.requestMatchers("/login", "/css/**").permitAll()
						.anyRequest().authenticated())
			.formLogin(customizer -> customizer.loginPage("/login"));
		return http.build();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public RegisteredClientRepository registeredClientRepository(PasswordEncoder passwordEncoder,
			AppSecurityProperties properties, JdbcTemplate jdbcTemplate) {
		
		RegisteredClient portfolioWeb = RegisteredClient
				.withId(UUID.randomUUID().toString())
				.clientId(properties.getWebClientId())
				.clientSecret(passwordEncoder.encode(properties.getWebClientSecret()))
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
				.redirectUri("http://127.0.0.1:8080/login/oauth2/code/messaging-client-oidc")
				.redirectUri(properties.getWebRedirectUri())
				.scope(OidcScopes.OPENID)
				.scope(OidcScopes.PROFILE)
				.scope("READ")
				.scope("WRITE")
				.tokenSettings(TokenSettings
						.builder()
						.accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
						.accessTokenTimeToLive(Duration.ofMinutes(15))
						.reuseRefreshTokens(false)
						.refreshTokenTimeToLive(Duration.ofDays(1))
						.build())
				.clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
				.build();
		
		JdbcRegisteredClientRepository jdbcRegisteredClientRepository = new JdbcRegisteredClientRepository(jdbcTemplate);
		
		RegisteredClient existRegisteredClient = jdbcRegisteredClientRepository.findByClientId(properties.getWebClientId());
		if (existRegisteredClient == null) {
			jdbcRegisteredClientRepository.save(portfolioWeb);
		}
		
		return jdbcRegisteredClientRepository;
	}
	
	@Bean
	public OAuth2AuthorizationService oAuth2AuthorizationService(JdbcTemplate jdbcTemplate,
			RegisteredClientRepository registeredClientRepository) {
		return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
	}
	
	@Bean
	public OAuth2AuthorizationConsentService oAuth2AuthorizationConsentService(JdbcTemplate jdbcTemplate,
			RegisteredClientRepository registeredClientRepository) {
		return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
	}
	
	@Bean
	public JWKSource<SecurityContext> jwkSource(AppSecurityProperties properties) throws Exception {
		byte[] jks = Base64.getDecoder().decode(properties.getJwtBase64Jks());
		char[] jksPassword = properties.getJwtJksPassword().toCharArray();
		String jksAlias = properties.getJwtJksAlias();
		
		KeyStore keyStore = KeyStore.getInstance("JKS");
		keyStore.load(new ByteArrayInputStream(jks), jksPassword);
		
		RSAKey rsaKey = RSAKey.load(keyStore, jksAlias, jksPassword);
		JWKSet jwkSet = new JWKSet(rsaKey);
		
		return new ImmutableJWKSet<>(jwkSet);		
	}
	
	@Bean
	public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
		return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
	}
	
	@Bean 
	public AuthorizationServerSettings authorizationServerSettings() {
		return AuthorizationServerSettings.builder().build();
	}
	
	@Bean
	public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer(UserAccountRepository userAccountRepository) {
		return context -> {
			Authentication authentication = context.getPrincipal();
			
			if (authentication.getPrincipal() instanceof User) {
				User user = (User) authentication.getPrincipal();
				
				UserAccount userAccount = userAccountRepository.findByEmail(user.getUsername())
						.orElseThrow(() -> new UsernameNotFoundException("User not found with informed email"));
				
				Set<String> authorities = new HashSet<>();
				user.getAuthorities().forEach(authority -> authorities.add(authority.getAuthority()));
				
				context.getClaims().claim("user_id", userAccount.getId().toString());
				context.getClaims().claim("name", userAccount.getName());
				context.getClaims().claim("email", userAccount.getEmail());
				context.getClaims().claim("authorities", authorities);
			}	
		};
	}
	
}
