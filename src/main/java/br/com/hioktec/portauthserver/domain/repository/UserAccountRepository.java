package br.com.hioktec.portauthserver.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.hioktec.portauthserver.domain.model.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {
		
	Optional<UserAccount> findByEmail(String email);

	Optional<UserAccount> findByIdAndActive(UUID id, boolean active);
	
}
