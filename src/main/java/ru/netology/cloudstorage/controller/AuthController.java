package ru.netology.cloudstorage.controller;

import lombok.AllArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import ru.netology.cloudstorage.jwt.JwtTokenProvider;
import ru.netology.cloudstorage.model.AuthenticationRequest;
import ru.netology.cloudstorage.model.AuthenticationResponse;
import ru.netology.cloudstorage.model.ErrorResponse;
import ru.netology.cloudstorage.service.ApplicationUserService;

import javax.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin
@AllArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final ApplicationUserService applicationUserService;
    private final JwtTokenProvider tokenProvider;

    protected final Log logger = LogFactory.getLog(this.getClass());

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authRequest) {
        authenticate(authRequest.getUsername(), authRequest.getPassword());
        UserDetails userDetails = applicationUserService.loadUserByUsername(authRequest.getUsername());
        String token = tokenProvider.generateToken(userDetails);
        logger.info(String.format("User \"%s\" logged in", userDetails.getUsername()));
        return ResponseEntity.ok(new AuthenticationResponse(token));
    }

    private void authenticate(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect username or password");
        }
    }

    @GetMapping()
    public String home() {
        return ("<h1>Welcome</h1>");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String admin() {
        return ("<h1>Welcome Admin</h1>");
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public String user() {
        return ("<h1>Welcome User</h1>");
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage(), 400));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage(), 400));
    }
}
