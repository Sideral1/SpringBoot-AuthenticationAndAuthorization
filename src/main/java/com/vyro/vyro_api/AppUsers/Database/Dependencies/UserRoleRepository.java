package com.vyro.vyro_api.AppUsers.Database.Dependencies;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vyro.vyro_api.AppUsers.Database.Enums.EnumUserRole;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long> {

    Optional<UserRoleEntity> findByRole(EnumUserRole name);
}
