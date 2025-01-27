package org.mbarek0.folioflex.web.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mbarek0.folioflex.service.authentication.AuthenticationService;
import org.mbarek0.folioflex.service.authentication.JwtService;
import org.mbarek0.folioflex.web.vm.request.LoginFormVM;
import org.mbarek0.folioflex.web.vm.request.RegisterVM;
import org.mbarek0.folioflex.web.vm.response.TokenVM;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }


    @PostMapping("/register")
    public ResponseEntity<TokenVM> register(@RequestBody @Valid RegisterVM userDTO,HttpServletRequest request) {

        String clientOrigin = request.getHeader(HttpHeaders.ORIGIN);
        if (clientOrigin == null) {
            clientOrigin = request.getHeader(HttpHeaders.REFERER);
        }
        return ResponseEntity.ok(authenticationService.register(userDTO, clientOrigin));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenVM> login(@RequestBody @Valid LoginFormVM request) {

        TokenVM response = authenticationService.login(request.getUsername(), request.getPassword());
        if (response.getToken() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {

        authenticationService.verifyEmail(token);

        return ResponseEntity.ok(" Email verified seccessfully");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email, HttpServletRequest request) {
        String clientOrigin = request.getHeader(HttpHeaders.ORIGIN);
        if (clientOrigin == null) {
            clientOrigin = request.getHeader(HttpHeaders.REFERER);
        }
        authenticationService.forgotPassword(email,clientOrigin);
        return ResponseEntity.ok("Password reset email sent.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {
       authenticationService.resetPassword(token, newPassword);

        return ResponseEntity.ok("Password reset successfully.");
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenVM> refresh(@RequestBody String refreshToken) {
        return ResponseEntity.ok(authenticationService.refresh(refreshToken));
    }

}
