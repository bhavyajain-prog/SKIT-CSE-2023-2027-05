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

class RoleHierarchyConfigTest {

    private final RoleHierarchy hierarchy = RoleHierarchyConfig.roleHierarchy();

    private Set<String> reachableFrom(String role) {
        return hierarchy.getReachableGrantedAuthorities(List.of(new SimpleGrantedAuthority(role)))
                .stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }

    @Test
    void hodReachesFacultyButNotAdminRoles() {
        Set<String> reachable = reachableFrom("ROLE_HOD");
        assertTrue(reachable.containsAll(Set.of("ROLE_HOD", "ROLE_FACULTY")));
        assertFalse(reachable.contains("ROLE_INSTITUTE_ADMIN"));
        assertFalse(reachable.contains("ROLE_SUPER_ADMIN"));
    }

    @Test
    void superAdminReachesEveryRole() {
        assertTrue(reachableFrom("ROLE_SUPER_ADMIN").containsAll(
                Set.of("ROLE_SUPER_ADMIN", "ROLE_INSTITUTE_ADMIN", "ROLE_HOD", "ROLE_FACULTY")));
    }
}
