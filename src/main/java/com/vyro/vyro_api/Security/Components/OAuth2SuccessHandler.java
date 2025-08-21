package com.vyro.vyro_api.Security.Components;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.vyro.vyro_api.AppUsers.DTO.AppUserOidcDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

        // ========================= Initialization =========================
        private final JwtService jwtService;

        @Value("${app.cookieName}")
        private String cookieName;

        @Value("${app.oauthRedirectUrl}")
        private String redirectUrl;

        // ========================= Logic =========================
        @Override
        public void onAuthenticationSuccess(
                        HttpServletRequest request,
                        HttpServletResponse response,
                        Authentication authentication) throws IOException, ServletException {

                // ========================= Returning JWT =========================
                // Getting AppUserEntity
                if (authentication.getPrincipal() instanceof AppUserOidcDTO appUserOidcDTO) {

                        // Creating JWT Token
                        String jwtToken = jwtService.generateToken(appUserOidcDTO.getAppUserEntity());

                        // Setting cokie
                        Cookie cookie = new Cookie(cookieName, jwtToken);
                        cookie.setHttpOnly(false);
                        cookie.setSecure(true);
                        cookie.setPath("/");
                        cookie.setMaxAge(jwtService.getExpiration() / 1000);

                        response.addCookie(cookie);
                }

                // ========================= Redirecting after login =========================
                response.sendRedirect(redirectUrl);
        }

}
