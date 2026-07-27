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
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
 * Adds JWT authentication to the Spring Security filter chain.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    JwtService jwtTokenService;
    UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
        throws ServletException, IOException {

        var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        var token = authHeader.substring(7);

        try {
            var username = jwtTokenService.extractUsername(token);

            if (username == null) {
                SecurityContextHolder.clearContext();
                log.warn("JWT authentication failed: token does not contain a username");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                return;
            }

            var userDetails = userDetailsService.loadUserByUsername(username);

            if (!jwtTokenService.isValidToken(token, userDetails)) {
                SecurityContextHolder.clearContext();
                log.warn("JWT authentication failed: token is not valid for user {}", username);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid or expired JWT token");
                return;
            }

            var authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource()
                .buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("JWT authentication set: principal={}, authenticated={}, "
                    + "authorities={}",
                authentication.getName(),
                authentication.isAuthenticated(),
                authentication.getAuthorities()
            );

            filterChain.doFilter(request, response);

        } catch (JwtException ex) {
            SecurityContextHolder.clearContext();
            log.error("Could not authenticate JWT token, {}", ex.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                "Invalid or expired JWT token");
        }
    }

}
