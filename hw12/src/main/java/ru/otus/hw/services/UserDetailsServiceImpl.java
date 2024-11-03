package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Role;
import ru.otus.hw.repositories.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ru.otus.hw.models.User user = userRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + "not found"));
        List<SimpleGrantedAuthority> authorities = mapGrantedAuthority(user.getRole());
        return new User(user.getLogin(), user.getPassword(), authorities);
    }

    private List<SimpleGrantedAuthority> mapGrantedAuthority(List<Role> roles) {
        return roles.stream()
                .map(role -> {
                    String roleName = "ROLE_%s".formatted(role.getName());
                    return new SimpleGrantedAuthority(roleName);
                })
                .toList();
    }

}
