package com.kronos.web;

import com.kronos.service.AdminService;
import com.kronos.web.dto.AdminDtos.AssignRolesRequest;
import com.kronos.web.dto.AdminDtos.CreateUserRequest;
import com.kronos.web.dto.AdminDtos.RoleView;
import com.kronos.web.dto.AdminDtos.SetPermissionsRequest;
import com.kronos.web.dto.AdminDtos.UserView;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public List<RoleView> roles() {
        return adminService.listRoles();
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public List<UserView> users() {
        return adminService.listUsers();
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public UserView createUser(@Valid @RequestBody CreateUserRequest request, Authentication authentication) {
        return adminService.createUser(request, authentication.getName());
    }

    @PutMapping("/users/{id}/roles")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public UserView assignRoles(@PathVariable Long id, @Valid @RequestBody AssignRolesRequest request,
                                Authentication authentication) {
        return adminService.assignRoles(id, request.roles(), authentication.getName());
    }

    @PutMapping("/roles/{name}/permissions")
    @PreAuthorize("hasAuthority('PERMISSION_MANAGE')")
    public RoleView setPermissions(@PathVariable String name, @Valid @RequestBody SetPermissionsRequest request) {
        return adminService.setPermissions(name, request.permissions());
    }
}
