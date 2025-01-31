package org.mbarek0.folioflex.web.controller.portfolio_components;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.mbarek0.folioflex.model.portfolio_components.PersonalInformation;
import org.mbarek0.folioflex.service.aws.S3Service;
import org.mbarek0.folioflex.service.portfolio_components.PersonalInformationService;
import org.mbarek0.folioflex.web.vm.mapper.PersonalInformationMapper;
import org.mbarek0.folioflex.web.vm.request.CreatePersonalInformationVM;
import org.mbarek0.folioflex.web.vm.response.PersonalInformationVM;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/portfolio/personal-information")
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
            @Valid @ModelAttribute CreatePersonalInformationVM request) {

        // Create personal information
        PersonalInformation personalInformation = personalInformationService.createPersonalInformation(request);

        // Check if there are missing translations for other languages
        boolean hasMissingTranslations = personalInformationService.hasMissingTranslations(request.getUserId());

        // Build the response
        PersonalInformationVM response = personalInformationMapper.toVM(personalInformation);

        List<String> missingLanguages = personalInformationService.getMissingLanguages(request.getUserId());

        // Create a custom response body
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", response); // Include the personal information data
        responseBody.put("status", hasMissingTranslations ? "INCOMPLETE" : "COMPLETE"); // Custom status
        responseBody.put("message", hasMissingTranslations ?
                "Complete the missing translations for the following languages: " + missingLanguages :

                "Personal information created successfully");

        // Always return 200 OK with a custom status in the response body
        return ResponseEntity.ok(responseBody);
    }
}