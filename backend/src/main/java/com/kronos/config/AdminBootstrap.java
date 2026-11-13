package com.kronos.config;

import com.kronos.persistence.entity.RoleEntity;
import com.kronos.persistence.entity.UserEntity;
import com.kronos.persistence.repository.RoleRepository;
import com.kronos.persistence.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creates the first SUPER_ADMIN from BOOTSTRAP_ADMIN_EMAIL / BOOTSTRAP_ADMIN_PASSWORD
 * at startup, in every profile. Runs on each restart but only acts once: when the
 * variables are unset, or a user with that email already exists, it changes nothing.
 * Further admins are created or promoted by existing admins through /api/admin.
 */
@Component
@Order(3)
public class AdminBootstrap implements CommandLineRunner {

    public static final int MIN_PASSWORD_LENGTH = 12;

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);
    private static final String SUPER_ADMIN = "SUPER_ADMIN";

    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;
    private final String email;
    private final String password;
    private final String fullName;

    public AdminBootstrap(UserRepository users, RoleRepository roles, PasswordEncoder encoder,
                          @Value("${app.bootstrap-admin.email:}") String email,
                          @Value("${app.bootstrap-admin.password:}") String password,
                          @Value("${app.bootstrap-admin.full-name:Administrator}") String fullName) {
        this.users = users;
        this.roles = roles;
        this.encoder = encoder;
        this.email = email.trim();
        this.password = password;
        this.fullName = fullName;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (email.isEmpty() && password.isEmpty()) {
            log.info("Admin bootstrap skipped: BOOTSTRAP_ADMIN_EMAIL is not set");
            return;
        }
        if (email.isEmpty() || password.isEmpty()) {
            log.warn("Admin bootstrap skipped: set both BOOTSTRAP_ADMIN_EMAIL and BOOTSTRAP_ADMIN_PASSWORD");
            return;
        }
        if (users.existsByEmailIgnoreCase(email)) {
            log.info("Admin bootstrap skipped: user {} already exists", email);
            return;
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalStateException(
                    "BOOTSTRAP_ADMIN_PASSWORD must be at least " + MIN_PASSWORD_LENGTH + " characters");
        }

        RoleEntity superAdmin = roles.findByName(SUPER_ADMIN)
                .orElseThrow(() -> new IllegalStateException("SUPER_ADMIN role missing, RbacSeeder did not run"));
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPassword(encoder.encode(password));
        user.getRoles().add(superAdmin);
        users.save(user);
        log.info("Admin bootstrap created SUPER_ADMIN {}", email);
    }
}
