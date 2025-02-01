package org.mbarek0.folioflex.web.vm.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkExperienceVM {

    private Long id;
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