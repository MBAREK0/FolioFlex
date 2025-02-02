package org.mbarek0.folioflex.web.vm.response.portfolio_components;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkExperienceResponseVM {

    private Long id;
    private UUID experienceId;
    private Long userId;
    private String languageCode;
    private String jobTitle;
    private String companyName;
    private String companyLogo;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}