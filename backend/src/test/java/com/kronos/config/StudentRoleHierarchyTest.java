package com.kronos.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StudentRoleHierarchyTest {

    private final RoleHierarchy hierarchy = RoleHierarchyConfig.roleHierarchy();

    private Set<String> reachableFrom(String role) {
        return hierarchy.getReachableGrantedAuthorities(List.of(new SimpleGrantedAuthority(role)))
                .stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }

    @Test
    void hodReachesStudent() {
        assertTrue(reachableFrom("ROLE_HOD").contains("ROLE_STUDENT"));
    }

    @Test
    void superAdminReachesStudent() {
        assertTrue(reachableFrom("ROLE_SUPER_ADMIN").contains("ROLE_STUDENT"));
    }

    @Test
    void studentAndFacultyDoNotReachEachOther() {
        assertFalse(reachableFrom("ROLE_STUDENT").contains("ROLE_FACULTY"));
        assertFalse(reachableFrom("ROLE_FACULTY").contains("ROLE_STUDENT"));
    }
}
