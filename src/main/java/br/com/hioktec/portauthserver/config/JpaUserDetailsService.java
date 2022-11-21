package br.com.hioktec.portauthserver.config;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.hioktec.portauthserver.domain.model.UserAccount;
import br.com.hioktec.portauthserver.domain.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JpaUserDetailsService implements UserDetailsService {
	
	private final UserAccountRepository userAccountRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserAccount userAccount = userAccountRepository.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with informed email"));
		
		if (userAccount.getActive() == false) {
			throw new UsernameNotFoundException("User not active, please confirm or email address");
		}
		
		return new User(userAccount.getEmail(), userAccount.getPassword(), getAuthorities(userAccount));
	}

	private Collection<? extends GrantedAuthority> getAuthorities(UserAccount userAccount) {
		return userAccount.getGroups().stream()
				.flatMap(group -> group.getRoles().stream()
						.map(role -> new SimpleGrantedAuthority(role.getName().toUpperCase())))
				.collect(Collectors.toList());
	}

}
