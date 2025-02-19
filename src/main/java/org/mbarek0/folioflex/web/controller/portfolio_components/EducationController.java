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
import org.mbarek0.folioflex.web.vm.request.portfolio_components.ReorderRequest;
import org.mbarek0.folioflex.web.vm.response.portfolio_components.EducationResponseVM;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
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


    @GetMapping("/user/{username}/primary")
    @Operation(
            summary = "Get all education entries by username and primary language",
            description = "Retrieves all education entries for a user in their primary language",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Education entries retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<List<EducationResponseVM>> getAllEducationByUserAndPrimaryLanguage(
            @Parameter(description = "Username of the user", required = true)
            @PathVariable String username) {
        List<Education> educations = educationService.getAllEducation(username,(String) null);
        return ResponseEntity.ok(educations.stream().map(educationMapper::toVM).collect(Collectors.toList()));
    }


    @GetMapping("/user/{username}/language/{languageCode}")
    @Operation(
            summary = "Get all education entries by username and language",
            description = "Retrieves all education entries for a user in a specific language",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Education entries retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<List<EducationResponseVM>> getAllEducationByUserAndLanguage(
            @Parameter(description = "Username of the user", required = true)
            @PathVariable String username,
            @Parameter(description = "Language code", required = true)
            @PathVariable String languageCode) {
        List<Education> educations = educationService.getAllEducation(username, languageCode);
        return ResponseEntity.ok(educations.stream().map(educationMapper::toVM).collect(Collectors.toList()));
    }

    @GetMapping("/user/{username}/education/{uuid}")
    @Operation(
            summary = "Get all education entries by username and education ID",
            description = "Retrieves all language versions of an education entry",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Education entries retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Education entry not found")
            }
    )
    public ResponseEntity<List<EducationResponseVM>> getEducationByUserAndEducationId(
            @Parameter(description = "Username of the user", required = true)
            @PathVariable String username,
            @Parameter(description = "Education ID", required = true)
            @PathVariable UUID uuid) {
        List<Education> educations = educationService.getAllEducation(username, uuid);
        return ResponseEntity.ok(educations.stream().map(educationMapper::toVM).collect(Collectors.toList()));
    }


    @GetMapping("/user/{username}/education/{uuid}/language/primary")
    @Operation(
            summary = "Get education entry by username, education ID, and primary language",
            description = "Retrieves the primary language version of an education entry",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Education entry retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Education entry not found")
            }
    )
    public ResponseEntity<EducationResponseVM> getEducationByUserAndEducationIdAndPrimaryLanguage(
            @Parameter(description = "Username of the user", required = true)
            @PathVariable String username,
            @Parameter(description = "Education ID", required = true)
            @PathVariable UUID uuid) {
        Education education = educationService.getEducation(username, uuid,null);
        return ResponseEntity.ok(educationMapper.toVM(education));
    }

    @GetMapping("/user/{username}/education/{uuid}/language/{languageCode}")
    @Operation(
            summary = "Get education entry by username, education ID, and language",
            description = "Retrieves a specific language version of an education entry",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Education entry retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Education entry not found")
            }
    )
    public ResponseEntity<EducationResponseVM> getEducationByUserAndEducationIdAndLanguage(
            @Parameter(description = "Username of the user", required = true)
            @PathVariable String username,
            @Parameter(description = "Education ID", required = true)
            @PathVariable UUID uuid,
            @Parameter(description = "Language code", required = true)
            @PathVariable String languageCode) {
        Education education = educationService.getEducation(username, uuid, languageCode);
        return ResponseEntity.ok(educationMapper.toVM(education));
    }



    @PutMapping("/education/{uuid}")
    @Operation(
            summary = "Update education entries",
            description = "Updates multiple education translations sharing the same education ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Education entries updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Education entry not found")
            }
    )
    public ResponseEntity<List<EducationResponseVM>> updateEducation(
            @Parameter(description = "Education ID", required = true)
            @PathVariable UUID uuid,
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
                            "educationId": "642e3bba-7c3d-4bba-99e6-f18b3252b1f4",
                            "userId": 1,
                            "languageCode": "en",
                            "schoolName": "Harvard University",
                            "degree": "Bachelor of Science",
                            "fieldOfStudy": "Computer Science",
                            "startDate": "2015-09-01",
                            "endDate": "2019-06-30"    \s
                        }
                    ]
                """
                            )
                    )
            )
            @Valid @RequestParam("educations") String educations,
            @Parameter(description = "New school logo file (optional)")
            @RequestParam(value = "schoolLogoFile", required = false)  MultipartFile schoolLogoFile) throws IOException {

        List<EducationRequestVM> educationRequests = objectMapper.readValue(educations, new TypeReference<>() {});
        List<Education> updatedEducations = educationService.updateEducation(uuid, educationRequests, schoolLogoFile);
        return ResponseEntity.ok(updatedEducations.stream().map(educationMapper::toVM).collect(Collectors.toList()));
    }

    @PutMapping("/reorder")
    @Operation(
            summary = "Reorder education entries",
            description = "Update the display order of education entries",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    public ResponseEntity<List<EducationResponseVM>> reorderEducation(
            @Parameter(description = "Reorder requests", required = true)
            @Valid @RequestBody List<ReorderRequest> reorderRequests) {
        List<Education> reorderedEducations = educationService.reorder(reorderRequests);
        return ResponseEntity.ok(reorderedEducations.stream().map(educationMapper::toVM).collect(Collectors.toList()));
    }

    @DeleteMapping("/education/{uuid}")
    @Operation(
            summary = "Delete education entry",
            description = "Marks an education entry as deleted",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Education entry deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Education entry not found")
            }
    )
    public ResponseEntity<List<EducationResponseVM>> deleteEducation(
            @Parameter(description = "Education ID", required = true)
            @PathVariable UUID uuid) {
        List<Education> deletedEducations = educationService.deleteEducation(uuid);
        return ResponseEntity.ok(deletedEducations.stream().map(educationMapper::toVM).collect(Collectors.toList()));
    }

    @PutMapping("/education/{uuid}/archive")
    @Operation(
            summary = "Archive education entry",
            description = "Marks an education entry as archived",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Education entry archived successfully"),
                    @ApiResponse(responseCode = "404", description = "Education entry not found")
            }
    )
    public ResponseEntity<List<EducationResponseVM>> archiveEducation(
            @Parameter(description = "Education ID", required = true)
            @PathVariable UUID uuid) {
        List<Education> archivedEducations = educationService.archiveEducation(uuid);
        return ResponseEntity.ok(archivedEducations.stream().map(educationMapper::toVM).collect(Collectors.toList()));
    }

}