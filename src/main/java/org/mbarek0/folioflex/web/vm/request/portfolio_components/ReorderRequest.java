package org.mbarek0.folioflex.web.vm.request.portfolio_components;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReorderRequest {
    @NotNull(message = "Experience ID cannot be null")
    private UUID componentId;

    @NotNull(message = "Language code cannot be null")
    private String languageCode;

    @NotNull(message = "Display order cannot be null")
    @Min(value = 0, message = "Display order cannot be negative")
    private Integer displayOrder;
}
