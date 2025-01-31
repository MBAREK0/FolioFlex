package org.mbarek0.folioflex.service.portfolio_components;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.mbarek0.folioflex.model.portfolio_components.PersonalInformation;
import org.mbarek0.folioflex.web.vm.request.CreatePersonalInformationVM;

import java.util.List;

public interface PersonalInformationService {
    PersonalInformation createPersonalInformation(CreatePersonalInformationVM request);

    boolean hasMissingTranslations(Long userId);

    List<String> getMissingLanguages(Long userId);

    PersonalInformation getPersonalInformation(String username, String languageCode);
}
