package org.mbarek0.folioflex.web.vm.mapper;

import org.mbarek0.folioflex.model.PortfolioTranslationLanguage;
import org.mbarek0.folioflex.web.vm.response.translation.PortfolioTranslationLanguageResponseVM;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PortfolioTranslationLanguageMapper {


    @Mapping(source = "language.language", target = "language")
    @Mapping(source = "language.code", target = "code")
    @Mapping(source = "primary", target = "isPrimary") // Map "primary" to "isPrimary"
    PortfolioTranslationLanguageResponseVM toVM(PortfolioTranslationLanguage portfolioTranslationLanguage);

    @Mapping(source = "language", target = "language.language")
    @Mapping(source = "code", target = "language.code")
    @Mapping(source = "primary", target = "isPrimary")
    PortfolioTranslationLanguage toEntity(PortfolioTranslationLanguageResponseVM portfolioTranslationLanguageVM);
}