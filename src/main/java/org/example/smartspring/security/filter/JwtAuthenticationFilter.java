package org.example.smartspring.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.smartspring.security.service.CustomUserDetailsService;
import org.example.smartspring.security.service.JwtService;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No JWT token found in request headers");
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        try {
            final String username = jwtService.extractUsername(jwt);
            log.info("Processing JWT for username: {}", username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtService.validateToken(jwt, userDetails)) {

                    // Extraire les permissions du token
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();

                    Object permissionsObj = jwtService.extractClaim(jwt, claims -> claims.get("permissions"));

                    log.info("Permissions object type: {}", permissionsObj != null ? permissionsObj.getClass().getName() : "null");
                    log.info("Permissions object value: {}", permissionsObj);

                    if (permissionsObj instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<String> permissionsList = (List<String>) permissionsObj;
                        authorities = permissionsList.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());
                        log.info("Extracted {} authorities from List: {}", authorities.size(), authorities);
                    } else if (permissionsObj instanceof String) {
                        String permissionsStr = (String) permissionsObj;
                        if (!permissionsStr.isEmpty()) {
                            String[] permArray = permissionsStr.split(",");
                            for (String perm : permArray) {
                                authorities.add(new SimpleGrantedAuthority(perm.trim()));
                            }
                            log.info("Extracted {} authorities from String: {}", authorities.size(), authorities);
                        }
                    } else {
                        log.warn("Permissions not found or invalid type in token");
                    }

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            authorities  // Utiliser les permissions extraites du JWT
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.info("Authentication successful for user: {} with {} authorities", username, authorities.size());
                }
            }
        } catch (Exception e) {
            log.error("JWT authentication error: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Authentification invalide: " + e.getMessage() + "\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}