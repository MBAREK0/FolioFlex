package org.mbarek0.folioflex.web.controller.authentication;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.mbarek0.folioflex.service.authentication.AuthenticationService;
import org.mbarek0.folioflex.web.vm.request.LoginFormVM;
import org.mbarek0.folioflex.web.vm.request.RegisterVM;
import org.mbarek0.folioflex.web.vm.response.TokenVM;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "APIs for user authentication and registration")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

//--------- Register a new user -----------------------------------------------------------------------------------------
    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Registers a new user and returns an authentication token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User registered successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    public ResponseEntity<TokenVM> register(
            @Parameter(description = "User registration details", required = true)
            @RequestBody @Valid RegisterVM userDTO, HttpServletRequest request) {

        String clientOrigin = request.getHeader(HttpHeaders.ORIGIN);
        if (clientOrigin == null) {
            clientOrigin = request.getHeader(HttpHeaders.REFERER);
        }
        return ResponseEntity.ok(authenticationService.register(userDTO, clientOrigin));
    }

//--------- Authenticate a user -----------------------------------------------------------------------------------------
    @PostMapping("/login")
    @Operation(
            summary = "Authenticate a user",
            description = "Authenticates a user and returns an authentication token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User authenticated successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    public ResponseEntity<TokenVM> login(@Parameter(description = "User login credentials", required = true)
                                         @RequestBody @Valid LoginFormVM request) {

        TokenVM response = authenticationService.login(request.getUsername(), request.getPassword());
        if (response.getToken() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(response);
    }

//-------- Verify user email -------------------------------------------------------------------------------------------
    @GetMapping("/verify")
    @Operation(
            summary = "Verify user email",
            description = "Verifies a user's email address using a verification token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Email verified successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid token")
            }
    )
    public ResponseEntity<String> verifyEmail(@Parameter(description = "Email verification token", required = true)
                                              @RequestParam String token) {

        authenticationService.verifyEmail(token);
        return ResponseEntity.ok(" Email verified seccessfully");
    }

//-------- Request password reset --------------------------------------------------------------------------------------
    @PostMapping("/forgot-password")
    @Operation(
            summary = "Request password reset",
            description = "Sends a password reset email to the user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password reset email sent"),
                    @ApiResponse(responseCode = "400", description = "Invalid email")
            }
    )
    public ResponseEntity<String> forgotPassword(@Parameter(description = "User email address", required = true)
                                                 @RequestParam String email, HttpServletRequest request) {
        String clientOrigin = request.getHeader(HttpHeaders.ORIGIN);
        if (clientOrigin == null) {
            clientOrigin = request.getHeader(HttpHeaders.REFERER);
        }
        authenticationService.forgotPassword(email,clientOrigin);
        return ResponseEntity.ok("Password reset email sent.");
    }

//------- Reset user password -------------------------------------------------------------------------------------------
    @PostMapping("/reset-password")
    @Operation(
            summary = "Reset user password",
            description = "Resets the user's password using a reset token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password reset successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid token or password")
            }
    )
    public ResponseEntity<String> resetPassword(
            @Parameter(description = "Password reset token", required = true) @RequestParam String token,
            @Parameter(description = "New password", required = true) @RequestParam String newPassword) {
       authenticationService.resetPassword(token, newPassword);

        return ResponseEntity.ok("Password reset successfully.");
    }

//------- Reset user password -------------------------------------------------------------------------------------------
    @PostMapping("/refresh")
    @Operation(
            summary = "Refresh authentication token",
            description = "Refreshes the user's authentication token using a refresh token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid refresh token")
            }
    )
    public ResponseEntity<TokenVM> refresh(@Parameter(description = "Refresh token", required = true)
                                           @RequestBody String refreshToken) {
        return ResponseEntity.ok(authenticationService.refresh(refreshToken));
    }

}
