package org.mbarek0.folioflex.web.controller.portfolio_components;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.mbarek0.folioflex.model.portfolio_components.Education;
import org.mbarek0.folioflex.service.portfolio_components.EducationService;
import org.mbarek0.folioflex.web.vm.mapper.EducationMapper;
import org.mbarek0.folioflex.web.vm.request.portfolio_components.EducationRequestVM;
import org.mbarek0.folioflex.web.vm.response.portfolio_components.EducationResponseVM;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/portfolio/education")
@Tag(name = "Education", description = "APIs for managing education")
public class EducationController {

    private final EducationService educationService;
    private final EducationMapper educationMapper;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Create education entries",
            description = "Creates multiple education entries with a school logo file upload",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Education entries created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = EducationResponseVM.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data or validation errors"
                    )
            }
    )
    public ResponseEntity<List<EducationResponseVM>> createEducation(
            @Parameter(
                    description = "JSON array of education objects",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = EducationRequestVM.class),
                            examples = @ExampleObject(
                                    value = """
                [
                  {
                    "userId": 1,
                    "languageCode": "en",
                    "schoolName": "Harvard University",
                    "degree": "Bachelor of Science",
                    "fieldOfStudy": "Computer Science",
                    "startDate": "2015-09-01",
                    "endDate": "2019-06-30"
                  }
                ]
                """
                            )
                    )
            )
            @Valid @RequestParam("educations") String educations,
            @Parameter(
                    description = "School logo image file",
                    required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            @RequestParam("schoolLogoFile") MultipartFile schoolLogoFile) throws IOException {

        // Parse the JSON string into a list of EducationRequestVM
        List<EducationRequestVM> educationRequests = objectMapper.readValue(educations, new TypeReference<>() {});

        // Call the service to create education entries
        List<Education> createdEducations = educationService.createEducation(educationRequests, schoolLogoFile);

        // Map the created entities to response VMs
        List<EducationResponseVM> response = createdEducations.stream()
                .map(educationMapper::toVM)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }


}