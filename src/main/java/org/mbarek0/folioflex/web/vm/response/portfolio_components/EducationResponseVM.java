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
public class EducationResponseVM {
    private Long id;
    private UUID educationId;
    private Long userId;
    private String languageCode;
    private String schoolName;
    private String schoolLogo;
    private String degree;
    private String fieldOfStudy;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}