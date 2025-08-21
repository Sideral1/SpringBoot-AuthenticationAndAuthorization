package com.vyro.vyro_api.AppUsers.DTO;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.vyro.vyro_api.AppUsers.Database.Enums.EnumUserRole;

import lombok.Data;

@Data
public class JwtAuthenticationHolderDTO {

    // ========================= Attributes =========================
    private final String username;
    private final EnumUserRole role;

    // ========================= Methods =========================
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

}
