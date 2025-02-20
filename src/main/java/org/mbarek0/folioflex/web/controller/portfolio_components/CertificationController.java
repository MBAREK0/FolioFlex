package org.mbarek0.folioflex.web.controller.portfolio_components;

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
import org.mbarek0.folioflex.model.portfolio_components.Certification;
import org.mbarek0.folioflex.service.portfolio_components.CertificationService;
import org.mbarek0.folioflex.web.vm.mapper.CertificationMapper;
import org.mbarek0.folioflex.web.vm.request.portfolio_components.ReorderRequest;
import org.mbarek0.folioflex.web.vm.request.portfolio_components.CertificationRequestVM;
import org.mbarek0.folioflex.web.vm.request.portfolio_components.WorkExperienceRequestVM;
import org.mbarek0.folioflex.web.vm.response.portfolio_components.CertificationResponseVM;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/portfolio/certification")
@Tag(name = "Certification", description = "APIs for managing certifications")
public class CertificationController {

    private final CertificationService certificationService;
    private final CertificationMapper certificationMapper;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Create certification entries",
            description = "Creates multiple certification entries with a certification image file upload",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Certifications created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = CertificationResponseVM.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data or validation errors"
                    )
            }
    )
    public ResponseEntity<List<CertificationResponseVM>> createCertification(
            @Parameter(
                    description = "JSON array of certification entries",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = CertificationRequestVM.class),
                            examples = @ExampleObject(
                                    value = """
                                                [
                                                  {
                                                    "userId": 1,
                                                    "languageCode": "en",
                                                    "jobTitle": "Software Engineer",
                                                    "companyName": "Tech Corp",
                                                    "location": "New York",
                                                    "startDate": "2020-01-01",
                                                    "endDate": "2022-12-31",
                                                    "description": "Worked on core platform development" 
                                                  }
                                                ]
                                            """
                            )
                    )
            )
            @Valid @RequestParam("certifications") String certifications,
            @Parameter(description = "Certification image file", required = true)
            @RequestParam("certificationImageFile") MultipartFile certificationImageFile) throws IOException {

        List<CertificationRequestVM> certificationsVM = objectMapper.readValue(
                certifications,
                new TypeReference<>() {});

        List<Certification> createdCertifications = certificationService.createCertification(certificationsVM, certificationImageFile);

        List<CertificationResponseVM> response = createdCertifications.stream()
                .map(certificationMapper::toVM)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{username}/primary")
    @Operation(
            summary = "Get all certifications by username and primary language",
            description = "Retrieves all certifications for a user in their primary language"
    )
    public ResponseEntity<List<CertificationResponseVM>> getAllCertificationsByUserAndPrimaryLanguage(
            @PathVariable String username) {
        List<Certification> certifications = certificationService.getAllCertifications(username, (String) null);
        List<CertificationResponseVM> response = certifications.stream()
                .map(certificationMapper::toVM)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{username}/language/{languageCode}")
    @Operation(
            summary = "Get all certifications by username and language",
            description = "Retrieves all certifications for a user in a specific language"
    )
    public ResponseEntity<List<CertificationResponseVM>> getAllCertificationsByUserAndLanguage(
            @PathVariable String username,
            @PathVariable String languageCode) {
        List<Certification> certifications = certificationService.getAllCertifications(username, languageCode);
        List<CertificationResponseVM> response = certifications.stream()
                .map(certificationMapper::toVM)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{username}/certification/{uuid}")
    @Operation(
            summary = "Get all certifications by username and certification id",
            description = "Retrieves all certifications for a user by certification id"
    )
    public ResponseEntity<List<CertificationResponseVM>> getCertificationsByUserAndCertificationId(
            @PathVariable String username,
            @PathVariable UUID uuid) {
        List<Certification> certifications = certificationService.getAllCertifications(username, uuid);
        List<CertificationResponseVM> response = certifications.stream()
                .map(certificationMapper::toVM)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{username}/certification/{uuid}/language/primary")
    @Operation(
            summary = "Get certification by username, certification id, and primary language",
            description = "Retrieves a certification for a user by certification id in their primary language"
    )
    public ResponseEntity<CertificationResponseVM> getCertificationByUserAndCertificationIdAndPrimaryLanguage(
            @PathVariable String username,
            @PathVariable UUID uuid) {
        Certification certification = certificationService.getCertification(username, uuid, (String) null);
        return ResponseEntity.ok(certificationMapper.toVM(certification));
    }

    @GetMapping("/user/{username}/certification/{uuid}/language/{languageCode}")
    @Operation(
            summary = "Get certification by username, certification id, and language",
            description = "Retrieves a certification for a user by certification id in a specific language"
    )
    public ResponseEntity<CertificationResponseVM> getCertificationByUserAndCertificationIdAndLanguage(
            @PathVariable String username,
            @PathVariable UUID uuid,
            @PathVariable String languageCode) {
        Certification certification = certificationService.getCertification(username, uuid, languageCode);
        return ResponseEntity.ok(certificationMapper.toVM(certification));
    }

    @PutMapping("/{uuid}")
    @Operation(
            summary = "Update certification entries",
            description = "Updates multiple certification translations sharing the same certification ID"
    )
    public ResponseEntity<List<CertificationResponseVM>> updateCertification(
            @PathVariable UUID uuid,
            @Valid @RequestParam("certifications") String certifications,
            @RequestParam(value = "certificationImageFile", required = false) MultipartFile certificationImageFile) throws IOException {

        List<CertificationRequestVM> certificationsVM = objectMapper.readValue(
                certifications,
                new TypeReference<>() {});

        List<Certification> updatedCertifications = certificationService.updateCertification(uuid, certificationsVM, certificationImageFile);

        List<CertificationResponseVM> response = updatedCertifications.stream()
                .map(certificationMapper::toVM)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/reorder")
    @Operation(
            summary = "Reorder certifications",
            description = "Update the display order of certifications"
    )
    public ResponseEntity<List<CertificationResponseVM>> reorderCertifications(
            @Valid @RequestBody List<ReorderRequest> reorderRequests) {
        List<Certification> updatedCertifications = certificationService.reorder(reorderRequests);
        List<CertificationResponseVM> response = updatedCertifications.stream()
                .map(certificationMapper::toVM)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{uuid}")
    @Operation(
            summary = "Delete certification",
            description = "Delete a certification by certification id"
    )
    public ResponseEntity<List<CertificationResponseVM>> deleteCertification(
            @PathVariable UUID uuid) {
        List<Certification> updatedCertification = certificationService.deleteCertification(uuid);
        List<CertificationResponseVM> response = updatedCertification.stream()
                .map(certificationMapper::toVM)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{uuid}/archive")
    @Operation(
            summary = "Archive certification",
            description = "Archive a certification by certification id"
    )
    public ResponseEntity<List<CertificationResponseVM>> archiveCertification(
            @PathVariable UUID uuid) {
        List<Certification> updatedCertification = certificationService.archiveCertification(uuid);
        List<CertificationResponseVM> response = updatedCertification.stream()
                .map(certificationMapper::toVM)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}