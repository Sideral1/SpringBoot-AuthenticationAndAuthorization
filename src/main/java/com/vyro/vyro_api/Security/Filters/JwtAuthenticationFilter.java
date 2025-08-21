package com.vyro.vyro_api.Security.Filters;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.vyro.vyro_api.AppUsers.DTO.JwtAuthenticationHolderDTO;
import com.vyro.vyro_api.AppUsers.Database.Enums.EnumUserRole;
import com.vyro.vyro_api.Security.Components.JwtService;

import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // ========================= Initialization =========================
    private final JwtService jwtService;
    @Value("${app.cookieName}")
    private String cookieName;

    // ========================= Logic =========================
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // If user is already authenticated, early return
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Creating constants
        final String userEmail;
        final EnumUserRole enumUserRole;

        // Cookie to logic
        final Cookie[] cookies = request.getCookies() != null ? request.getCookies() : new Cookie[0];

        // Getting JWT token
        String jwt = null;
        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                jwt = cookie.getValue();
                break;
            }
        }

        // If JWT token not in request, proceed with the filter chain (EARLY RETURN)
        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Logging
        log.debug("\033[1;34mExtracted JWT: {}\033[0m", jwt);

        // Retrieving claims
        try {
            userEmail = jwtService.extractUsername(jwt);
            enumUserRole = jwtService.extractRoles(jwt);
        }
        // If claims were not successfull found or token was altered, do not
        // authenticate user
        catch (NotFoundException | SignatureException | MalformedJwtException exception) {

            // Logging
            if (exception instanceof NotFoundException)
                log.error("\033[1;31mUsername or Role not found in JWT token\033[0m");
            log.error("\033[1;31mToken was altered\033[0m");

            // Stop filter chain and return response
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid JWT signature\"}");
            return;
        }

        // If token is missing data, early return
        if ((userEmail == null) || (enumUserRole == null)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Otherwise, proceed to atuhenticate user

        // If token is valid
        if (jwtService.isTokenValid(jwt)) {
            // Creating UserDetails Object
            JwtAuthenticationHolderDTO jwtAuthenticationHolderDTO = new JwtAuthenticationHolderDTO(userEmail,
                    enumUserRole);

            // Creating authentication object
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    jwtAuthenticationHolderDTO,
                    null,
                    jwtAuthenticationHolderDTO.getAuthorities());

            // Setting authentication details
            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));

            // Assigning authentication object to current security context == "telling
            // application this request is authenticated"
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // Proceed with remaining filters
        filterChain.doFilter(request, response);
    }

}
