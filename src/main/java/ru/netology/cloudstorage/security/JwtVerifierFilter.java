package ru.netology.cloudstorage.security;

import com.google.common.base.Strings;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.netology.cloudstorage.jwt.JwtConfig;
import ru.netology.cloudstorage.jwt.JwtTokenProvider;
import ru.netology.cloudstorage.service.ApplicationUserService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtVerifierFilter extends OncePerRequestFilter {

    private final ApplicationUserService applicationUserService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtConfig jwtConfig;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader(jwtConfig.getAuthorizationHeader());
        String username = null;
        String token = null;

        if (!Strings.isNullOrEmpty(authorizationHeader) && authorizationHeader.startsWith(jwtConfig.getTokenPrefix())) {
            token = authorizationHeader.replace(jwtConfig.getTokenPrefix(), "");
            try {
                username = jwtTokenProvider.getUsernameFromToken(token);
            } catch (IllegalArgumentException e) {
                logger.warn("Unable to get JWT from request");
            } catch (ExpiredJwtException e) {
                logger.warn("JWT has expired");
            }
        } else {
            logger.warn("JWT has incorrect header or does not start with [Bearer ]");
        }

        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = applicationUserService.loadUserByUsername(username);
            if (jwtTokenProvider.validateToken(token, userDetails)) {
                var authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);

//        String token = authorizationHeader.replace(jwtConfig.getTokenPrefix(), "");
//
//        try {
//            Jws<Claims> claimsJws = Jwts.parser()
//                    .setSigningKey(secretKey)
//                    .parseClaimsJws(token);
//
//            Claims body = claimsJws.getBody();
//            String username = body.getSubject();
//            var authorities = (List<Map<String, String>>) body.get("authorities");
//
//            Set<SimpleGrantedAuthority> simpleGrantedAuthorities = authorities.stream()
//                    .map(m -> new SimpleGrantedAuthority(m.get("authority")))
//                    .collect(Collectors.toSet());
//
//            Authentication authentication = new UsernamePasswordAuthenticationToken(
//                    username,
//                    null,
//                    simpleGrantedAuthorities);
//
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        } catch (JwtException e) {
//            logger.warn(String.format("Token %s cannot be trusted", token));
//            throw new IllegalStateException(String.format("Token %s cannot be trusted", token));
//        }
    }
}
