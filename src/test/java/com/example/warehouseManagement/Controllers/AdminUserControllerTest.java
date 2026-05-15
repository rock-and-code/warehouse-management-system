package com.example.warehouseManagement.Controllers;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.warehouseManagement.Domains.User;
import com.example.warehouseManagement.Domains.User.Role;
import com.example.warehouseManagement.Security.TwoFactorAuthenticationSuccessHandler;
import com.example.warehouseManagement.Services.UserService;

/**
 * Slice test for AdminUserController. Security gating (anonymous → /login, ROLE_USER → 403)
 * relies on the real SecurityConfig and is covered by {@link com.example.warehouseManagement.WarehouseManagementApplicationTests}.
 * Here we only assert controller behavior given an authenticated admin.
 */
@WebMvcTest(AdminUserController.class)
class AdminUserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean private UserService userService;
    @MockBean private TwoFactorAuthenticationSuccessHandler twoFactorAuthenticationSuccessHandler;

    @Test
    void listUsers_asAdmin_returnsAdminUsersView() throws Exception {
        given(userService.findAll()).willReturn(List.of(
                User.builder().username("admin").email("a@x").role(Role.ADMIN).enabled(true).build()));

        mvc.perform(get("/admin/users").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users"))
                .andExpect(model().attribute("title", "Users"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    void userDetails_missingId_redirectsToListWithNotFoundFlag() throws Exception {
        given(userService.findById(999L)).willReturn(Optional.empty());

        mvc.perform(get("/admin/users/999").with(user("admin").roles("ADMIN")))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void userDetails_existingId_returnsDetailsView() throws Exception {
        given(userService.findById(7L)).willReturn(Optional.of(
                User.builder().id(7L).username("alice").email("a@x").role(Role.USER).enabled(true).build()));

        mvc.perform(get("/admin/users/7").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/userDetails"))
                .andExpect(model().attribute("title", "User"))
                .andExpect(model().attributeExists("user"));
    }
}
