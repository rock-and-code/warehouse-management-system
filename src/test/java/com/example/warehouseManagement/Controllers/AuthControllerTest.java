package com.example.warehouseManagement.Controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;

import com.example.warehouseManagement.Security.TwoFactorAuthenticationSuccessHandler;
import com.example.warehouseManagement.Services.EmailService;
import com.example.warehouseManagement.Services.UserService;

/**
 * Slice test for AuthController. All four endpoints are permitAll in the real
 * SecurityConfig — we disable the test-only security filter chain here so the
 * default 401-everywhere behavior doesn't shadow the actual controller logic.
 * Real security gating is covered by {@link com.example.warehouseManagement.WarehouseManagementApplicationTests}.
 */
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean private UserService userService;
    @MockBean private EmailService emailService;
    @MockBean private SecurityContextRepository securityContextRepository;
    @MockBean private TwoFactorAuthenticationSuccessHandler twoFactorAuthenticationSuccessHandler;

    @Test
    void loginPage_isPermitAllAndRendersAuthLoginView() throws Exception {
        mvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attribute("title", "Sign in"));
    }

    @Test
    void verify2faForm_withoutPending2faSession_redirectsToLogin() throws Exception {
        mvc.perform(get("/verify-2fa"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void verify2faForm_withPending2faSession_rendersVerifyView() throws Exception {
        mvc.perform(get("/verify-2fa")
                        .sessionAttr(TwoFactorAuthenticationSuccessHandler.PENDING_2FA_USERNAME, "manager"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/verify2fa"))
                .andExpect(model().attribute("title", "Verify"))
                .andExpect(model().attributeExists("twoFactor"));
    }

    @Test
    void forgotPasswordForm_isPermitAllAndRendersView() throws Exception {
        mvc.perform(get("/forgot-password"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/forgotPassword"))
                .andExpect(model().attribute("title", "Forgot password"));
    }

    @Test
    void forgotPasswordSubmit_invalidEmail_rerendersForm() throws Exception {
        mvc.perform(post("/forgot-password")
                        .with(csrf())
                        .param("email", "not-an-email"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/forgotPassword"));
    }

    @Test
    void forgotPasswordSubmit_validEmail_redirectsWithSubmittedFlag() throws Exception {
        mvc.perform(post("/forgot-password")
                        .with(csrf())
                        .param("email", "someone@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/forgot-password?submitted"));
    }

    @Test
    void resetPasswordForm_withoutToken_redirectsToLoginWithInvalidFlag() throws Exception {
        mvc.perform(get("/reset-password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?invalidResetToken"));
    }
}
