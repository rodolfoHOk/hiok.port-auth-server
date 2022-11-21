package br.com.hioktec.portauthserver.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.hioktec.portauthserver.domain.model.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, String> {
		
	Optional<UserAccount> findByEmail(String email);
	
}
