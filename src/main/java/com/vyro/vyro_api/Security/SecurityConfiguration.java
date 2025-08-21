package com.vyro.vyro_api.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.vyro.vyro_api.AppUsers.Database.Enums.EnumUserRole;
import com.vyro.vyro_api.Security.Components.CustomAccessDeniedHandler;
import com.vyro.vyro_api.Security.Components.OAuth2SuccessHandler;
import com.vyro.vyro_api.Security.Filters.JwtAuthenticationFilter;
import com.vyro.vyro_api.Security.Filters.PythonServerFilter;
import com.vyro.vyro_api.Security.OAuth.OidcAppUserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

        // ========================= Initialization =========================
        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final PythonServerFilter pythonServerFilter;
        private final AuthenticationProvider authenticationProvider;
        private final OAuth2SuccessHandler oAuth2SuccessHandler;
        private final OidcAppUserService oidcAppUserService;
        private final CustomAccessDeniedHandler customAccessDeniedHandler;

        // Paths without authentication
        private final String[] openEndPoints = {
                        // My endpoints
                        "/auth/**"
        };

        // Paths with ADMIN role
        private final String[] adminEndpoints = {
                        // OpenAPI
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
        };

        // ========================= Configuration =========================
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
                return httpSecurity
                                .cors(cors -> cors.disable())
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(httpRequest -> httpRequest
                                                .requestMatchers(openEndPoints).permitAll()
                                                .requestMatchers(adminEndpoints).hasAuthority(EnumUserRole.ADMIN.name())
                                                .anyRequest().hasAuthority(EnumUserRole.USER.name()))
                                .oauth2Login(oauth2login -> oauth2login
                                                .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                                                .oidcUserService(oidcAppUserService))
                                                .successHandler(oAuth2SuccessHandler))
                                .exceptionHandling(e -> e
                                                .authenticationEntryPoint(
                                                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                                                .accessDeniedHandler(customAccessDeniedHandler))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                                .addFilterBefore(pythonServerFilter, JwtAuthenticationFilter.class)
                                .logout(logout -> logout.disable())
                                .build();
        }

        @Bean
        public WebMvcConfigurer corsConfigurer() {
                return new WebMvcConfigurer() {
                        @Override
                        public void addCorsMappings(@NonNull CorsRegistry registry) {
                                registry.addMapping("/**")
                                                .allowedOrigins("http://localhost:3000")
                                                .allowedMethods("*")
                                                .allowedHeaders("*")
                                                .allowCredentials(true);
                        }
                };
        }
}