package com.kronos.web.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Set;

public final class AdminDtos {

    private AdminDtos() {
    }

    public record RoleView(String name, int level, List<String> permissions) {
    }

    /** Never contains the password hash. */
    public record UserView(Long id, String email, String fullName, boolean enabled, List<String> roles) {
    }

    public record AssignRolesRequest(@NotEmpty Set<String> roles) {
    }

    public record SetPermissionsRequest(@NotEmpty Set<String> permissions) {
    }
}
