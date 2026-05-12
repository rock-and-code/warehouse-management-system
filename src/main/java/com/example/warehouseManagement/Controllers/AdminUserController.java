package com.example.warehouseManagement.Controllers;

import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.warehouseManagement.Domains.User;
import com.example.warehouseManagement.Domains.User.Role;
import com.example.warehouseManagement.Domains.DTOs.UserFormDto;
import com.example.warehouseManagement.Domains.Exceptions.DuplicateUserException;
import com.example.warehouseManagement.Domains.Exceptions.UserNotFoundException;
import com.example.warehouseManagement.Services.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private static final String NEW_USER_PATH = "/new";
    private static final String USER_ID_PATH = "/{userId}";
    private static final String UPDATE_USER_ID_PATH = USER_ID_PATH + "/update";

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("title", "Users");
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }

    @GetMapping(NEW_USER_PATH)
    public String newUserForm(Model model) {
        model.addAttribute("title", "New user");
        model.addAttribute("userForm", UserFormDto.builder().role(Role.USER).enabled(true).build());
        model.addAttribute("roles", Role.values());
        model.addAttribute("isCreate", true);
        return "admin/newUserForm";
    }

    @PostMapping(NEW_USER_PATH)
    public String createUser(@Valid @ModelAttribute("userForm") UserFormDto dto,
                             BindingResult binding,
                             Model model) {
        if (binding.hasErrors()) {
            return rerenderForm(model, "admin/newUserForm", "New user", true);
        }
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            binding.rejectValue("password", "required", "Password is required");
            return rerenderForm(model, "admin/newUserForm", "New user", true);
        }
        try {
            User saved = userService.create(dto);
            return "redirect:/admin/users/" + saved.getId() + "?created";
        } catch (DuplicateUserException e) {
            binding.reject("duplicate", e.getMessage());
            return rerenderForm(model, "admin/newUserForm", "New user", true);
        }
    }

    @GetMapping(USER_ID_PATH)
    public String userDetails(@PathVariable Long userId, Model model) {
        Optional<User> user = userService.findById(userId);
        if (user.isEmpty()) {
            return "redirect:/admin/users?notFound";
        }
        model.addAttribute("title", "User");
        model.addAttribute("user", user.get());
        return "admin/userDetails";
    }

    @GetMapping(UPDATE_USER_ID_PATH)
    public String updateUserForm(@PathVariable Long userId, Model model) {
        Optional<User> user = userService.findById(userId);
        if (user.isEmpty()) {
            return "redirect:/admin/users?notFound";
        }
        User u = user.get();
        UserFormDto dto = UserFormDto.builder()
                .id(u.getId())
                .username(u.getUsername())
                .email(u.getEmail())
                .role(u.getRole())
                .enabled(u.isEnabled())
                .twoFactorEnabled(u.isTwoFactorEnabled())
                .build();
        model.addAttribute("title", "Edit user");
        model.addAttribute("userForm", dto);
        model.addAttribute("roles", Role.values());
        model.addAttribute("isCreate", false);
        return "admin/updateUserForm";
    }

    @PostMapping(UPDATE_USER_ID_PATH)
    public String updateUser(@PathVariable Long userId,
                             @Valid @ModelAttribute("userForm") UserFormDto dto,
                             BindingResult binding,
                             Model model) {
        if (binding.hasErrors()) {
            return rerenderForm(model, "admin/updateUserForm", "Edit user", false);
        }
        try {
            userService.update(userId, dto);
            return "redirect:/admin/users/" + userId + "?updated";
        } catch (UserNotFoundException e) {
            return "redirect:/admin/users?notFound";
        } catch (DuplicateUserException e) {
            binding.reject("duplicate", e.getMessage());
            return rerenderForm(model, "admin/updateUserForm", "Edit user", false);
        }
    }

    @PostMapping(value = USER_ID_PATH, params = "delete")
    public String deleteUser(@PathVariable Long userId,
                             @AuthenticationPrincipal UserDetails principal) {
        // Don't let an admin delete themselves and lock the system out.
        Optional<User> user = userService.findById(userId);
        if (user.isEmpty()) {
            return "redirect:/admin/users?notFound";
        }
        if (principal != null && user.get().getUsername().equals(principal.getUsername())) {
            return "redirect:/admin/users/" + userId + "?cannotDeleteSelf";
        }
        userService.delete(userId);
        return "redirect:/admin/users?deleted";
    }

    private String rerenderForm(Model model, String view, String title, boolean isCreate) {
        model.addAttribute("title", title);
        model.addAttribute("roles", Role.values());
        model.addAttribute("isCreate", isCreate);
        return view;
    }
}
