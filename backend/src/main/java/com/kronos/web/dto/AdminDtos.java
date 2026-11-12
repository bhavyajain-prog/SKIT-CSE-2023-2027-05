package com.kronos.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Set;

public final class AdminDtos {

    private AdminDtos() {
    }

    public record RoleView(String name, int level, List<String> permissions) {
    }

    public record UserView(Long id, String email, String fullName, boolean enabled, List<String> roles) {
    }

    public record AssignRolesRequest(@NotEmpty Set<String> roles) {
    }

    public record CreateUserRequest(@NotBlank @Email String email,
                                    @NotBlank @Size(min = 12, max = 128) String password,
                                    @Size(max = 255) String fullName,
                                    @NotEmpty Set<String> roles) {
    }

    public record SetPermissionsRequest(@NotEmpty Set<String> permissions) {
    }
}
