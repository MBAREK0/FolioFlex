package org.mbarek0.folioflex.web.controller.portfolio_components;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.mbarek0.folioflex.service.portfolio_components.ProjectService;
import org.mbarek0.folioflex.web.vm.mapper.ProjectMapper;
import org.mbarek0.folioflex.web.vm.request.portfolio_components.ProjectRequestVM;
import org.mbarek0.folioflex.web.vm.response.portfolio_components.ProjectResponseVM;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/portfolio/projects")
@Tag(name = "Projects", description = "APIs for managing projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectMapper projectMapper;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Create project entries",
            description = "Creates multiple project entries with a project logo file upload and project screenshots file uploads",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Project entries created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ProjectResponseVM.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data or validation errors"
                    )
            }
    )
    public ResponseEntity<List<ProjectResponseVM>> createProject(
            @RequestParam("projects") String projects,
            @RequestParam("projectLogoFile") MultipartFile projectLogoFile,
            @RequestParam("projectScreenshotFiles") List<MultipartFile> projectScreenshotFiles
    ) throws IOException {

        List<ProjectRequestVM> projectRequests = objectMapper.readValue(projects, new TypeReference<>() {});

        var createdProjects = projectService.createProjects(projectRequests, projectLogoFile, projectScreenshotFiles);

        List<ProjectResponseVM> response = createdProjects.stream()
                .map(projectMapper::toVM)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
