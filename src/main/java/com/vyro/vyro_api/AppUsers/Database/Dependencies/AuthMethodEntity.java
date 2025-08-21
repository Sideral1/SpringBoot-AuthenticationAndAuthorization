package com.vyro.vyro_api.AppUsers.Database.Dependencies;

import com.vyro.vyro_api.AppUsers.Database.Enums.EnumAuthMethod;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "app_user_auth_method")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthMethodEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private EnumAuthMethod enumAuthMethod;
}
