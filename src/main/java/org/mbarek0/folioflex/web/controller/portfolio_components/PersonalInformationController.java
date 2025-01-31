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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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
    public ResponseEntity<PersonalInformationVM> createPersonalInformation(
            @Valid @ModelAttribute CreatePersonalInformationVM request) throws IOException {

        // Create personal information
        PersonalInformation personalInformation = personalInformationService.createPersonalInformation(request);
        return ResponseEntity.ok(personalInformationMapper.toVM(personalInformation));
    }



}