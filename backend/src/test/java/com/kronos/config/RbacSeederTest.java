package com.kronos.config;

import com.kronos.persistence.entity.PermissionEntity;
import com.kronos.persistence.entity.RoleEntity;
import com.kronos.persistence.repository.PermissionRepository;
import com.kronos.persistence.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RbacSeederTest {

    @Autowired
    private RbacSeeder seeder;
    @Autowired
    private RoleRepository roles;
    @Autowired
    private PermissionRepository permissions;

    private Set<String> permissionsOf(String roleName) {
        return roles.findByName(roleName).orElseThrow().getPermissions().stream()
                .map(PermissionEntity::getName).collect(Collectors.toSet());
    }

    @Test
    void createsTheFourRolesWithTheirLevels() {
        seeder.run();
        assertEquals(4, roles.findByName("SUPER_ADMIN").orElseThrow().getLevel());
        assertEquals(3, roles.findByName("INSTITUTE_ADMIN").orElseThrow().getLevel());
        assertEquals(2, roles.findByName("HOD").orElseThrow().getLevel());
        assertEquals(1, roles.findByName("FACULTY").orElseThrow().getLevel());
    }

    @Test
    void superAdminHasEveryPermissionAndNoneStartWithRole() {
        seeder.run();
        assertEquals(Set.copyOf(RbacSeeder.SUPER_ADMIN_PERMISSIONS), permissionsOf("SUPER_ADMIN"));
        assertFalse(permissions.findAll().stream().anyMatch(p -> p.getName().startsWith("ROLE_")));
    }

    @Test
    void higherRolesKeepEveryPermissionOfTheRolesBelow() {
        seeder.run();
        assertTrue(permissionsOf("HOD").containsAll(permissionsOf("FACULTY")));
        assertTrue(permissionsOf("INSTITUTE_ADMIN").containsAll(permissionsOf("HOD")));
        assertTrue(permissionsOf("SUPER_ADMIN").containsAll(permissionsOf("INSTITUTE_ADMIN")));
    }

    @Test
    void runningAgainDoesNotOverwriteCustomisedPermissions() {
        seeder.run();
        RoleEntity hod = roles.findByName("HOD").orElseThrow();
        hod.getPermissions().removeIf(p -> p.getName().equals("TIMETABLE_EXPORT"));
        roles.saveAndFlush(hod);

        seeder.run();

        assertFalse(permissionsOf("HOD").contains("TIMETABLE_EXPORT"));
    }
}
