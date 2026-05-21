package com.school.management.common.test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

/**
 * Base class for all service tests.
 * Provides Mockito setup and common test helpers for service-layer testing.
 * 
 * Usage:
 * 
 * @class MyServiceTest extends BaseServiceTest {
 * @Mock private MyRepository myRepository;
 * @InjectMocks private MyService myService;
 * 
 * @Test
 *       void testServiceMethod() {
 *       when(myRepository.findById(1L)).thenReturn(Optional.of(testEntity));
 *       MyResult result = myService.doSomething(1L);
 *       assertThat(result).isNotNull();
 *       verify(myRepository).findById(1L);
 *       }
 *       }
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public abstract class BaseServiceTest {

    /**
     * Override this in test classes if custom test setup is needed.
     */
    protected void setupTestData() {
        // Override in subclasses
    }
}
