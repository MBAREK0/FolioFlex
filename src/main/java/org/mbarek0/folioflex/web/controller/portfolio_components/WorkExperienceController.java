package org.mbarek0.folioflex.web.controller.portfolio_components;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.mbarek0.folioflex.model.portfolio_components.WorkExperience;
import org.mbarek0.folioflex.service.portfolio_components.WorkExperienceService;
import org.mbarek0.folioflex.web.vm.mapper.WorkExperienceMapper;
import org.mbarek0.folioflex.web.vm.request.portfolio_components.WorkExperienceRequestVM;
import org.mbarek0.folioflex.web.vm.response.portfolio_components.WorkExperienceResponseVM;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/portfolio/work-experience")
@Tag(name = "Work Experience", description = "APIs for managing work experience")
public class WorkExperienceController {

    private final WorkExperienceService workExperienceService;
    private final WorkExperienceMapper workExperienceMapper;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<WorkExperienceResponseVM>> createWorkExperience(
            @Valid @RequestParam("workExperiences") String workExperiences,
            @RequestParam("companyLogoFile") MultipartFile companyLogoFile) throws IOException {

        // Parse the JSON string into a list of CreateWorkExperienceVM
        List<WorkExperienceRequestVM> workExperiencesVM = objectMapper.readValue(
                workExperiences,
                new TypeReference<List<WorkExperienceRequestVM>>() {});

        List<WorkExperience> createdExperiences = workExperienceService.createWorkExperience(workExperiencesVM, companyLogoFile);

        List<WorkExperienceResponseVM> response = createdExperiences.stream()
                .map(workExperienceMapper::toVM)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{username}/primary")
    @Operation(
            summary = "Get all work experiences by username and primary language",
            description = "Retrieves all work experiences for a user in their primary language",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Work experiences retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<List<WorkExperienceResponseVM>> getAllWorkExperiencesByUserAndPrimaryLanguage(
            @Parameter(description = "Username of the user", required = true)
            @PathVariable String username) {

        List<WorkExperience> workExperiences = workExperienceService.getAllWorkExperiences(username, (String) null);

        List<WorkExperienceResponseVM> response = workExperiences.stream()
                .map(workExperienceMapper::toVM)
                .collect(Collectors.toList());


        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{username}/language/{languageCode}")
    @Operation(
            summary = "Get all work experiences by username and language",
            description = "Retrieves all work experiences for a user in a specific language",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Work experiences retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<List<WorkExperienceResponseVM>> getAllWorkExperiencesByUserAndLanguage(
            @Parameter(description = "Username of the user", required = true)
            @PathVariable String username,
            @Parameter(description = "code of the language", required = true)
            @PathVariable String languageCode) {

        List<WorkExperience> workExperiences = workExperienceService.getAllWorkExperiences(username, languageCode);

        List<WorkExperienceResponseVM> response = workExperiences.stream()
                .map(workExperienceMapper::toVM)
                .collect(Collectors.toList());


        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{username}/experience/{uuid}")
    @Operation(
            summary = "Get all work experiences by username and experience id",
            description = "Retrieves all work experiences for a user by experience id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Work experiences retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<List<WorkExperienceResponseVM>> getWorkExperiencesByUserAndExperienceId(
            @Parameter(description = "Username of the user", required = true)
            @PathVariable String username,
            @Parameter(description = "Experience ID", required = true)
            @PathVariable UUID uuid) {

        List<WorkExperience> workExperiences = workExperienceService.getAllWorkExperiences(username, uuid);

        List<WorkExperienceResponseVM> response = workExperiences.stream()
                .map(workExperienceMapper::toVM)
                .collect(Collectors.toList());


        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{username}/experience/{uuid}/language/primary")
    @Operation(
            summary = "Get all work experience by username and experience id and primary language",
            description = "Retrieves all work experience for a user by experience id and primary language",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Work experience retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<WorkExperienceResponseVM> getWorkExperiencesByUserAndExperienceIdAndPrimaryLanguage(
            @Parameter(description = "Username of the user", required = true)
            @PathVariable String username,
            @Parameter(description = "Experience ID", required = true)
            @PathVariable UUID uuid) {

        WorkExperience workExperiences = workExperienceService.getAWorkExperiencs(username, uuid, (String) null);
        WorkExperienceResponseVM response = workExperienceMapper.toVM(workExperiences);


        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{username}/experience/{uuid}/language/{languageCode}")
    @Operation(
            summary = "Get all work experience by username and experience id and language",
            description = "Retrieves all work experience for a user by experience id and language",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Work experience retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<WorkExperienceResponseVM> getWorkExperiencesByUserAndExperienceIdAndLanguage(
            @Parameter(description = "Username of the user", required = true)
            @PathVariable String username,
            @Parameter(description = "Experience ID", required = true)
            @PathVariable UUID uuid,
            @Parameter(description = "Language Code", required = true)
            @PathVariable String languageCode) {

        WorkExperience workExperiences = workExperienceService.getAWorkExperiencs(username, uuid,languageCode);
        WorkExperienceResponseVM response = workExperienceMapper.toVM(workExperiences);


        return ResponseEntity.ok(response);
    }

    @PutMapping("/experience/{uuid}")
    @Operation(
            summary = "Update work experience",
            description = "Updates a work experience by experience id should send list of CreateWorkExperienceVM with the same experience id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Work experience updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Work experience not found")
            }
    )
    public ResponseEntity<List<WorkExperienceResponseVM>> updateWorkExperience(
            @Parameter(description = "Experience ID", required = true)
            @PathVariable UUID uuid,
            @Valid @RequestParam("workExperiences") String workExperiences,
            @RequestParam(value = "companyLogoFile" ,required = false) MultipartFile companyLogoFile) throws IOException {

        List<WorkExperienceRequestVM> workExperiencesVM = objectMapper.readValue(
                workExperiences,
                new TypeReference<List<WorkExperienceRequestVM>>() {});

        List<WorkExperience> updatedExperience = workExperienceService.updateWorkExperience(uuid, workExperiencesVM, companyLogoFile);

        List<WorkExperienceResponseVM> response = updatedExperience.stream()
                .map(workExperienceMapper::toVM)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}