package org.mbarek0.folioflex.web.vm.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioTranslationLanguageVM {

    private String language;
    private String code;
    private boolean isPrimary;
}
