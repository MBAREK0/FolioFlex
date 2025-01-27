package org.mbarek0.folioflex.web.controller;


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
    private final String frontendUrl;

    public AuthenticationController(AuthenticationService authenticationService, @Value("${app.frontend.url}") String frontendUrl) {
        this.authenticationService = authenticationService;
//        this.frontendUrl = frontendUrl + "/verify-success";
        this.frontendUrl = frontendUrl ;
    }


    @PostMapping("/register")
    public ResponseEntity<TokenVM> register(@RequestBody @Valid RegisterVM userDTO) {
        return ResponseEntity.ok(authenticationService.register(userDTO));
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
       boolean isVerified =  authenticationService.verifyEmail(token);

        if (isVerified) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, frontendUrl)
                    .build();
        } else {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, frontendUrl)
                    .build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenVM> refresh(@RequestBody String refreshToken) {
        return ResponseEntity.ok(authenticationService.refresh(refreshToken));
    }

}
