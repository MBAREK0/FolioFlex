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
public class CertificationRequestVM {
    @NotNull(message = "User ID cannot be null")
    private Long userId;

    private UUID certificationId;

    @NotBlank(message = "Language code is required")
    @Size(min = 2, max = 5, message = "Language code must be between 2 and 5 characters")
    private String languageCode;

    @NotBlank(message = "Certification name is required")
    @Size(max = 255, message = "Certification name cannot exceed 255 characters")
    private String certificationName;

    @NotBlank(message = "Issuing organization is required")
    @Size(max = 255, message = "Issuing organization cannot exceed 255 characters")
    private String issuingOrganization;

    @NotNull(message = "Issue date is required")
    private LocalDate issueDate;

    private LocalDate expirationDate;

    private String[] skills;

}