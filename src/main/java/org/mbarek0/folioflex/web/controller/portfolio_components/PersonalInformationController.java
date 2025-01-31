//package org.mbarek0.folioflex.web.controller.portfolio_components;
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import lombok.AllArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@AllArgsConstructor
//@RequestMapping("/api/personal-informations")
//@Tag(name = "Personal Information", description = "APIs for managing personal information")
//public class PersonalInformationController {
//
//    private final PersonalInformationService personalInformationService;
//
//    @PostMapping
//    @Operation(
//            summary = "Create personal information",
//            description = "Creates personal information for a user in a specific language",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "Personal information created successfully"),
//                    @ApiResponse(responseCode = "400", description = "Invalid input")
//            }
//    )
//    public ResponseEntity<PersonalInformation> createPersonalInformation(
//            @Valid @RequestBody CreatePersonalInformationVM request) {
//        PersonalInformation personalInformation = personalInformationService.createPersonalInformation(request);
//        return ResponseEntity.ok(personalInformation);
//    }
//
//    @PutMapping
//    @Operation(
//            summary = "Update personal information",
//            description = "Updates personal information for a user in a specific language",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "Personal information updated successfully"),
//                    @ApiResponse(responseCode = "404", description = "Personal information not found")
//            }
//    )
//    public ResponseEntity<PersonalInformation> updatePersonalInformation(
//            @Valid @RequestBody UpdatePersonalInformationVM request) {
//        PersonalInformation personalInformation = personalInformationService.updatePersonalInformation(request);
//        return ResponseEntity.ok(personalInformation);
//    }
//
//    @DeleteMapping
//    @Operation(
//            summary = "Delete personal information",
//            description = "Deletes personal information for a user in a specific language",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "Personal information deleted successfully"),
//                    @ApiResponse(responseCode = "404", description = "Personal information not found")
//            }
//    )
//    public ResponseEntity<String> deletePersonalInformation(
//            @Parameter(name = "userId", description = "User ID", required = true, example = "1")
//            @RequestParam Long userId,
//
//            @Parameter(name = "languageCode", description = "Language Code", required = true, example = "en")
//            @RequestParam String languageCode) {
//        personalInformationService.deletePersonalInformation(userId, languageCode);
//        return ResponseEntity.ok("Personal information deleted successfully");
//    }
//
//    @GetMapping("/user/{userId}")
//    @Operation(
//            summary = "Get all personal information for a user",
//            description = "Returns all personal information for the specified user",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "Successfully retrieved personal information"),
//                    @ApiResponse(responseCode = "404", description = "User not found")
//            }
//    )
//    public ResponseEntity<List<PersonalInformation>> getAllPersonalInformationForUser(
//            @PathVariable Long userId) {
//        List<PersonalInformation> personalInformationList = personalInformationService.getAllPersonalInformationForUser(userId);
//        return ResponseEntity.ok(personalInformationList);
//    }
//
//    @GetMapping("/user/{userId}/language/{languageCode}")
//    @Operation(
//            summary = "Get personal information for a user and language",
//            description = "Returns personal information for the specified user and language",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "Successfully retrieved personal information"),
//                    @ApiResponse(responseCode = "404", description = "Personal information not found")
//            }
//    )
//    public ResponseEntity<PersonalInformation> getPersonalInformationForUserAndLanguage(
//            @PathVariable Long userId,
//            @PathVariable String languageCode) {
//        PersonalInformation personalInformation = personalInformationService.getPersonalInformationForUserAndLanguage(userId, languageCode);
//        return ResponseEntity.ok(personalInformation);
//    }
//}