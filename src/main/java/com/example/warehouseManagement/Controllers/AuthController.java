package com.example.warehouseManagement.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.warehouseManagement.Domains.User;
import com.example.warehouseManagement.Domains.DTOs.ForgotPasswordDto;
import com.example.warehouseManagement.Domains.DTOs.ResetPasswordDto;
import com.example.warehouseManagement.Domains.DTOs.TwoFactorVerificationDto;
import com.example.warehouseManagement.Security.TwoFactorAuthenticationSuccessHandler;
import com.example.warehouseManagement.Services.EmailService;
import com.example.warehouseManagement.Services.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class AuthController {

    private static final String LOGIN_PATH = "/login";
    private static final String VERIFY_2FA_PATH = "/verify-2fa";
    private static final String FORGOT_PASSWORD_PATH = "/forgot-password";
    private static final String RESET_PASSWORD_PATH = "/reset-password";

    private final UserService userService;
    private final EmailService emailService;
    private final SecurityContextRepository securityContextRepository;
    private final String baseUrl;

    public AuthController(UserService userService,
                          EmailService emailService,
                          SecurityContextRepository securityContextRepository,
                          @Value("${app.auth.base-url}") String baseUrl) {
        this.userService = userService;
        this.emailService = emailService;
        this.securityContextRepository = securityContextRepository;
        this.baseUrl = baseUrl;
    }

    @GetMapping(LOGIN_PATH)
    public String loginPage(Model model) {
        model.addAttribute("title", "Sign in");
        return "auth/login";
    }

    // ----- 2FA -----

    @GetMapping(VERIFY_2FA_PATH)
    public String verify2faForm(HttpSession session, Model model) {
        if (session.getAttribute(TwoFactorAuthenticationSuccessHandler.PENDING_2FA_USERNAME) == null) {
            return "redirect:/login";
        }
        model.addAttribute("title", "Verify");
        model.addAttribute("twoFactor", new TwoFactorVerificationDto());
        return "auth/verify2fa";
    }

    @PostMapping(VERIFY_2FA_PATH)
    public String verify2faSubmit(@Validated @ModelAttribute("twoFactor") TwoFactorVerificationDto dto,
                                  BindingResult binding,
                                  HttpServletRequest request,
                                  HttpServletResponse response,
                                  HttpSession session,
                                  Model model) {
        String username = (String) session.getAttribute(TwoFactorAuthenticationSuccessHandler.PENDING_2FA_USERNAME);
        if (username == null) {
            return "redirect:/login";
        }
        if (binding.hasErrors()) {
            model.addAttribute("title", "Verify");
            return "auth/verify2fa";
        }

        User user = userService.findByUsername(username).orElse(null);
        if (user == null || !user.isEnabled() || !userService.verifyTwoFactorCode(user, dto.getCode())) {
            session.removeAttribute(TwoFactorAuthenticationSuccessHandler.PENDING_2FA_USERNAME);
            return "redirect:/login?invalidCode";
        }

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                user.getUsername(), null, authorities);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        session.removeAttribute(TwoFactorAuthenticationSuccessHandler.PENDING_2FA_USERNAME);
        userService.recordSuccessfulLogin(user);
        return "redirect:/";
    }

    // ----- Forgot password -----

    @GetMapping(FORGOT_PASSWORD_PATH)
    public String forgotPasswordForm(Model model) {
        model.addAttribute("title", "Forgot password");
        model.addAttribute("forgotPassword", new ForgotPasswordDto());
        return "auth/forgotPassword";
    }

    @PostMapping(FORGOT_PASSWORD_PATH)
    public String forgotPasswordSubmit(@Valid @ModelAttribute("forgotPassword") ForgotPasswordDto dto,
                                       BindingResult binding,
                                       Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("title", "Forgot password");
            return "auth/forgotPassword";
        }
        // Always show the same banner — never leak whether the email exists.
        userService.findByEmail(dto.getEmail()).ifPresent(user -> {
            String token = userService.generatePasswordResetToken(user);
            String url = baseUrl + "/reset-password?token=" + token;
            emailService.sendPasswordResetLink(user.getEmail(), url);
        });
        return "redirect:/forgot-password?submitted";
    }

    // ----- Reset password -----

    @GetMapping(RESET_PASSWORD_PATH)
    public String resetPasswordForm(@RequestParam(required = false) String token, Model model) {
        if (token == null || userService.findByValidResetToken(token).isEmpty()) {
            return "redirect:/login?invalidResetToken";
        }
        ResetPasswordDto dto = new ResetPasswordDto();
        dto.setToken(token);
        model.addAttribute("title", "Reset password");
        model.addAttribute("resetPassword", dto);
        return "auth/resetPassword";
    }

    @PostMapping(RESET_PASSWORD_PATH)
    public String resetPasswordSubmit(@Valid @ModelAttribute("resetPassword") ResetPasswordDto dto,
                                      BindingResult binding,
                                      Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("title", "Reset password");
            return "auth/resetPassword";
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            binding.rejectValue("confirmPassword", "mismatch", "Passwords do not match");
            model.addAttribute("title", "Reset password");
            return "auth/resetPassword";
        }
        User user = userService.findByValidResetToken(dto.getToken()).orElse(null);
        if (user == null) {
            return "redirect:/login?invalidResetToken";
        }
        userService.resetPassword(user, dto.getPassword());
        return "redirect:/login?passwordReset";
    }
}
