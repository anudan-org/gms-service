package org.codealpha.gmsservice.controllers;


import org.codealpha.gmsservice.entities.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecureTestController {

    @GetMapping("/secure/test")
    public Object testSecureEndpoint() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        // This should be your User entity (set in JwtAuthenticationFilter)
        User user = (User) authentication.getPrincipal();

        return new TestResponse(
                "JWT validated successfully",
                user.getEmailId(),
                user.getOrganization().getCode()
        );
    }

    // Simple response DTO (record is perfect here)
    public record TestResponse(
            String message,
            String email,
            String tenantCode
    ) {}
}
