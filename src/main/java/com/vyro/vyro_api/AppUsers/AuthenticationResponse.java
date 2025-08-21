package com.vyro.vyro_api.AppUsers;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationResponse {
        private final String token;
}