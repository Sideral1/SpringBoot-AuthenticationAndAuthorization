package com.vyro.vyro_api.Security.OAuth;

import java.util.Optional;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.vyro.vyro_api.AppUsers.DTO.AppUserOidcDTO;
import com.vyro.vyro_api.AppUsers.Database.AppUserEntity;
import com.vyro.vyro_api.AppUsers.Database.AppUserRepository;
import com.vyro.vyro_api.AppUsers.Database.Dependencies.AuthMethodEntity;
import com.vyro.vyro_api.AppUsers.Database.Dependencies.AuthMethodRepository;
import com.vyro.vyro_api.AppUsers.Database.Dependencies.UserRoleEntity;
import com.vyro.vyro_api.AppUsers.Database.Dependencies.UserRoleRepository;
import com.vyro.vyro_api.AppUsers.Database.Enums.EnumAuthMethod;
import com.vyro.vyro_api.AppUsers.Database.Enums.EnumUserRole;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OidcAppUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

        // ========================= Initialization =========================
        private final OidcUserService oidcUserService;
        private final AppUserRepository userRepository;
        private final UserRoleRepository appUserRoleRepository;
        private final AuthMethodRepository appUserAuthMethodRepository;

        // ================== Methods ==================
        @Override
        public OidcUser loadUser(OidcUserRequest oidcUserRequest) throws OAuth2AuthenticationException {

                // Getting OidcUser
                OidcUser oidcUser = this.oidcUserService.loadUser(oidcUserRequest);

                // Logging OidcUser
                log.debug("\033[1;34mLoading user *{}*\033[0m", oidcUser);

                // Instantiating custom OidcUser implementation to hold AppUserEntity
                AppUserOidcDTO customAuthenticationObject = null;

                // ============ Handling first-time login and updates ============
                Optional<AppUserEntity> userOptional = userRepository.findByEmail(oidcUser.getEmail());

                // If user is not present in database OR his account was not created with GOOGLE oauth
                // Create an account for him
                if (!(userOptional.isPresent())
                                || !(userOptional.get().getAppUserAuthMethod()
                                                .getEnumAuthMethod() == EnumAuthMethod.GOOGLE)) {

                        // Creating new user
                        AppUserEntity newUser = new AppUserEntity();

                        // Adding email
                        newUser.setEmail(oidcUser.getEmail());

                        // Adding first_name
                        newUser.setFirstName(oidcUser.getGivenName());

                        // Add last_name
                        String lastName = oidcUser.getFamilyName();
                        newUser.setLastName(lastName == null ? "NOT INFORMED" : lastName);

                        // Setting auth_method_id to GOOGLE
                        AuthMethodEntity authMethodEntity = appUserAuthMethodRepository
                                        .findByEnumAuthMethod(EnumAuthMethod.GOOGLE)
                                        .orElseThrow(() -> new RuntimeException("Auth method [GOOGLE] not found"));
                        newUser.setAppUserAuthMethod(authMethodEntity);

                        // Setting role_id to USER
                        UserRoleEntity roleEntity = appUserRoleRepository.findByRole(EnumUserRole.USER)
                                        .orElseThrow(() -> new RuntimeException("Role [USER] not found"));
                        newUser.setRole(roleEntity);

                        // Setting password to "NO PASSWORD"
                        newUser.setPassword("NO PASSWORD");

                        // Adding newUser to database
                        userRepository.save(newUser);

                        // Adding newUser to AuthenticationObject
                        customAuthenticationObject = new AppUserOidcDTO(newUser, oidcUser);
                } else {
                        // Adding user found in DB to AuthenticationObject
                        customAuthenticationObject = new AppUserOidcDTO(userOptional.get(), oidcUser);
                }

                // Adding AppUserEntity to SecurityContextHolder through custom implementation of OidcUser
                return customAuthenticationObject;
        }
}
