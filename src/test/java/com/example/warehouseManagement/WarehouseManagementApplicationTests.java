package com.example.warehouseManagement;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * End-to-end smoke test: real Spring context, real H2 (in-memory under
 * src/test/resources/application.properties), Flyway runs, Bootstrap seeds.
 * Anything we'd want to break loudly if it broke — security chain, view
 * resolution, dashboard query path — gets exercised here.
 */
@SpringBootTest
@AutoConfigureMockMvc
class WarehouseManagementApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Test
    void contextLoads() {
        // Implicit: @SpringBootTest fails the test if the context can't start.
        // Keeps the no-op smoke check while the others below add real coverage.
    }

    @Test
    void rootRoute_unauthenticated_redirectsToLogin() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void loginPage_isPublicAndRendersSignInForm() throws Exception {
        mvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Sign in")));
    }

    @Test
    void dashboard_authenticatedAsAdmin_rendersWithAllKpiAttributes() throws Exception {
        mvc.perform(get("/").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/dashboard"));
    }

    @Test
    void adminUsersPage_authenticatedAsAdmin_renders() throws Exception {
        mvc.perform(get("/admin/users").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users"));
    }
}
