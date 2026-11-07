package com.kronos.service;

import com.kronos.persistence.entity.PermissionEntity;
import com.kronos.persistence.entity.RoleEntity;
import com.kronos.persistence.entity.UserEntity;
import com.kronos.persistence.repository.PermissionRepository;
import com.kronos.persistence.repository.RoleRepository;
import com.kronos.persistence.repository.UserRepository;
import com.kronos.service.exception.BadRequestException;
import com.kronos.service.exception.ConflictException;
import com.kronos.service.exception.ResourceNotFoundException;
import com.kronos.web.dto.AdminDtos.RoleView;
import com.kronos.web.dto.AdminDtos.UserView;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private static final String SUPER_ADMIN = "SUPER_ADMIN";

    private final RoleRepository roles;
    private final PermissionRepository permissions;
    private final UserRepository users;

    public AdminService(RoleRepository roles, PermissionRepository permissions, UserRepository users) {
        this.roles = roles;
        this.permissions = permissions;
        this.users = users;
    }

    @Transactional(readOnly = true)
    public List<RoleView> listRoles() {
        return roles.findAll().stream()
                .sorted(Comparator.comparingInt(RoleEntity::getLevel).reversed())
                .map(this::toView)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserView> listUsers() {
        return users.findAll().stream().map(this::toView).toList();
    }

    @Transactional
    public UserView assignRoles(Long userId, Set<String> roleNames, String callerEmail) {
        UserEntity caller = users.findByEmail(callerEmail)
                .orElseThrow(() -> new AccessDeniedException("Caller not found"));
        UserEntity target = users.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (caller.getId().equals(target.getId())) {
            throw new ConflictException("You cannot change your own roles");
        }
        int callerLevel = maxLevel(caller);
        if (maxLevel(target) > callerLevel) {
            throw new AccessDeniedException("Cannot modify a user with a higher role");
        }

        Set<RoleEntity> newRoles = roleNames.stream()
                .map(name -> roles.findByName(name)
                        .orElseThrow(() -> new BadRequestException("Unknown role: " + name)))
                .collect(Collectors.toSet());
        if (newRoles.stream().anyMatch(role -> role.getLevel() > callerLevel)) {
            throw new AccessDeniedException("Cannot assign a role higher than your own");
        }

        target.getRoles().clear();
        target.getRoles().addAll(newRoles);
        return toView(users.save(target));
    }

    @Transactional
    public RoleView setPermissions(String roleName, Set<String> permissionNames) {
        RoleEntity role = roles.findByName(roleName)
                .orElseThrow(() -> new BadRequestException("Unknown role: " + roleName));
        if (SUPER_ADMIN.equals(role.getName())) {
            throw new ConflictException("SUPER_ADMIN permissions are fixed");
        }

        Set<PermissionEntity> newPermissions = permissionNames.stream()
                .map(name -> permissions.findByName(name)
                        .orElseThrow(() -> new BadRequestException("Unknown permission: " + name)))
                .collect(Collectors.toSet());

        role.getPermissions().clear();
        role.getPermissions().addAll(newPermissions);
        return toView(roles.save(role));
    }

    private int maxLevel(UserEntity user) {
        return user.getRoles().stream().mapToInt(RoleEntity::getLevel).max().orElse(0);
    }

    private RoleView toView(RoleEntity role) {
        return new RoleView(role.getName(), role.getLevel(),
                role.getPermissions().stream().map(PermissionEntity::getName).sorted().toList());
    }

    private UserView toView(UserEntity user) {
        return new UserView(user.getId(), user.getEmail(), user.getFullName(), user.isEnabled(),
                user.getRoles().stream().map(RoleEntity::getName).sorted().toList());
    }
}
