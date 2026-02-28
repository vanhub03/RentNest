package com.example.rentnest.repository;

import com.example.rentnest.model.User;

import java.util.Optional;

public interface UserRepository extends BaseRepository<User, Long>{
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Boolean existsByPhoneNumber(String phoneNumber);
}
