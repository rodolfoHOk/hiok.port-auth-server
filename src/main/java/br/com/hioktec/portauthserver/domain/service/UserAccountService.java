package br.com.hioktec.portauthserver.domain.service;

import br.com.hioktec.portauthserver.domain.exception.ResourceNotFoundException;
import br.com.hioktec.portauthserver.domain.model.UserAccount;
import br.com.hioktec.portauthserver.domain.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserAccountService {

  private final UserAccountRepository userAccountRepository;
  private final JwtDecoder jwtDecoder;

    public UserAccount getUserAccountByToken(String bearerToken) {
      String token = bearerToken.substring(7);
      Jwt jwt = jwtDecoder.decode(token);
      String userId = jwt.getSubject();

      return userAccountRepository
        .findByIdAndActive(UUID.fromString(userId), true)
        .orElseThrow(() -> new ResourceNotFoundException("User account not found"));
  }

}
