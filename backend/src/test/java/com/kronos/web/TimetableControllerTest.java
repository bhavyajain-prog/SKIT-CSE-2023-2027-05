package com.kronos.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kronos.enums.SubjectType;
import com.kronos.persistence.entity.BatchEntity;
import com.kronos.persistence.entity.RoomEntity;
import com.kronos.persistence.entity.SubjectEntity;
import com.kronos.persistence.entity.TeacherEntity;
import com.kronos.persistence.entity.UserEntity;
import com.kronos.persistence.repository.BatchRepository;
import com.kronos.persistence.repository.RoleRepository;
import com.kronos.persistence.repository.RoomRepository;
import com.kronos.persistence.repository.SubjectRepository;
import com.kronos.persistence.repository.TeacherRepository;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TimetableControllerTest {

    private static final String PASSWORD = "S3cret-pass!";
    private static final String HOD = "hod@kronos.test";
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
    private TeacherRepository teachers;
    @Autowired
    private BatchRepository batches;
    @Autowired
    private RoomRepository rooms;
    @Autowired
    private PasswordEncoder encoder;

    private final ObjectMapper json = new ObjectMapper();
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(springSecurityFilterChain)
                .build();
        createUser(HOD, "HOD");
        createUser(VIEWER, "FACULTY");
    }

    @Test
    void generateWithoutTokenReturns401() throws Exception {
        mockMvc.perform(post("/api/timetables/generate").contentType(MediaType.APPLICATION_JSON)
                        .content(body("Odd semester", 5, 6, 3)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void facultyRoleCannotGenerate() throws Exception {
        authorizedJson(post("/api/timetables/generate"), VIEWER, body("Odd semester", 5, 6, 3))
                .andExpect(status().isForbidden());
    }

    @Test
    void hodGeneratesTimetableFromStoredMasterData() throws Exception {
        seedTwoBatches();

        JsonNode detail = generate("Odd semester");

        assertThat(detail.at("/summary/id").isNumber()).isTrue();
        assertThat(detail.at("/summary/name").asText()).isEqualTo("Odd semester");
        // Maths (3 x 1 slot) and a lab (1 x 2 slots) for each of two batches.
        assertThat(detail.at("/summary/scheduledCount").asLong()).isEqualTo(8);
        assertThat(detail.get("unscheduled")).isEmpty();
        assertThat(detail.at("/summary/createdAt").isMissingNode()).isFalse();
    }

    @Test
    void generatedTimetableHasNoClashesAndKeepsLunchFree() throws Exception {
        seedTwoBatches();

        JsonNode scheduled = generate("Odd semester").get("scheduled");

        Set<String> teacherSlots = new HashSet<>();
        Set<String> batchSlots = new HashSet<>();
        Set<String> roomSlots = new HashSet<>();
        for (JsonNode slot : scheduled) {
            int day = slot.get("dayOfWeek").asInt();
            int start = slot.get("startSlot").asInt();
            int duration = slot.get("slotDuration").asInt();
            for (int s = start; s < start + duration; s++) {
                assertThat(s).isNotEqualTo(3).isLessThan(6);
                String key = day + ":" + s;
                assertThat(teacherSlots.add(slot.get("teacherId").asLong() + "@" + key)).isTrue();
                assertThat(batchSlots.add(slot.get("batchId").asLong() + "@" + key)).isTrue();
                assertThat(roomSlots.add(slot.get("roomId").asLong() + "@" + key)).isTrue();
            }
        }
    }

    @Test
    void labGoesToARoomBigEnoughForTheBatch() throws Exception {
        seedTwoBatches();

        JsonNode scheduled = generate("Odd semester").get("scheduled");

        Map<String, Integer> capacityByRoom = new HashMap<>();
        rooms.findAll().forEach(room -> capacityByRoom.put(room.getName(), room.getCapacity()));
        Map<String, Integer> strengthByBatch = new HashMap<>();
        batches.findAll().forEach(batch ->
                strengthByBatch.put(batch.getName() + " " + batch.getSection(), batch.getStrength()));
        for (JsonNode slot : scheduled) {
            assertThat(capacityByRoom.get(slot.get("roomName").asText()))
                    .isGreaterThanOrEqualTo(strengthByBatch.get(slot.get("batchName").asText()));
        }
    }

    @Test
    void generatedTimetableCanBeListedAndFetched() throws Exception {
        seedTwoBatches();
        long id = generate("Odd semester").at("/summary/id").asLong();

        authorized(get("/api/timetables"), VIEWER)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id))
                .andExpect(jsonPath("$[0].scheduledCount").value(8))
                .andExpect(jsonPath("$[0].unscheduledCount").value(0));

        authorized(get("/api/timetables/" + id), VIEWER)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary.name").value("Odd semester"))
                .andExpect(jsonPath("$.scheduled.length()").value(8))
                .andExpect(jsonPath("$.scheduled[0].subjectCode").isString())
                .andExpect(jsonPath("$.scheduled[0].day").isString());
    }

    @Test
    void sessionsThatFitNoRoomAreReportedAsUnscheduled() throws Exception {
        SubjectEntity maths = saveSubject("MA101", 2, 1, SubjectType.LECTURE);
        saveTeacher("asha@kronos.test", List.of(maths));
        saveBatch("CSE", "A", 200, List.of(maths));
        saveRoom("LT-1", 60);

        JsonNode detail = generate("Too big");

        assertThat(detail.get("scheduled")).isEmpty();
        assertThat(detail.get("unscheduled")).hasSize(2);
        assertThat(detail.at("/unscheduled/0/roomId").isNull()).isTrue();
    }

    @Test
    void invalidSettingsReturn400WithFieldErrors() throws Exception {
        authorizedJson(post("/api/timetables/generate"), HOD, body("", 9, 0, -1))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors.length()").value(4));
    }

    @Test
    void lunchOutsideTheDayReturns400() throws Exception {
        authorizedJson(post("/api/timetables/generate"), HOD, body("Odd semester", 5, 6, 6))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("lunchStart must be less than maxSlots"));
    }

    @Test
    void subjectWithoutTeacherReturns400() throws Exception {
        SubjectEntity maths = saveSubject("MA101", 2, 1, SubjectType.LECTURE);
        saveBatch("CSE", "A", 30, List.of(maths));
        saveRoom("LT-1", 60);

        authorizedJson(post("/api/timetables/generate"), HOD, body("Odd semester", 5, 6, 3))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cannot build sessions: No teacher mapped for Maths"));
    }

    @Test
    void unknownTimetableReturns404() throws Exception {
        authorized(get("/api/timetables/999999"), VIEWER)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Timetable not found with id: 999999"));
    }

    @Test
    void deleteRemovesTimetable() throws Exception {
        seedTwoBatches();
        long id = generate("Odd semester").at("/summary/id").asLong();

        authorized(delete("/api/timetables/" + id), VIEWER).andExpect(status().isForbidden());
        authorized(delete("/api/timetables/" + id), HOD).andExpect(status().isNoContent());
        authorized(get("/api/timetables/" + id), VIEWER).andExpect(status().isNotFound());
    }

    private void seedTwoBatches() {
        SubjectEntity maths = saveSubject("MA101", 3, 1, SubjectType.LECTURE);
        SubjectEntity lab = saveSubject("CS101L", 1, 2, SubjectType.LAB);
        saveTeacher("asha@kronos.test", List.of(maths));
        saveTeacher("meera@kronos.test", List.of(lab));
        saveBatch("CSE", "A", 60, List.of(maths, lab));
        saveBatch("CSE", "B", 30, List.of(maths, lab));
        saveRoom("LT-1", 70);
        saveRoom("LAB-1", 35);
    }

    private JsonNode generate(String name) throws Exception {
        String response = authorizedJson(post("/api/timetables/generate"), HOD, body(name, 5, 6, 3))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return json.readTree(response);
    }

    private String body(String name, int weekDays, int maxSlots, int lunchStart) throws Exception {
        return json.writeValueAsString(Map.of(
                "name", name,
                "weekDays", weekDays,
                "maxSlots", maxSlots,
                "lunchStart", lunchStart));
    }

    private SubjectEntity saveSubject(String code, int weight, int slotDuration, SubjectType type) {
        return subjects.save(SubjectEntity.builder()
                .subjectCode(code)
                .subjectName(type == SubjectType.LAB ? "Programming Lab" : "Maths")
                .weight(weight)
                .slotDuration(slotDuration)
                .subjectType(type)
                .build());
    }

    private void saveTeacher(String email, List<SubjectEntity> taught) {
        teachers.save(TeacherEntity.builder()
                .firstName(email.substring(0, email.indexOf('@')))
                .lastName("Rao")
                .email(email)
                .subjects(taught)
                .build());
    }

    private void saveBatch(String name, String section, int strength, List<SubjectEntity> taken) {
        batches.save(BatchEntity.builder()
                .name(name)
                .section(section)
                .strength(strength)
                .subjects(taken)
                .build());
    }

    private void saveRoom(String name, int capacity) {
        rooms.save(RoomEntity.builder().name(name).capacity(capacity).build());
    }

    private void createUser(String email, String roleName) {
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPassword(encoder.encode(PASSWORD));
        user.getRoles().add(roles.findByName(roleName).orElseThrow());
        users.save(user);
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
