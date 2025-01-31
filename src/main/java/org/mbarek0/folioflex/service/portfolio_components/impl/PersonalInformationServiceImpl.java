package org.mbarek0.folioflex.service.portfolio_components.impl;

import lombok.AllArgsConstructor;
import org.mbarek0.folioflex.model.Language;
import org.mbarek0.folioflex.model.User;
import org.mbarek0.folioflex.model.portfolio_components.PersonalInformation;
import org.mbarek0.folioflex.repository.PersonalInformationRepository;
import org.mbarek0.folioflex.service.aws.S3Service;
import org.mbarek0.folioflex.service.portfolio_components.PersonalInformationService;
import org.mbarek0.folioflex.service.translation.PortfolioTranslationLanguageService;
import org.mbarek0.folioflex.service.user.UserService;
import org.mbarek0.folioflex.web.exception.portfolioExs.personal_informationExs.PersonalInformationAlreadyExistsException;
import org.mbarek0.folioflex.web.exception.translationExs.UserDontHaveLanguageException;
import org.mbarek0.folioflex.web.vm.request.CreatePersonalInformationVM;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@AllArgsConstructor
@Service
public class PersonalInformationServiceImpl implements PersonalInformationService {

    private final PortfolioTranslationLanguageService portfolioTranslationLanguageService;
    private final UserService userService;
    private final PersonalInformationRepository personalInformationRepository;
    private final S3Service s3Service;


    @Override
    public PersonalInformation createPersonalInformation(CreatePersonalInformationVM request) {
        Language lang = portfolioTranslationLanguageService.getLanguageByCode(request.getLanguageCode());
        User user = userService.findUserById(request.getUserId());

        if (!portfolioTranslationLanguageService.existsByUserAndLanguage(user, lang))
            throw new UserDontHaveLanguageException("User is not allowed to use this language");


        if (personalInformationRepository.findByUserAndLanguage(user,lang).isPresent())
            throw new PersonalInformationAlreadyExistsException("Personal information already exists");

        String profilePhotoUrl;
        String backgroundBannerUrl;

        if (request.getProfilePhoto() == null) profilePhotoUrl = null;
        else profilePhotoUrl = s3Service.uploadFile(request.getProfilePhoto());

        if (request.getBackgroundBanner() == null) backgroundBannerUrl = null;
        else backgroundBannerUrl = s3Service.uploadFile(request.getBackgroundBanner());


        PersonalInformation personalInformation = new PersonalInformation();
        personalInformation.setUser(user);
        personalInformation.setLanguage(lang);
        personalInformation.setFirstName(request.getFirstName());
        personalInformation.setLastName(request.getLastName());
        personalInformation.setBackgroundBanner(profilePhotoUrl);
        personalInformation.setProfilePhoto(backgroundBannerUrl);
        personalInformation.setAbout(request.getAbout());
        personalInformation.setLocation(request.getLocation());
        personalInformation.setHeadline(request.getHeadline());
        personalInformation.setCreatedAt(LocalDateTime.now());
        personalInformation.setUpdatedAt(LocalDateTime.now());
        personalInformation.setArchived(false);
        personalInformation.setDeleted(false);

        return personalInformationRepository.save(personalInformation);





    }
}
