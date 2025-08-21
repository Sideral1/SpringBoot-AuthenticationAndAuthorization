package com.vyro.vyro_api.AppUsers.DTO;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import com.vyro.vyro_api.AppUsers.Database.AppUserEntity;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
public class AppUserOidcDTO implements OidcUser, UserDetails {

    // ========================= Initialization =========================
    private final AppUserEntity appUserEntity;
    private final OidcUser oidcUser;

    // ========================= Custom methods =========================
    public AppUserEntity getAppUserEntity() {
        return this.appUserEntity;
    }

    //  ========================= Interface methods =========================
    @Override
    public Map<String, Object> getAttributes() {
        return this.oidcUser.getClaims();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.oidcUser.getAuthorities();
    }

    @Override
    public String getName() {
        return this.oidcUser.getName();
    }

    @Override
    public String getPassword() {
        return this.appUserEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return this.appUserEntity.getEmail();
    }

    @Override
    public Map<String, Object> getClaims() {
        return this.oidcUser.getClaims();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return this.oidcUser.getUserInfo();
    }

    @Override
    public OidcIdToken getIdToken() {
        return this.oidcUser.getIdToken();
    }

}
