package com.vyro.vyro_api.Security.Filters;

import java.io.IOException;
import java.util.List;

import org.springframework.web.filter.OncePerRequestFilter;

import com.vyro.vyro_api.AppUsers.Database.Enums.EnumUserRole;

import jakarta.servlet.FilterChain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class PythonServerFilter extends OncePerRequestFilter {

        @Value("${app.pythonServerSecretKey}")
        String pythonServerSecretKey;

        @Override
        protected void doFilterInternal(
                        @NonNull HttpServletRequest request,
                        @NonNull HttpServletResponse response,
                        @NonNull FilterChain filterChain) throws ServletException, IOException {

                final String authenticationHeader = request.getHeader("Authorization");
                final String key;

                // If no authenticationHeader, proceed with the filter chain (EARLY RETURN)
                if (authenticationHeader == null || !authenticationHeader.startsWith("Bearer ")) {
                        filterChain.doFilter(request, response);
                        return;
                }

                // Getting key
                key = authenticationHeader.substring(7);

                // If key matches, authenticate request
                if (key.equals(pythonServerSecretKey)) {

                        Authentication authentication = new UsernamePasswordAuthenticationToken(
                                        "python-server",
                                        null,
                                        List.of(new SimpleGrantedAuthority(EnumUserRole.ADMIN.name())));

                        // Assigning authentication object to current security context
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                }

                // Proceed with remaining filters
                filterChain.doFilter(request, response);
        }

}
