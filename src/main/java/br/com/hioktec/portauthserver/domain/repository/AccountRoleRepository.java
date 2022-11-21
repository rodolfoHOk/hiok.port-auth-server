package br.com.hioktec.portauthserver.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.hioktec.portauthserver.domain.model.AccountRole;

public interface AccountRoleRepository extends JpaRepository<AccountRole, Long> {

}
