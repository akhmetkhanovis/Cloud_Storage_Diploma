package ru.netology.cloudstorage.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.netology.cloudstorage.entity.UserEntity;
import ru.netology.cloudstorage.jwt.JwtTokenProvider;
import ru.netology.cloudstorage.model.AuthenticationRequest;
import ru.netology.cloudstorage.model.AuthenticationResponse;
import ru.netology.cloudstorage.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    public AuthenticationResponse createToken(AuthenticationRequest authenticationRequest) {
        authenticate(authenticationRequest.getLogin(), authenticationRequest.getPassword());
        final UserDetails userDetails = getUser(authenticationRequest.getLogin());
        final String token = tokenProvider.generateToken(userDetails);
        log.info(String.format("User %s logged in", userDetails.getUsername()));

        return new AuthenticationResponse(token);
    }

    public void logout(String token) {
        String username = tokenProvider.getUsernameFromToken(token);
        try {
            getUser(username);
        } catch (UsernameNotFoundException e) {
            log.info(String.format("User %s is not authenticated", username));
            throw new UsernameNotFoundException(String.format("User %s is not authenticated", username));
        }
        log.info(String.format("User %s logged out", username));
    }

    private void authenticate(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect login or password");
        }
    }

    public UserDetails getUser(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User \"%s\" not found", username)));
        return new User(user.getUsername(), user.getPassword(), user.getGrantedAuthorities());
    }
}
