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
public class CertificationResponseVM {
    private Long id;
    private UUID certificationId;
    private Long userId;
    private String languageCode;
    private String certificationName;
    private String issuingOrganization;
    private String certificationImage;
    private LocalDate issueDate;
    private LocalDate expirationDate;
    private String[] skills;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
