package com.kronos.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kronos.persistence.entity.UserEntity;
import com.kronos.persistence.repository.RoleRepository;
import com.kronos.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AdminControllerTest {

    private static final String PASSWORD = "S3cret-pass!";

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private FilterChainProxy springSecurityFilterChain;
    @Autowired
    private UserRepository users;
    @Autowired
    private RoleRepository roles;
    @Autowired
    private PasswordEncoder encoder;

    private final ObjectMapper json = new ObjectMapper();
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(springSecurityFilterChain)
                .build();
    }

    @Test
    void noTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/admin/roles")).andExpect(status().isUnauthorized());
    }

    @Test
    void superAdminCanListRoles() throws Exception {
        createUser("sa@kronos.test", "SUPER_ADMIN");
        mockMvc.perform(get("/api/admin/roles").header("Authorization", bearer("sa@kronos.test")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name=='SUPER_ADMIN')]").exists());
    }

    @Test
    void facultyCannotListRoles() throws Exception {
        createUser("f@kronos.test", "FACULTY");
        mockMvc.perform(get("/api/admin/roles").header("Authorization", bearer("f@kronos.test")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void instituteAdminCanPromoteFacultyToHod() throws Exception {
        UserEntity faculty = createUser("f@kronos.test", "FACULTY");
        createUser("ia@kronos.test", "INSTITUTE_ADMIN");
        putJson("/api/admin/users/" + faculty.getId() + "/roles", "ia@kronos.test", "{\"roles\":[\"HOD\"]}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles[0]").value("HOD"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void instituteAdminCannotGiveSuperAdminRole() throws Exception {
        UserEntity faculty = createUser("f@kronos.test", "FACULTY");
        createUser("ia@kronos.test", "INSTITUTE_ADMIN");
        putJson("/api/admin/users/" + faculty.getId() + "/roles", "ia@kronos.test", "{\"roles\":[\"SUPER_ADMIN\"]}")
                .andExpect(status().isForbidden());
    }

    @Test
    void hodCannotAssignRoles() throws Exception {
        UserEntity faculty = createUser("f@kronos.test", "FACULTY");
        createUser("hod@kronos.test", "HOD");
        putJson("/api/admin/users/" + faculty.getId() + "/roles", "hod@kronos.test", "{\"roles\":[\"FACULTY\"]}")
                .andExpect(status().isForbidden());
    }

    @Test
    void instituteAdminCannotChangeASuperAdmin() throws Exception {
        UserEntity superAdmin = createUser("sa@kronos.test", "SUPER_ADMIN");
        createUser("ia@kronos.test", "INSTITUTE_ADMIN");
        putJson("/api/admin/users/" + superAdmin.getId() + "/roles", "ia@kronos.test", "{\"roles\":[\"FACULTY\"]}")
                .andExpect(status().isForbidden());
    }

    @Test
    void nobodyCanChangeTheirOwnRoles() throws Exception {
        UserEntity admin = createUser("ia@kronos.test", "INSTITUTE_ADMIN");
        putJson("/api/admin/users/" + admin.getId() + "/roles", "ia@kronos.test", "{\"roles\":[\"HOD\"]}")
                .andExpect(status().isConflict());
    }

    @Test
    void unknownRoleNameReturns400() throws Exception {
        UserEntity faculty = createUser("f@kronos.test", "FACULTY");
        createUser("ia@kronos.test", "INSTITUTE_ADMIN");
        putJson("/api/admin/users/" + faculty.getId() + "/roles", "ia@kronos.test", "{\"roles\":[\"NOPE\"]}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unknown role: NOPE"));
    }

    @Test
    void superAdminCanChangeRolePermissions() throws Exception {
        createUser("sa@kronos.test", "SUPER_ADMIN");
        putJson("/api/admin/roles/FACULTY/permissions", "sa@kronos.test",
                "{\"permissions\":[\"FACULTY_READ\",\"ROOM_READ\"]}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permissions.length()").value(2));
        assertEquals(2, roles.findByName("FACULTY").orElseThrow().getPermissions().size());
    }

    @Test
    void instituteAdminCannotChangeRolePermissions() throws Exception {
        createUser("ia@kronos.test", "INSTITUTE_ADMIN");
        putJson("/api/admin/roles/FACULTY/permissions", "ia@kronos.test", "{\"permissions\":[\"FACULTY_READ\"]}")
                .andExpect(status().isForbidden());
    }

    @Test
    void superAdminRoleIsFixed() throws Exception {
        createUser("sa@kronos.test", "SUPER_ADMIN");
        putJson("/api/admin/roles/SUPER_ADMIN/permissions", "sa@kronos.test", "{\"permissions\":[\"FACULTY_READ\"]}")
                .andExpect(status().isConflict());
    }

    @Test
    void emptyPermissionListReturns400() throws Exception {
        createUser("sa@kronos.test", "SUPER_ADMIN");
        putJson("/api/admin/roles/FACULTY/permissions", "sa@kronos.test", "{\"permissions\":[]}")
                .andExpect(status().isBadRequest());
    }

    @Test
    void instituteAdminCanGiveStudentRole() throws Exception {
        UserEntity user = createUser("s@kronos.test", "FACULTY");
        createUser("ia@kronos.test", "INSTITUTE_ADMIN");
        putJson("/api/admin/users/" + user.getId() + "/roles", "ia@kronos.test", "{\"roles\":[\"STUDENT\"]}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles[0]").value("STUDENT"));
    }

    @Test
    void studentCannotListRoles() throws Exception {
        createUser("st@kronos.test", "STUDENT");
        mockMvc.perform(get("/api/admin/roles").header("Authorization", bearer("st@kronos.test")))
                .andExpect(status().isForbidden());
    }

    @Test
    void superAdminCanCreateAnotherSuperAdmin() throws Exception {
        createUser("sa@kronos.test", "SUPER_ADMIN");
        postJson("/api/admin/users", "sa@kronos.test", newUser("sa2@kronos.test", "SUPER_ADMIN"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("sa2@kronos.test"))
                .andExpect(jsonPath("$.roles[0]").value("SUPER_ADMIN"))
                .andExpect(jsonPath("$.password").doesNotExist());
        bearer("sa2@kronos.test");
    }

    @Test
    void instituteAdminCanCreateAnotherInstituteAdmin() throws Exception {
        createUser("ia@kronos.test", "INSTITUTE_ADMIN");
        postJson("/api/admin/users", "ia@kronos.test", newUser("ia2@kronos.test", "INSTITUTE_ADMIN"))
                .andExpect(status().isCreated());
    }

    @Test
    void instituteAdminCannotCreateASuperAdmin() throws Exception {
        createUser("ia@kronos.test", "INSTITUTE_ADMIN");
        postJson("/api/admin/users", "ia@kronos.test", newUser("x@kronos.test", "SUPER_ADMIN"))
                .andExpect(status().isForbidden());
    }

    @Test
    void hodCannotCreateUsers() throws Exception {
        createUser("hod@kronos.test", "HOD");
        postJson("/api/admin/users", "hod@kronos.test", newUser("x@kronos.test", "FACULTY"))
                .andExpect(status().isForbidden());
    }

    @Test
    void duplicateEmailInAnyCaseReturns409() throws Exception {
        createUser("sa@kronos.test", "SUPER_ADMIN");
        createUser("f@kronos.test", "FACULTY");
        postJson("/api/admin/users", "sa@kronos.test", newUser("F@Kronos.test", "FACULTY"))
                .andExpect(status().isConflict());
    }

    @Test
    void shortPasswordReturns400() throws Exception {
        createUser("sa@kronos.test", "SUPER_ADMIN");
        postJson("/api/admin/users", "sa@kronos.test",
                "{\"email\":\"x@kronos.test\",\"password\":\"short\",\"roles\":[\"FACULTY\"]}")
                .andExpect(status().isBadRequest());
    }

    @Test
    void superAdminCanPromoteAnotherUserToSuperAdmin() throws Exception {
        UserEntity ia = createUser("ia@kronos.test", "INSTITUTE_ADMIN");
        createUser("sa@kronos.test", "SUPER_ADMIN");
        putJson("/api/admin/users/" + ia.getId() + "/roles", "sa@kronos.test", "{\"roles\":[\"SUPER_ADMIN\"]}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles[0]").value("SUPER_ADMIN"));
    }

    private UserEntity createUser(String email, String roleName) {
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPassword(encoder.encode(PASSWORD));
        user.getRoles().add(roles.findByName(roleName).orElseThrow());
        return users.save(user);
    }

    private String bearer(String email) throws Exception {
        String body = mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("email", email, "password", PASSWORD))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + json.readTree(body).get("accessToken").asText();
    }

    private ResultActions putJson(String url, String email, String body) throws Exception {
        return mockMvc.perform(put(url)
                .header("Authorization", bearer(email))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private String newUser(String email, String role) throws Exception {
        return json.writeValueAsString(Map.of("email", email, "password", PASSWORD,
                "fullName", "New User", "roles", List.of(role)));
    }

    private ResultActions postJson(String url, String email, String body) throws Exception {
        return mockMvc.perform(post(url)
                .header("Authorization", bearer(email))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }
}
