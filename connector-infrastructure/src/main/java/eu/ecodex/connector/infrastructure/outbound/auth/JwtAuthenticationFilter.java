/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.auth;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * A filter that intercepts each HTTP request to perform JWT-based authentication.
 * This filter ensures that any incoming request is checked for a valid JSON Web Token (JWT)
 * in the `Authorization` header. If a valid token is found, the user's authentication details
 * are set into the Spring Security context.
 *
 * <p>The authentication token's validity is verified using {@link JwtService}. The user's
 * details are fetched using {@link UserDetailsService}, and properly authenticated users are
 * granted access to resources based on their authorities.
 *
 * <p>This filter should be executed once per request, extending the
 * {@link OncePerRequestFilter}.</p>
 *
 * <p>Detailed steps executed by this filter:
 * - Extracts the `Authorization` header from the incoming request.
 * - Verifies the format and presence of the Bearer token.
 * - Extracts the username from the token using {@link JwtService}.
 * - Loads user details using {@link UserDetailsService}.
 * - Validates the token for the fetched user.
 * - Sets the authentication details in the Spring Security context if the token is valid.
 * - If any exception occurs during token validation, the security context is cleared,
 * and an error is logged.</p>
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtTokenService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtTokenService,
                                   UserDetailsService userDetailsService) {
        this.jwtTokenService = jwtTokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
        throws ServletException, IOException {

        var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        var bearerPrefix = "Bearer ";

        if (authHeader == null || !authHeader.startsWith(bearerPrefix)) {
            filterChain.doFilter(request, response);
            return;
        }
        var token = authHeader.substring(bearerPrefix.length());

        try {
            var username = jwtTokenService.extractUsername(token);

            if (username != null
                && SecurityContextHolder.getContext().getAuthentication() == null) {

                var userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtTokenService.isValidToken(token, userDetails)) {
                    var authentication = new UsernamePasswordAuthenticationToken(userDetails,
                        null, userDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug(
                        "JWT authentication set: principal={}, authenticated={}, authorities={}",
                        authentication.getName(), authentication.isAuthenticated(),
                        authentication.getAuthorities()
                    );
                }
            }
        } catch (JwtException | IllegalArgumentException ex) {
            SecurityContextHolder.clearContext();
            log.error("Could not authenticate JWT token, {}", ex.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
