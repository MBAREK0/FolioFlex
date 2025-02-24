package org.mbarek0.folioflex.web.vm.response.translation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioTranslationLanguageResponseVM {

    private String language;
    private String code;
    private boolean isPrimary;
}
