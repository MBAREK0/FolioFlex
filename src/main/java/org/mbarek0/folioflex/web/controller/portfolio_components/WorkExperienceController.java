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
import org.mbarek0.folioflex.web.vm.request.CreateWorkExperienceVM;
import org.mbarek0.folioflex.web.vm.response.WorkExperienceVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/portfolio/work-experience")
@Tag(name = "Work Experience", description = "APIs for managing work experience")
public class WorkExperienceController {

    private final WorkExperienceService workExperienceService;
    private final WorkExperienceMapper workExperienceMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Create work experience",
            description = "Creates work experience for a user in a specific language",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Work experience created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    public ResponseEntity<Map<String, Object>> createWorkExperience(
            @Valid @ModelAttribute CreateWorkExperienceVM request) {

        WorkExperience workExperience = workExperienceService.createWorkExperience(request);

        boolean hasMissingTranslations = workExperienceService.hasMissingTranslations(request.getUserId());

        WorkExperienceVM response = workExperienceMapper.toVM(workExperience);

        List<String> missingLanguages = workExperienceService.getMissingLanguages(request.getUserId());

        // Create a custom response body
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", response);
        responseBody.put("status", hasMissingTranslations ? "INCOMPLETE" : "COMPLETE");
        responseBody.put("message", hasMissingTranslations ?
                "Complete the missing translations for the following languages: " + missingLanguages :
                "Work experience created successfully");

        return ResponseEntity.ok(responseBody);
    }

}