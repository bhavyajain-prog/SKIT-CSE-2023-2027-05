package com.kronos.config;

import com.kronos.persistence.entity.RoleEntity;
import com.kronos.persistence.entity.UserEntity;
import com.kronos.persistence.repository.RoleRepository;
import com.kronos.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creates one admin account so login can be tried locally. Only runs with the "dev" profile.
 * The password comes from the DEV_ADMIN_PASSWORD environment variable (.env), never from code.
 */
@Component
@Profile("dev")
public class DevUserSeeder implements CommandLineRunner {

    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;
    private final String email;
    private final String password;

    public DevUserSeeder(UserRepository users, RoleRepository roles, PasswordEncoder encoder,
                         @Value("${DEV_ADMIN_EMAIL:admin@kronos.local}") String email,
                         @Value("${DEV_ADMIN_PASSWORD}") String password) {
        this.users = users;
        this.roles = roles;
        this.encoder = encoder;
        this.email = email;
        this.password = password;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (users.existsByEmail(email)) {
            return;
        }
        RoleEntity admin = roles.findByName("SUPER_ADMIN")
                .orElseGet(() -> roles.save(new RoleEntity("SUPER_ADMIN")));
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setFullName("Dev Admin");
        user.setPassword(encoder.encode(password));
        user.getRoles().add(admin);
        users.save(user);
    }
}
