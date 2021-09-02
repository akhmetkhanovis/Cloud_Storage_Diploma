package ru.netology.cloudstorage.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.netology.cloudstorage.repository.UserRepository;

@Service
@AllArgsConstructor
public class ApplicationUserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException(String.format("User \"%s\" not found", username)));
    }
}
