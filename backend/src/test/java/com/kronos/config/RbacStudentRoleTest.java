package com.kronos.config;

import com.kronos.persistence.entity.PermissionEntity;
import com.kronos.persistence.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RbacStudentRoleTest {

    @Autowired
    private RbacSeeder seeder;
    @Autowired
    private RoleRepository roles;

    private Set<String> permissionsOf(String roleName) {
        return roles.findByName(roleName).orElseThrow().getPermissions().stream()
                .map(PermissionEntity::getName).collect(Collectors.toSet());
    }

    @Test
    void studentSitsAtTheSameLevelAsFaculty() {
        seeder.run();
        assertEquals(roles.findByName("FACULTY").orElseThrow().getLevel(),
                roles.findByName("STUDENT").orElseThrow().getLevel());
    }

    @Test
    void studentOnlyGetsReadPermissionsForSubjectsRoomsAndTimetable() {
        seeder.run();
        assertEquals(Set.of("SUBJECT_READ", "ROOM_READ", "TIMETABLE_READ"), permissionsOf("STUDENT"));
    }

    @Test
    void everyRoleAboveStudentKeepsTheStudentPermissions() {
        seeder.run();
        assertTrue(permissionsOf("HOD").containsAll(permissionsOf("STUDENT")));
        assertTrue(permissionsOf("INSTITUTE_ADMIN").containsAll(permissionsOf("STUDENT")));
        assertTrue(permissionsOf("SUPER_ADMIN").containsAll(permissionsOf("STUDENT")));
    }
}
