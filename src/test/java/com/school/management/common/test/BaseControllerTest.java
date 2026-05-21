package com.school.management.common.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Base class for all controller tests.
 * Provides MockMvc setup, security context, and JSON serialization helpers.
 * 
 * Usage:
 * 
 * @class MyControllerTest extends BaseControllerTest {
 * @MockBean private MyService myService;
 * 
 * @Test
 *       void testGetEndpoint() throws Exception {
 *       mockMvc.perform(get("/api/v1/my-endpoint"))
 *       .andExpect(status().isOk());
 *       }
 *       }
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(roles = "ADMIN") // Default security context for all tests
public abstract class BaseControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    /**
     * Helper to serialize an object to JSON string.
     */
    protected String asJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    /**
     * Helper to deserialize JSON string to an object.
     */
    protected <T> T fromJson(String json, Class<T> clazz) throws Exception {
        return objectMapper.readValue(json, clazz);
    }
}
