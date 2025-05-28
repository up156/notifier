package com.notifier.notifier.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notifier.notifier.dto.EventDto;
import com.notifier.notifier.model.Event;
import com.notifier.notifier.repository.EventRepository;
import com.notifier.notifier.repository.NotificationRepository;
import com.notifier.notifier.repository.UserRepository;
import com.notifier.notifier.request.EventCreateRequest;
import com.notifier.notifier.request.EventUpdateRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;

import static com.notifier.notifier.TestData.generateCreatedEventDto;
import static com.notifier.notifier.TestData.generateEvent;
import static com.notifier.notifier.TestData.generateEventCreateRequest;
import static com.notifier.notifier.TestData.generateEventDtoList;
import static com.notifier.notifier.TestData.generateEventUpdateRequest;
import static com.notifier.notifier.TestData.generateExpectedEventDto;
import static com.notifier.notifier.TestData.generateUpdatedEventDto;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/scripts/reset-db.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class EventControllerImplTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16-alpine"
    ).withDatabaseName("transfer_db")
            .withUsername("postgres")
            .withPassword("postgres");

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.scheduling.enabled", () -> "false");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;


    @Test
    void createEventShouldReturnEvent() throws Exception {
        EventCreateRequest request = generateEventCreateRequest();
        EventDto expected = generateCreatedEventDto();

        mockMvc.perform(post("/api/v1/notifier/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void getAllEventsShouldReturnList() throws Exception {

        List<EventDto> expected = generateEventDtoList();

        mockMvc.perform(get("/api/v1/notifier/events"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void getEventShouldReturnEvent() throws Exception {

        EventDto expected = generateExpectedEventDto();

        mockMvc.perform(get("/api/v1/notifier/events/" + expected.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void getEventNotFoundShouldReturn404() throws Exception {

        mockMvc.perform(get("/api/v1/notifier/events/99999"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void updateEventShouldReturnUpdatedEvent() throws Exception {

        EventUpdateRequest request = generateEventUpdateRequest();
        EventDto expected = generateUpdatedEventDto();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/notifier/events/" + expected.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void updateEventNotFoundShouldReturn404() throws Exception {

        EventUpdateRequest request = generateEventUpdateRequest();
        int wrongEventId = 999999;

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/notifier/events/" + wrongEventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteEventShouldReturnOk() throws Exception {

        Event event = generateEvent();

        mockMvc.perform(delete("/api/v1/notifier/events/" + event.getId()))
                .andExpect(status().isOk());

        Assertions.assertFalse(eventRepository.findById(event.getId()).isPresent());
    }

    @Test
    void deleteEventNotFoundShouldReturn404() throws Exception {

        mockMvc.perform(delete("/api/v1/notifier/events/99999"))
                .andExpect(status().is4xxClientError());
    }
}