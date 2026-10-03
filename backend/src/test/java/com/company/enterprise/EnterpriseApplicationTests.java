package com.company.enterprise;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class EnterpriseApplicationTests {

    @Test
    void applicationContextLoads() {
        assertNotNull(EnterpriseApplication.class);
    }
}
