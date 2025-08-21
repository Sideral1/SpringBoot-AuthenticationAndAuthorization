package com.vyro.vyro_api.AppUsers.Database;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.vyro.vyro_api.AppUsers.Database.Dependencies.AuthMethodEntity;
import com.vyro.vyro_api.AppUsers.Database.Dependencies.UserRoleEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUserEntity implements UserDetails {

        // ========================= Database configs =========================
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        @Column(name = "first_name", nullable = false)
        private String firstName;

        @Column(name = "last_name", nullable = false)
        private String lastName;

        @Column(unique = true, nullable = false)
        private String email;

        @Column(nullable = false)
        private String password;

        @ManyToOne
        @JoinColumn(name = "auth_method_id", nullable = false)
        private AuthMethodEntity appUserAuthMethod;

        @ManyToOne
        @JoinColumn(name = "role_id", nullable = false)
        private UserRoleEntity role;

        @Column(name = "creation_timestamp", updatable = false, nullable = false)
        private LocalDateTime creationTimestamp;

        // ========================= Database business loig =========================
        @PrePersist
        private void onCreate() {
                // Getting time stamp
                this.creationTimestamp = LocalDateTime.now();

                // Verifying if fields are filled
                if (!isAllFieldsNotNull()) {
                        // Prevent from adding to DB
                        throw new IllegalStateException(
                                        "Cannot persist AppUserEntity: some required fields are null. " +
                                                        "Fields: firstName=" + firstName +
                                                        ", lastName=" + lastName +
                                                        ", email=" + email +
                                                        ", password=" + (password != null ? "***" : null) +
                                                        ", authMethod=" + appUserAuthMethod +
                                                        ", role=" + role);
                }

                // Lowercasing email
                if (this.email != null) {
                        this.email = this.email.toLowerCase();
                }
        }

        // ========================= UserDetails Implementation =========================
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of(new SimpleGrantedAuthority(role.getRole().name()));
        }

        @Override
        public String getUsername() {
                return this.email;
        }

        @Override
        public String getPassword() {
                return this.password;
        }

        @Override
        public boolean isAccountNonExpired() {
                return true;
        }

        @Override
        public boolean isAccountNonLocked() {
                return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
                return true;
        }

        @Override
        public boolean isEnabled() {
                return true;
        }

        // ========================= Utilitites =========================
        private boolean isAllFieldsNotNull() {
                return (this.email != null) && (this.password != null) && (this.firstName != null)
                                && (this.lastName != null)
                                && (this.appUserAuthMethod != null) && (this.role != null)
                                && (this.creationTimestamp != null);
        }

        public void setEmail(String email) {
                this.email = email.toLowerCase();
        }
}
