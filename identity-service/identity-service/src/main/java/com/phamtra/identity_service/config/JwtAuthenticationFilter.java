package com.phamtra.identity_service.config;

import com.phamtra.identity_service.model.Permission;
import com.phamtra.identity_service.model.Role;
import com.phamtra.identity_service.model.User;
import com.phamtra.identity_service.repository.UserRepository;
import com.phamtra.identity_service.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        
        try {
            String jwt = extractJwtFromRequest(request);
            
            if (StringUtils.hasText(jwt)) {
                // Only verify token structure and expiration
                if (!jwtUtils.isTokenExpired(jwt)) {
                    String email = jwtUtils.extractUsername(jwt);
                    
                    if (email != null) {
                        // Load user with roles and permissions from database on EVERY request
                        // This ensures permission changes take effect immediately
                        Optional<User> userOpt = userRepository.findByEmailWithRolesAndPermissions(email);
                        
                        if (userOpt.isPresent()) {
                            User user = userOpt.get();
                            
                            // Get roles and permissions from database
                            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                            if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                                for (Role role : user.getRoles()) {
                                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getCode()));
                                    
                                    // Add permissions from roles
                                    if (role.getPermissions() != null) {
                                        for (Permission permission : role.getPermissions()) {
                                            authorities.add(new SimpleGrantedAuthority("PERM_" + permission.getCode()));
                                        }
                                    }
                                }
                            }
                            
                            UserDetails userDetails = org.springframework.security.core.userdetails.User
                                    .withUsername(email)
                                    .password(user.getPassword())
                                    .authorities(authorities)
                                    .build();
                            
                            UsernamePasswordAuthenticationToken authentication = 
                                    new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("JWT Filter Error: " + ex.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
