package br.com.hioktec.portauthserver.api.controller;

import br.com.hioktec.portauthserver.api.dto.AuthUserResponse;
import br.com.hioktec.portauthserver.domain.model.UserAccount;
import br.com.hioktec.portauthserver.domain.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthUserController {

  private final UserAccountService userAccountService;

  @GetMapping("/me")
  @PreAuthorize("hasAuthority('USER')")
  public AuthUserResponse getAuthUserInfo(@RequestHeader(name = "Authorization") String bearerToken) {
    UserAccount userAccount = userAccountService.getUserAccountByToken(bearerToken);

    AuthUserResponse userResponse = new AuthUserResponse();
    userResponse.setId(userAccount.getId());
    userResponse.setName(userAccount.getName());
    userResponse.setEmail(userAccount.getEmail());

    return userResponse;
  }
}
