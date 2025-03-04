package org.mbarek0.folioflex.web.vm.response.portfolio_components;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.mbarek0.folioflex.model.enums.IconType;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactResponseVM {
    private Long id;
    private UUID contactId;
    private Long userId;
    private String languageCode;
    private String contactName;
    private String contactType;
    private String contactValue;
    private String iconPath;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
