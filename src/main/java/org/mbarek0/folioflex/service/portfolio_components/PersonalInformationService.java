package org.mbarek0.folioflex.service.portfolio_components;

import org.mbarek0.folioflex.model.portfolio_components.PersonalInformation;
import org.mbarek0.folioflex.web.vm.request.portfolio_components.PersonalInformationRequestVM;

import java.util.List;

public interface PersonalInformationService {
    PersonalInformation createPersonalInformation(PersonalInformationRequestVM request);

    boolean hasMissingTranslations(Long userId);

    List<String> getMissingLanguages(Long userId);

    PersonalInformation getPersonalInformation(String username, String languageCode);

    List<PersonalInformation> getAllPersonalInformation(String username);

    PersonalInformation updatePersonalInformation(Long id, PersonalInformationRequestVM request);

}
