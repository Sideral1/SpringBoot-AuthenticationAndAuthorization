package com.vyro.vyro_api.AppUsers.Database.Dependencies;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vyro.vyro_api.AppUsers.Database.Enums.EnumAuthMethod;

public interface AuthMethodRepository extends JpaRepository<AuthMethodEntity, Long> {

    Optional<AuthMethodEntity> findByEnumAuthMethod(EnumAuthMethod name);
}
