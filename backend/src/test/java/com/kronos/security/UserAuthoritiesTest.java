package com.kronos.security;

import com.kronos.persistence.entity.PermissionEntity;
import com.kronos.persistence.entity.RoleEntity;
import com.kronos.persistence.entity.UserEntity;
import com.kronos.persistence.repository.PermissionRepository;
import com.kronos.persistence.repository.RoleRepository;
import com.kronos.persistence.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserAuthoritiesTest {

    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private UserRepository users;
    @Autowired
    private RoleRepository roles;
    @Autowired
    private PermissionRepository permissions;

    @Test
    void roleAndItsPermissionsBecomeAuthorities() {
        PermissionEntity read = permissions.save(new PermissionEntity("TEST_READ"));
        PermissionEntity write = permissions.save(new PermissionEntity("TEST_WRITE"));
        RoleEntity role = new RoleEntity("TEST_ROLE", 2);
        role.getPermissions().add(read);
        role.getPermissions().add(write);
        roles.save(role);

        UserEntity user = new UserEntity();
        user.setEmail("perm@kronos.test");
        user.setPassword("not-used");
        user.getRoles().add(role);
        users.save(user);

        UserDetails details = userDetailsService.loadUserByUsername("perm@kronos.test");
        List<String> names = details.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        assertEquals(3, names.size());
        assertTrue(names.containsAll(List.of("ROLE_TEST_ROLE", "TEST_READ", "TEST_WRITE")));
    }

    @Test
    void roleWithoutPermissionsOnlyGetsTheRoleAuthority() {
        RoleEntity role = roles.save(new RoleEntity("EMPTY_ROLE", 1));
        UserEntity user = new UserEntity();
        user.setEmail("empty@kronos.test");
        user.setPassword("not-used");
        user.getRoles().add(role);
        users.save(user);

        UserDetails details = userDetailsService.loadUserByUsername("empty@kronos.test");

        assertEquals(1, details.getAuthorities().size());
        assertTrue(details.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_EMPTY_ROLE")));
    }
}
