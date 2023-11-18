package Group4.Services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import Group4.Repositories.UserRepository;
import Group4.Security.JwtUserDetails;
import Group4.Entities.User;

@Service
public class UserDetailsServiceImplementation implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> user = userRepository.findByMail(username);
		if(user.isEmpty()) {
			return null;
		}
		return JwtUserDetails.newUserDetail(user.get());
	}
	
	public UserDetails loadUserById(Long id) {
		Optional<User> user = userRepository.findById(id);
		if(user.isEmpty()) {
			return null;
		}
		return JwtUserDetails.newUserDetail(user.get()); 
	}
}
