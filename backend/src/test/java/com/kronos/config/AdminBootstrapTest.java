package com.kronos.config;

import com.kronos.persistence.entity.RoleEntity;
import com.kronos.persistence.entity.UserEntity;
import com.kronos.persistence.repository.RoleRepository;
import com.kronos.persistence.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AdminBootstrapTest {

    private static final String EMAIL = "root@kronos.test";
    private static final String PASSWORD = "a-strong-password";

    @Autowired
    private UserRepository users;
    @Autowired
    private RoleRepository roles;
    @Autowired
    private PasswordEncoder encoder;

    private AdminBootstrap bootstrap(String email, String password) {
        return new AdminBootstrap(users, roles, encoder, email, password, "Root Admin");
    }

    @Test
    void createsSuperAdminFromSettings() {
        bootstrap(EMAIL, PASSWORD).run();

        UserEntity user = users.findByEmail(EMAIL).orElseThrow();
        assertEquals("Root Admin", user.getFullName());
        assertTrue(encoder.matches(PASSWORD, user.getPassword()));
        assertEquals(List.of("SUPER_ADMIN"), user.getRoles().stream().map(RoleEntity::getName).toList());
    }

    @Test
    void secondRunLeavesTheExistingUserUnchanged() {
        bootstrap(EMAIL, PASSWORD).run();
        String hash = users.findByEmail(EMAIL).orElseThrow().getPassword();

        bootstrap(EMAIL, "a-different-password").run();

        assertEquals(1, users.findAll().stream().filter(u -> u.getEmail().equalsIgnoreCase(EMAIL)).count());
        assertEquals(hash, users.findByEmail(EMAIL).orElseThrow().getPassword());
    }

    @Test
    void existingEmailInDifferentCaseIsSkipped() {
        bootstrap(EMAIL, PASSWORD).run();
        bootstrap("ROOT@kronos.test", PASSWORD).run();

        assertFalse(users.existsByEmail("ROOT@kronos.test"));
    }

    @Test
    void unsetOrHalfSetVariablesDoNothing() {
        long before = users.count();
        bootstrap("", "").run();
        bootstrap(EMAIL, "").run();
        bootstrap("", PASSWORD).run();
        assertEquals(before, users.count());
    }

    @Test
    void shortPasswordFailsStartupInsteadOfCreatingAWeakAdmin() {
        assertThrows(IllegalStateException.class, () -> bootstrap(EMAIL, "short").run());
        assertFalse(users.existsByEmail(EMAIL));
    }
}
