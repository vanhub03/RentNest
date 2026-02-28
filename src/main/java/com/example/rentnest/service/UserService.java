package com.example.rentnest.service;

import com.example.rentnest.model.User;

public interface UserService extends BaseService<User, Long>{
    User findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}
