package com.school.management.common.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void success_withData_shouldSetTimestamp() {
        ApiResponse<String> response = ApiResponse.success("test data");
        assertTrue(response.isSuccess());
        assertEquals("test data", response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void success_withDataAndMessage_shouldSetTimestamp() {
        ApiResponse<String> response = ApiResponse.success("test data", "test message");
        assertTrue(response.isSuccess());
        assertEquals("test data", response.getData());
        assertEquals("test message", response.getMessage());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void successMessage_shouldSetTimestamp() {
        ApiResponse<Object> response = ApiResponse.successMessage("test message");
        assertTrue(response.isSuccess());
        assertNull(response.getData());
        assertEquals("test message", response.getMessage());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void error_shouldNotSetTimestamp() {
        ApiResponse<Object> response = ApiResponse.error("error message");
        assertFalse(response.isSuccess());
        assertEquals("error message", response.getMessage());
        assertNull(response.getTimestamp());
    }

    @Test
    void builder_directUsage_shouldNotSetTimestamp() {
        ApiResponse<Object> response = ApiResponse.builder()
                .success(true)
                .message("built directly")
                .build();
        assertTrue(response.isSuccess());
        assertEquals("built directly", response.getMessage());
        assertNull(response.getTimestamp());
    }
}
