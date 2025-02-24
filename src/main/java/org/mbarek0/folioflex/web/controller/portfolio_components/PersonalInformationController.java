package org.mbarek0.folioflex.web.controller.portfolio_components;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.mbarek0.folioflex.model.portfolio_components.PersonalInformation;
import org.mbarek0.folioflex.service.portfolio_components.PersonalInformationService;
import org.mbarek0.folioflex.web.vm.mapper.PersonalInformationMapper;
import org.mbarek0.folioflex.web.vm.request.portfolio_components.PersonalInformationRequestVM;
import org.mbarek0.folioflex.web.vm.response.portfolio_components.PersonalInformationResponseVM;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/portfolio/personal-information")
@Tag(name = "Personal Information", description = "APIs for managing personal information")
public class PersonalInformationController {

    private final PersonalInformationService personalInformationService;
    private final PersonalInformationMapper personalInformationMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Create personal information",
            description = "Creates personal information for a user in a specific language",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Personal information created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    public ResponseEntity<Map<String, Object>> createPersonalInformation(
            @Valid @ModelAttribute PersonalInformationRequestVM request) {

        PersonalInformation personalInformation = personalInformationService.createPersonalInformation(request);

        boolean hasMissingTranslations = personalInformationService.hasMissingTranslations(request.getUserId());

        PersonalInformationResponseVM response = personalInformationMapper.toVM(personalInformation);

        List<String> missingLanguages = personalInformationService.getMissingLanguages(request.getUserId());

        // Create a custom response body
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", response);
        responseBody.put("status", hasMissingTranslations ? "INCOMPLETE" : "COMPLETE");
        responseBody.put("message", hasMissingTranslations ?
                "Complete the missing translations for the following languages: " + missingLanguages :

                "Personal information created successfully");

        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/{username}/primary")
    @Operation(
            summary = "Get personal information with specific language for specific user",
            description = "Retrieves personal information for a user in a specific language by username",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Personal information retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Personal information not found")
            }
    )
    public ResponseEntity<PersonalInformationResponseVM> getPersonalInformation(
            @Parameter(description = "Username of the user", required = true)
            @PathVariable String username) {

        PersonalInformation personalInformation = personalInformationService.getPersonalInformation(username, null);
        PersonalInformationResponseVM response = personalInformationMapper.toVM(personalInformation);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{username}/{languageCode}")
    @Operation(
            summary = "Get personal information with specific language for specific user",
            description = "Retrieves personal information for a user in a specific language by username",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Personal information retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Personal information not found")
            }
    )
    public ResponseEntity<PersonalInformationResponseVM> getPersonalInformation(
            @Parameter(description = "Username of the user", required = true)
            @PathVariable String username,
            @Parameter(description = "Language code , if not provided, will return data by all languages")
            @PathVariable String languageCode) {

        PersonalInformation personalInformation = personalInformationService.getPersonalInformation(username, languageCode);
        PersonalInformationResponseVM response = personalInformationMapper.toVM(personalInformation);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{username}")
    @Operation(
            summary = "Get all personal information for specific user",
            description = "Retrieves all personal information for a user by username",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Personal information retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Personal information not found")
            }
    )
    public ResponseEntity<List<PersonalInformationResponseVM>> getallPersonalInformation(
            @Parameter(description = "Username of the user", required = true)
            @PathVariable String username) {

        List<PersonalInformation> personalInformation = personalInformationService.getAllPersonalInformation(username);
        List<PersonalInformationResponseVM> response = personalInformation.stream()
                .map(personalInformationMapper::toVM)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = "/{id}")
    @Operation(
            summary = "Update personal information",
            description = "Updates personal information for a user in a specific language",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Personal information updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    public ResponseEntity<PersonalInformationResponseVM> updatePersonalInformation(
            @Parameter(description = "Personal information ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody @ModelAttribute PersonalInformationRequestVM request) {

        PersonalInformation personalInformation = personalInformationService.updatePersonalInformation(id, request);
        PersonalInformationResponseVM response = personalInformationMapper.toVM(personalInformation);

        return ResponseEntity.ok(response);
    }

}