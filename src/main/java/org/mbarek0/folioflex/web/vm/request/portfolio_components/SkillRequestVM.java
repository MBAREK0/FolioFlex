package org.mbarek0.folioflex.web.vm.request.portfolio_components;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.mbarek0.folioflex.model.enums.IconType;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillRequestVM {

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    private UUID skillId;

    @NotBlank(message = "Language code is required")
    @Size(min = 2, max = 5, message = "Language code must be between 2 and 5 characters")
    private String languageCode;

    @NotBlank(message = "Skill name is required")
    @Size(max = 255, message = "Skill name cannot exceed 255 characters")
    private String skillName;

    @NotNull(message = "Icon type is required")
    private IconType iconType;

    @NotBlank(message = "Icon value is required")
    @Size(max = 255, message = "Icon value cannot exceed 255 characters")
    private String iconValue;
}
