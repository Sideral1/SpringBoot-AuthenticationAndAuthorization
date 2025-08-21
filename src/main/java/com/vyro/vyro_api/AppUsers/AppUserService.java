package com.vyro.vyro_api.AppUsers;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vyro.vyro_api.AppUsers.DTO.AppUserLoginDTO;
import com.vyro.vyro_api.AppUsers.DTO.AppUserRegisterDTO;
import com.vyro.vyro_api.AppUsers.Database.AppUserEntity;
import com.vyro.vyro_api.AppUsers.Database.AppUserRepository;
import com.vyro.vyro_api.AppUsers.Database.Dependencies.AuthMethodEntity;
import com.vyro.vyro_api.AppUsers.Database.Dependencies.AuthMethodRepository;
import com.vyro.vyro_api.AppUsers.Database.Dependencies.UserRoleEntity;
import com.vyro.vyro_api.AppUsers.Database.Dependencies.UserRoleRepository;
import com.vyro.vyro_api.AppUsers.Database.Enums.EnumAuthMethod;
import com.vyro.vyro_api.AppUsers.Database.Enums.EnumUserRole;
import com.vyro.vyro_api.Security.Components.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppUserService {

        private final AppUserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final ModelMapper modelMapper;
        private final AuthenticationManager authenticationManager;
        private final JwtService jwtService;
        private final UserRoleRepository appUserRoleRepository;
        private final AuthMethodRepository appUserAuthMethodRepository;

        // ================== Methods ==================
        public void insertUserEntiy(AppUserRegisterDTO userRegisterDTO) {
                // Mapping DTO to Entity
                AppUserEntity userEntity = modelMapper.map(userRegisterDTO, AppUserEntity.class);

                // Adding role
                UserRoleEntity roleEntity = appUserRoleRepository.findByRole(EnumUserRole.USER)
                                .orElseThrow(() -> new RuntimeException("Role [USER] not found"));
                userEntity.setRole(roleEntity);

                // Adding authentication method
                AuthMethodEntity authMethodEntity = appUserAuthMethodRepository
                                .findByEnumAuthMethod(EnumAuthMethod.SPRING)
                                .orElseThrow(() -> new RuntimeException("Auth method [SPRING] not found"));
                userEntity.setAppUserAuthMethod(authMethodEntity);

                // Encoding password
                userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));

                // Add user to database
                userRepository.save(userEntity);
        }

        public AuthenticationResponse authenticate(AppUserLoginDTO data) {
                // Authenticate user
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(data.getEmail(), data.getPassword()));

                // Attempting to retrieve user from database, else throw exception
                AppUserEntity user = userRepository.findByEmail(data.getEmail()).orElseThrow(
                                () -> new UsernameNotFoundException("User not found: " + data.getEmail()));

                // Returning JWT token
                return new AuthenticationResponse(jwtService.generateToken(user));
        }
}
