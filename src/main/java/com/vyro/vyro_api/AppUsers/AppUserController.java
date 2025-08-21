package com.vyro.vyro_api.AppUsers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vyro.vyro_api.AppUsers.DTO.AppUserLoginDTO;
import com.vyro.vyro_api.AppUsers.DTO.AppUserRegisterDTO;
import com.vyro.vyro_api.Security.Components.JwtService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AppUserController {

        // ========================= Initialization =========================
        // Instance of the service
        private final AppUserService userService;
        private final JwtService jwtService;
        @Value("${app.cookieName}")
        private String cookieName;

        // ================== Methods ==================

        @PostMapping(path = "/register")
        public ResponseEntity<String> addNewUserEntity(@RequestBody AppUserRegisterDTO data) {
                // If request is invalid
                if (!data.isAllFieldsNotNull()) {
                        return new ResponseEntity<>("Invalid field names", HttpStatus.BAD_REQUEST);
                }

                // Attempting to insert user to DB. If a duplicate exists, DataIntegrityViolationException will be thrown
                try {
                        userService.insertUserEntiy(data);
                        return ResponseEntity.ok("User Registered");
                } catch (DataIntegrityViolationException e) {
                        // Let the global exception handler handle this
                        throw e;
                }
        }

        @PostMapping(path = "/login")
        public ResponseEntity<String> login(@RequestBody AppUserLoginDTO data, HttpServletResponse httpResponse) {

                // Creating response object
                AuthenticationResponse authenticationResponse;

                // Attempting to authenticate user
                try {
                        authenticationResponse = userService.authenticate(data);
                }

                // If user does not exist, return BAD_REQUEST message
                catch (AuthenticationException authenticationException) {
                        return new ResponseEntity<String>("Invalid username or password", HttpStatus.BAD_REQUEST);
                }

                // Setting cokie
                Cookie cookie = new Cookie(cookieName, authenticationResponse.getToken());
                cookie.setHttpOnly(false);
                cookie.setSecure(true);
                cookie.setPath("/");
                cookie.setMaxAge(jwtService.getExpiration() / 1000);

                httpResponse.addCookie(cookie);

                // Else return JWT token
                return new ResponseEntity<String>("Login successful", HttpStatus.OK);
        }

        // ================== Error handler ==================
        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<String> handleUserAlreadyExists(HttpServletRequest req,
                        DataIntegrityViolationException e) {

                // Logging error
                log.error("\033[1;31mUser account already exists: " + e.getMessage() + "\033[0m");

                // Returning error reason
                return new ResponseEntity<>("User account already exists", HttpStatus.CONFLICT);
        }

}
