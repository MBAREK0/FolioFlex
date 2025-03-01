package org.mbarek0.folioflex.web.vm.response.portfolio_components;


import lombok.*;
import org.mbarek0.folioflex.model.enums.IconType;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillResponseVM {

    private Long id;
    private UUID skillId;
    private Long userId;
    private String languageCode;
    private String skillName;
    private IconType iconType;
    private String iconValue;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}