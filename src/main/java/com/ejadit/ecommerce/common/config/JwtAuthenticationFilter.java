package com.ejadit.ecommerce.common.config;

import com.ejadit.ecommerce.auth.repository.AuthenticationTokenRepository;
import com.ejadit.ecommerce.auth.domain.entity.AuthenticationToken;
import com.ejadit.ecommerce.users.repository.UserRepository;
import com.ejadit.ecommerce.users.entity.UserEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationTokenRepository tokenRepository;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(AuthenticationTokenRepository tokenRepository, 
                                   UserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractTokenFromRequest(request);
            
            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Find token in database
                Optional<AuthenticationToken> authToken = tokenRepository.findByToken(token);
                
                if (authToken.isPresent() && authToken.get().isValid()) {
                    // Get user from database
                    Optional<UserEntity> userOpt = userRepository.findById(authToken.get().getUserId());
                    
                    if (userOpt.isPresent()) {
                        UserEntity user = userOpt.get();
                        
                        // Create authentication object
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                user.getUserName(),
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getUserType().name()))
                            );
                        
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        // Set authentication in security context
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        
                        log.debug("Authenticated user: {}", user.getUserName());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
