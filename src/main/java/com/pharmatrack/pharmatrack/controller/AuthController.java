
package com.pharmatrack.pharmatrack.controller;

import com.pharmatrack.pharmatrack.entity.AppUser;
import com.pharmatrack.pharmatrack.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest,
            HttpServletResponse response) {

        String username = request.get("username");
        String password = request.get("password");

        if (username == null || password == null ||
                !authService.login(username, password)) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Invalid username or password"));
        }

        AppUser user = authService.findUser(username);

        var authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        new HttpSessionSecurityContextRepository()
                .saveContext(context, httpRequest, response);

        return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "username", user.getUsername(),
                "role", user.getRole()
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<?> currentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Not logged in"));
        }

        return ResponseEntity.ok(Map.of(
                "username", authentication.getName(),
                "role", authentication.getAuthorities().iterator().next()
                        .getAuthority().replace("ROLE_", "")
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        if (username == null || username.isBlank() ||
                password == null || password.length() < 8) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message",
                    "Username is required and password must have at least 8 characters"
            ));
        }

        try {
            AppUser user = authService.createUser(username, password);
            return ResponseEntity.ok(Map.of(
                    "message", "Account created successfully",
                    "username", user.getUsername()
            ));
        } catch (RuntimeException error) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", error.getMessage()));
        }
    }
}
