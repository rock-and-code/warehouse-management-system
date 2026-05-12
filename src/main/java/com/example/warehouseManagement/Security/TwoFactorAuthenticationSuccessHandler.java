package com.example.warehouseManagement.Security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

import com.example.warehouseManagement.Domains.User;
import com.example.warehouseManagement.Services.EmailService;
import com.example.warehouseManagement.Services.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Runs after Spring Security validates the username/password.
 * <p>
 * If the user has two-factor enabled we clear the security context, stash the
 * username on the HttpSession under {@link #PENDING_2FA_USERNAME}, generate +
 * email a code, and redirect to {@code /verify-2fa}. The controller for that
 * endpoint will promote the session to a full {@link Authentication} once the
 * code is verified.
 */
@Component
public class TwoFactorAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    public static final String PENDING_2FA_USERNAME = "PENDING_2FA_USERNAME";

    private final UserService userService;
    private final EmailService emailService;
    private final SecurityContextRepository securityContextRepository;

    public TwoFactorAuthenticationSuccessHandler(UserService userService,
                                                 EmailService emailService,
                                                 SecurityContextRepository securityContextRepository) {
        this.userService = userService;
        this.emailService = emailService;
        this.securityContextRepository = securityContextRepository;
        setDefaultTargetUrl("/");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElse(null);
        if (user == null) {
            // Should not happen — auth just succeeded — but fail closed.
            SecurityContextHolder.clearContext();
            response.sendRedirect("/login?error");
            return;
        }

        if (user.isTwoFactorEnabled()) {
            String code = userService.generateTwoFactorCode(user);
            emailService.sendTwoFactorCode(user.getEmail(), code);

            // Demote the authenticated session: the UsernamePasswordAuthenticationFilter
            // has already persisted the authenticated SecurityContext to the session.
            // Overwrite it with an empty context so the user cannot reach protected pages
            // by skipping /verify-2fa.
            SecurityContextHolder.clearContext();
            securityContextRepository.saveContext(SecurityContextHolder.createEmptyContext(), request, response);
            HttpSession session = request.getSession(true);
            session.setAttribute(PENDING_2FA_USERNAME, username);
            response.sendRedirect("/verify-2fa");
            return;
        }

        userService.recordSuccessfulLogin(user);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
