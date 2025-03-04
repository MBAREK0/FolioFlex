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
public class ContactRequestVM {
    @NotNull(message = "User ID cannot be null")
    private Long userId;

    private UUID contactId;

    @NotBlank(message = "Language code is required")
    @Size(min = 2, max = 5, message = "Language code must be between 2 and 5 characters")
    private String languageCode;

    @NotBlank(message = "Contact name is required")
    @Size(max = 255, message = "Contact name cannot exceed 255 characters")
    private String contactName;

    @NotBlank(message = "Contact type is required")
    @Size(max = 255, message = "Contact type cannot exceed 255 characters")
    private String contactType;

    @NotBlank(message = "Icon path is required")
    @Size(max = 255, message = "Icon path cannot exceed 255 characters")
    private String contactValue;




}
