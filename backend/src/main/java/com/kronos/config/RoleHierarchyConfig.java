package com.kronos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

@Configuration
public class RoleHierarchyConfig {

    @Bean
    public static RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy("""
                ROLE_SUPER_ADMIN > ROLE_INSTITUTE_ADMIN
                ROLE_INSTITUTE_ADMIN > ROLE_HOD
                ROLE_HOD > ROLE_FACULTY
                ROLE_HOD > ROLE_STUDENT
                """);
    }
}
