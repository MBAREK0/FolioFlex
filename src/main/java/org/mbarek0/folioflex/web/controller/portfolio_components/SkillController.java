package org.mbarek0.folioflex.web.controller.portfolio_components;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
import org.mbarek0.folioflex.model.portfolio_components.Skill;
import org.mbarek0.folioflex.service.portfolio_components.SkillService;
import org.mbarek0.folioflex.web.vm.mapper.SkillMapper;
import org.mbarek0.folioflex.web.vm.request.portfolio_components.SkillRequestVM;
import org.mbarek0.folioflex.web.vm.request.portfolio_components.ReorderRequest;
import org.mbarek0.folioflex.web.vm.response.portfolio_components.SkillResponseVM;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/portfolio/skills")
@Tag(name = "Skills", description = "APIs for managing skills")
public class SkillController {

    private final SkillService skillService;
    private final SkillMapper skillMapper;

    @PostMapping
    @Operation(
            summary = "Create skill entries",
            description = "Creates multiple skill entries with different language translations",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Skills created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = SkillResponseVM.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data or validation errors"
                    )
            }
    )
    public ResponseEntity<List<SkillResponseVM>> createSkill(
            @Parameter(
                    description = "List of skill objects",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = SkillRequestVM.class),
                            examples = @ExampleObject(
                                    value = """
                [
                  {
                    "userId": 1,
                    "languageCode": "en",
                    "skillName": "Java",
                    "iconType": "FONTAWESOME",
                    "iconValue": "fa-java"
                  }
                ]
                """
                            )
                    )
            )
            @Valid @RequestBody List<SkillRequestVM> skills) {

        List<Skill> createdSkills = skillService.createSkill(skills);

        List<SkillResponseVM> response = createdSkills.stream()
                .map(skillMapper::toVM)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{username}/primary")
    @Operation(
            summary = "Get all skills by username and primary language",
            description = "Retrieves all skills for a user in their primary language",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Skills retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<List<SkillResponseVM>> getAllSkillsByUserAndPrimaryLanguage(
            @Parameter(description = "Username of the user", required = true)
            @PathVariable String username) {

        List<Skill> skills = skillService.getAllSkills(username, (String) null);

        List<SkillResponseVM> response = skills.stream()
                .map(skillMapper::toVM)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{username}/language/{languageCode}")
    @Operation(
            summary = "Get all skills by username and language",
            description = "Retrieves all skills for a user in a specific language",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Skills retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<List<SkillResponseVM>> getAllSkillsByUserAndLanguage(
            @Parameter(description = "Username of the user", required = true)
            @PathVariable String username,
            @Parameter(description = "Code of the language", required = true)
            @PathVariable String languageCode) {

        List<Skill> skills = skillService.getAllSkills(username, languageCode);

        List<SkillResponseVM> response = skills.stream()
                .map(skillMapper::toVM)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{username}/skill/{uuid}")
    @Operation(
            summary = "Get all skills by username and skill id",
            description = "Retrieves all skill translations for a user by skill id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Skills retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<List<SkillResponseVM>> getSkillsByUserAndSkillId(
            @Parameter(description = "Username of the user", required = true)
            @PathVariable String username,
            @Parameter(description = "Skill ID", required = true)
            @PathVariable UUID uuid) {

        List<Skill> skills = skillService.getAllSkills(username, uuid);

        List<SkillResponseVM> response = skills.stream()
                .map(skillMapper::toVM)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{username}/skill/{uuid}/language/primary")
    @Operation(
            summary = "Get skill by username, skill id and primary language",
            description = "Retrieves a skill for a user by skill id and primary language",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Skill retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Skill not found")
            }
    )
    public ResponseEntity<SkillResponseVM> getSkillByUserAndSkillIdAndPrimaryLanguage(
            @Parameter(description = "Username of the user", required = true)
            @PathVariable String username,
            @Parameter(description = "Skill ID", required = true)
            @PathVariable UUID uuid) {

        Skill skill = skillService.getSkill(username, uuid, null);
        SkillResponseVM response = skillMapper.toVM(skill);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{username}/skill/{uuid}/language/{languageCode}")
    @Operation(
            summary = "Get skill by username, skill id and language",
            description = "Retrieves a skill for a user by skill id and language",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Skill retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Skill not found")
            }
    )
    public ResponseEntity<SkillResponseVM> getSkillByUserAndSkillIdAndLanguage(
            @Parameter(description = "Username of the user", required = true)
            @PathVariable String username,
            @Parameter(description = "Skill ID", required = true)
            @PathVariable UUID uuid,
            @Parameter(description = "Language Code", required = true)
            @PathVariable String languageCode) {

        Skill skill = skillService.getSkill(username, uuid, languageCode);
        SkillResponseVM response = skillMapper.toVM(skill);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{uuid}")
    @Operation(
            summary = "Update skill entries",
            description = "Updates multiple skill translations sharing the same skill ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Skills updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = SkillResponseVM.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data or validation errors"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Skill not found"
                    )
            }
    )
    public ResponseEntity<List<SkillResponseVM>> updateSkill(
            @Parameter(
                    description = "Unique skill identifier (UUID)",
                    example = "550e8400-e29b-41d4-a716-446655440000",
                    required = true
            )
            @PathVariable UUID uuid,

            @Parameter(
                    description = "JSON array of skill updates",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = SkillRequestVM.class),
                            examples = @ExampleObject(
                                    value = """
                [
                  {
                    "skillId": "550e8400-e29b-41d4-a716-446655440000",
                    "userId": 1,
                    "languageCode": "en",
                    "skillName": "Java Development",
                    "iconType": "FONTAWESOME",
                    "iconValue": "fa-brands fa-java"
                  }
                ]
                """
                            )
                    )
            )
            @Valid @RequestBody List<SkillRequestVM> skills) {

        List<Skill> updatedSkills = skillService.updateSkill(uuid, skills);

        List<SkillResponseVM> response = updatedSkills.stream()
                .map(skillMapper::toVM)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/reorder")
    @Operation(
            summary = "Reorder skills",
            description = "Update the display order of skills based on drag-and-drop",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "404", description = "Skill not found")
            }
    )
    public ResponseEntity<List<SkillResponseVM>> reorderSkills(
            @Parameter(description = "List of skill IDs with new display orders", required = true)
            @Valid @RequestBody List<ReorderRequest> reorderRequests) {

        List<Skill> updatedSkills = skillService.reorder(reorderRequests);
        List<SkillResponseVM> response = updatedSkills.stream()
                .map(skillMapper::toVM)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{uuid}")
    @Operation(
            summary = "Delete skill",
            description = "Delete a skill by skill id (marks as deleted)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Skill deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Skill not found")
            }
    )
    public ResponseEntity<List<SkillResponseVM>> deleteSkill(
            @Parameter(description = "Skill ID", required = true)
            @PathVariable UUID uuid) {

        List<Skill> updatedSkills = skillService.deleteSkill(uuid);

        List<SkillResponseVM> response = updatedSkills.stream()
                .map(skillMapper::toVM)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{uuid}/archive")
    @Operation(
            summary = "Archive skill",
            description = "Archive a skill by skill id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Skill archived successfully"),
                    @ApiResponse(responseCode = "404", description = "Skill not found")
            }
    )
    public ResponseEntity<List<SkillResponseVM>> archiveSkill(
            @Parameter(description = "Skill ID", required = true)
            @PathVariable UUID uuid) {

        List<Skill> updatedSkills = skillService.archiveSkill(uuid);

        List<SkillResponseVM> response = updatedSkills.stream()
                .map(skillMapper::toVM)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}