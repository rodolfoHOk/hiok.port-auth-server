package br.com.hioktec.portauthserver.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.hioktec.portauthserver.domain.model.AccountGroup;

public interface AccountGroupRepository extends JpaRepository<AccountGroup, Long>{

}
