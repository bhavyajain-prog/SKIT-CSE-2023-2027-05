package com.kronos.config;

import com.kronos.persistence.entity.PermissionEntity;
import com.kronos.persistence.entity.RoleEntity;
import com.kronos.persistence.repository.PermissionRepository;
import com.kronos.persistence.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

/**
 * Makes sure the standard roles and permissions exist. Safe to run on every start:
 * existing rows are reused, and a role that already has permissions keeps them
 * (an admin may have customised them). Creates no users.
 */
@Component
@Order(1)
public class RbacSeeder implements CommandLineRunner {

    // Never start a permission name with "ROLE_": Spring Security treats that prefix as a role.
    public static final List<String> FACULTY_PERMISSIONS =
            List.of("FACULTY_READ", "SUBJECT_READ", "ROOM_READ", "TIMETABLE_READ");

    public static final List<String> STUDENT_PERMISSIONS =
            List.of("SUBJECT_READ", "ROOM_READ", "TIMETABLE_READ");

    public static final List<String> HOD_PERMISSIONS =
            concat(FACULTY_PERMISSIONS, List.of("TIMETABLE_GENERATE", "TIMETABLE_EXPORT"));

    public static final List<String> INSTITUTE_ADMIN_PERMISSIONS =
            concat(HOD_PERMISSIONS, List.of("FACULTY_WRITE", "SUBJECT_WRITE", "ROOM_WRITE",
                    "TIMETABLE_APPROVE", "USER_MANAGE"));

    public static final List<String> SUPER_ADMIN_PERMISSIONS =
            concat(INSTITUTE_ADMIN_PERMISSIONS, List.of("PERMISSION_MANAGE"));

    private record RoleSpec(String name, int level, List<String> permissions) {
    }

    private static final List<RoleSpec> ROLES = List.of(
            new RoleSpec("FACULTY", 1, FACULTY_PERMISSIONS),
            new RoleSpec("STUDENT", 1, STUDENT_PERMISSIONS),
            new RoleSpec("HOD", 2, HOD_PERMISSIONS),
            new RoleSpec("INSTITUTE_ADMIN", 3, INSTITUTE_ADMIN_PERMISSIONS),
            new RoleSpec("SUPER_ADMIN", 4, SUPER_ADMIN_PERMISSIONS));

    private final PermissionRepository permissions;
    private final RoleRepository roles;

    public RbacSeeder(PermissionRepository permissions, RoleRepository roles) {
        this.permissions = permissions;
        this.roles = roles;
    }

    @Override
    @Transactional
    public void run(String... args) {
        SUPER_ADMIN_PERMISSIONS.forEach(name ->
                permissions.findByName(name).orElseGet(() -> permissions.save(new PermissionEntity(name))));

        for (RoleSpec spec : ROLES) {
            RoleEntity role = roles.findByName(spec.name())
                    .orElseGet(() -> new RoleEntity(spec.name(), spec.level()));
            role.setLevel(spec.level());
            if (role.getPermissions().isEmpty()) {
                spec.permissions().forEach(name ->
                        role.getPermissions().add(permissions.findByName(name).orElseThrow()));
            }
            roles.save(role);
        }
    }

    private static List<String> concat(List<String> first, List<String> second) {
        return Stream.concat(first.stream(), second.stream()).toList();
    }
}
