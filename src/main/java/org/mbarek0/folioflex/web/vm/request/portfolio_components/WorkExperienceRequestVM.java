package org.mbarek0.folioflex.web.vm.request.portfolio_components;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkExperienceRequestVM {

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    private UUID experienceId;

    @NotBlank(message = "Language code is required")
    @Size(min = 2, max = 5, message = "Language code must be between 2 and 5 characters")
    private String languageCode;

    @NotBlank(message = "Job title is required")
    @Size(max = 255, message = "Job title cannot exceed 255 characters")
    private String jobTitle;

    @NotBlank(message = "Company name is required")
    @Size(max = 255, message = "Company name cannot exceed 255 characters")
    private String companyName;

    @NotBlank(message = "Location is required")
    @Size(max = 255, message = "Location cannot exceed 255 characters")
    private String location;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    private String[] skills;


}