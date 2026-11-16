package com.kronos.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kronos.enums.SubjectType;
import com.kronos.persistence.entity.SubjectEntity;
import com.kronos.persistence.entity.UserEntity;
import com.kronos.persistence.repository.RoleRepository;
import com.kronos.persistence.repository.SubjectRepository;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FacultyControllerTest {

    private static final String PASSWORD = "S3cret-pass!";
    private static final String ADMIN = "admin@kronos.test";
    private static final String VIEWER = "viewer@kronos.test";

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private FilterChainProxy springSecurityFilterChain;
    @Autowired
    private UserRepository users;
    @Autowired
    private RoleRepository roles;
    @Autowired
    private SubjectRepository subjects;
    @Autowired
    private PasswordEncoder encoder;

    private final ObjectMapper json = new ObjectMapper();
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(springSecurityFilterChain)
                .build();
        createUser(ADMIN, "INSTITUTE_ADMIN");
        createUser(VIEWER, "FACULTY");
    }

    @Test
    void listWithoutTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/faculty")).andExpect(status().isUnauthorized());
    }

    @Test
    void facultyRoleCanList() throws Exception {
        authorized(get("/api/faculty"), VIEWER)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void facultyRoleCannotCreate() throws Exception {
        authorizedJson(post("/api/faculty"), VIEWER, body("Asha", "asha@kronos.test", List.of()))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCreatesFacultyWithSubjects() throws Exception {
        SubjectEntity subject = saveSubject("TST-1");
        authorizedJson(post("/api/faculty"), ADMIN, body("Asha", "asha@kronos.test", List.of(subject.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email").value("asha@kronos.test"))
                .andExpect(jsonPath("$.subjectIds[0]").value(subject.getId().intValue()));
    }

    @Test
    void duplicateEmailIgnoringCaseReturns409() throws Exception {
        create("dup@kronos.test");
        authorizedJson(post("/api/faculty"), ADMIN, body("Other", "DUP@kronos.test", List.of()))
                .andExpect(status().isConflict());
    }

    @Test
    void invalidBodyReturns400WithFieldErrors() throws Exception {
        authorizedJson(post("/api/faculty"), ADMIN, body("", "not-an-email", List.of()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors.length()").value(2));
    }

    @Test
    void unknownSubjectReturns400() throws Exception {
        authorizedJson(post("/api/faculty"), ADMIN, body("Asha", "asha@kronos.test", List.of(999999L)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Subject not found with id: 999999"));
    }

    @Test
    void unknownIdReturns404() throws Exception {
        authorized(get("/api/faculty/999999"), VIEWER)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Faculty not found with id: 999999"));
    }

    @Test
    void updateChangesFieldsAndSubjects() throws Exception {
        long id = create("old@kronos.test");
        SubjectEntity subject = saveSubject("TST-2");
        authorizedJson(put("/api/faculty/" + id), ADMIN, body("Meera", "new@kronos.test", List.of(subject.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Meera"))
                .andExpect(jsonPath("$.email").value("new@kronos.test"))
                .andExpect(jsonPath("$.subjectIds.length()").value(1));
    }

    @Test
    void updateWithEmailOfAnotherTeacherReturns409() throws Exception {
        create("first@kronos.test");
        long second = create("second@kronos.test");
        authorizedJson(put("/api/faculty/" + second), ADMIN, body("Second", "first@kronos.test", List.of()))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteRemovesFaculty() throws Exception {
        long id = create("gone@kronos.test");
        authorized(delete("/api/faculty/" + id), ADMIN).andExpect(status().isNoContent());
        authorized(get("/api/faculty/" + id), VIEWER).andExpect(status().isNotFound());
    }

    @Test
    void facultyRoleCannotDelete() throws Exception {
        long id = create("keep@kronos.test");
        authorized(delete("/api/faculty/" + id), VIEWER).andExpect(status().isForbidden());
    }

    private long create(String email) throws Exception {
        String response = authorizedJson(post("/api/faculty"), ADMIN, body("Asha", email, List.of()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return json.readTree(response).get("id").asLong();
    }

    private String body(String firstName, String email, List<Long> subjectIds) throws Exception {
        return json.writeValueAsString(Map.of(
                "firstName", firstName,
                "lastName", "Rao",
                "email", email,
                "subjectIds", subjectIds));
    }

    private SubjectEntity saveSubject(String code) {
        return subjects.save(SubjectEntity.builder()
                .subjectCode(code)
                .subjectName("Test subject")
                .weight(1)
                .slotDuration(1)
                .subjectType(SubjectType.values()[0])
                .build());
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

    private ResultActions authorized(MockHttpServletRequestBuilder request, String email) throws Exception {
        return mockMvc.perform(request.header("Authorization", bearer(email)));
    }

    private ResultActions authorizedJson(MockHttpServletRequestBuilder request, String email, String content)
            throws Exception {
        return mockMvc.perform(request.header("Authorization", bearer(email))
                .contentType(MediaType.APPLICATION_JSON)
                .content(content));
    }
}
