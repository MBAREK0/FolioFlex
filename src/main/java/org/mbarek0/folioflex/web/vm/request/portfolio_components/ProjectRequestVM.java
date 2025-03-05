package org.mbarek0.folioflex.web.vm.request.portfolio_components;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.mbarek0.folioflex.model.enums.ProjectStatus;
import org.mbarek0.folioflex.model.portfolio_components.project.Project;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequestVM {
    @NotNull(message = "User ID cannot be null")
    private Long userId;

    private UUID projectId;

    @NotBlank(message = "Language code is required")
    @Size(min = 2, max = 5, message = "Language code must be between 2 and 5 characters")
    private String languageCode;

    @NotBlank(message = "Project name is required")
    @Size(max = 255, message = "Project name cannot exceed 255 characters")
    private String projectName;

    private String description;

    private String demoLink;

    @Size(max = 255, message = "Slug cannot exceed 255 characters")
    private String slug;

    private Integer orderDisplay;

    @Size(max = 50, message = "Status cannot exceed 50 characters")
    private ProjectStatus status;

    private LocalDate startDate;

    private LocalDate endDate;

    private String[] skills;

    private String[] tags;
}