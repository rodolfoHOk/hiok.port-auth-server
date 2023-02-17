package br.com.hioktec.portauthserver.security.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Validated
@Component
@ConfigurationProperties(prefix = "app.cors")
public class AppCorsProperties {
  private List<String> allowedOrigins = new ArrayList<>();
}
