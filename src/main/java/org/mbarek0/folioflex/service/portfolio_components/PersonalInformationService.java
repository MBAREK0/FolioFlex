package org.mbarek0.folioflex.service.portfolio_components;

import jakarta.validation.Valid;
import org.mbarek0.folioflex.model.portfolio_components.PersonalInformation;
import org.mbarek0.folioflex.web.vm.request.CreatePersonalInformationVM;

public interface PersonalInformationService {
    PersonalInformation createPersonalInformation(CreatePersonalInformationVM request);
}
