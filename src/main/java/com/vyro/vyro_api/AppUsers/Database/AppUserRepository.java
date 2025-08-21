package com.vyro.vyro_api.AppUsers.Database;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUserEntity, Long>
{
    Optional<AppUserEntity> findByEmail(String email);
}
