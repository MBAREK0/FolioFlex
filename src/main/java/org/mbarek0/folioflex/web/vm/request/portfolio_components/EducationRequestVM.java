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
public class EducationRequestVM {
    @NotNull(message = "User ID cannot be null")
    private Long userId;

    private UUID educationId;

    @NotBlank(message = "Language code is required")
    @Size(min = 2, max = 5, message = "Language code must be between 2 and 5 characters")
    private String languageCode;

    @NotBlank(message = "School name is required")
    @Size(max = 255, message = "School name cannot exceed 255 characters")
    private String schoolName;

    @NotBlank(message = "Degree is required")
    @Size(max = 255, message = "Degree cannot exceed 255 characters")
    private String degree;

    @NotBlank(message = "Field of study is required")
    @Size(max = 255, message = "Field of study cannot exceed 255 characters")
    private String fieldOfStudy;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    private String[] skills;

}