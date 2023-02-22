package br.com.hioktec.portauthserver.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AuthUserResponse {

  private UUID id;
  private String name;
  private String email;

}
