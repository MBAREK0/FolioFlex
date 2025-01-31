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
import org.mbarek0.folioflex.web.exception.portfolioExs.personal_informationExs.InvalidImageUrlException;
import org.mbarek0.folioflex.web.exception.portfolioExs.personal_informationExs.PersonalInformationAlreadyExistsException;
import org.mbarek0.folioflex.web.exception.portfolioExs.personal_informationExs.PersonalInformationNotFoundException;
import org.mbarek0.folioflex.web.exception.translationExs.UserDontHaveLanguageException;
import org.mbarek0.folioflex.web.exception.userExs.UserNotFoundException;
import org.mbarek0.folioflex.web.vm.request.CreatePersonalInformationVM;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class PersonalInformationServiceImpl implements PersonalInformationService {

    private final PortfolioTranslationLanguageService portfolioTranslationLanguageService;
    private final UserService userService;
    private final PersonalInformationRepository personalInformationRepository;
    private final S3Service s3Service;


    // -------------------------------- Create Personal Information --------------------------------
    @Override
    public PersonalInformation createPersonalInformation(CreatePersonalInformationVM request) {
        Language lang = portfolioTranslationLanguageService.getLanguageByCode(request.getLanguageCode());
        User user = userService.findUserById(request.getUserId());

        if (!portfolioTranslationLanguageService.existsByUserAndLanguage(user, lang))
            throw new UserDontHaveLanguageException("User is not allowed to use this language");


        if (personalInformationRepository.findByUserAndLanguageAndIsDeletedFalseAndIsArchivedFalse(user,lang).isPresent())
            throw new PersonalInformationAlreadyExistsException("Personal information already exists");



        String profilePhotoUrl = resolveImageUrl(
                request.getProfilePhotoFile(),
                request.getProfilePhotoUrl(),
                user
        );

        String backgroundBannerUrl = resolveImageUrl(
                request.getBackgroundBannerFile(),
                request.getBackgroundBannerUrl(),
                user
        );

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

    private String resolveImageUrl(MultipartFile file, String url, User user) {
        if (file != null && !file.isEmpty()) {
            return s3Service.uploadFile(file);
        } else if (url != null && !url.isEmpty() ) {
            validateImageUrlOwnership(url, user);
            return url;
        }
        return null;
    }

    private void validateImageUrlOwnership(String url, User user) {
        boolean isUrlValid = personalInformationRepository.existsByUserAndProfilePhotoUrlOrBackgroundBannerUrl(user, url);
        if (!isUrlValid) {
            throw new InvalidImageUrlException("Image URL does not belong to user");
        }
    }

    @Override
    public boolean hasMissingTranslations(Long userId){
        // Get all languages the user has selected for translations
        List<Language> selectedLanguages = portfolioTranslationLanguageService
                .findLanguagesByUserId(userId);

        // Get all languages the user has completed translations for
        List<Language> completedLanguages = personalInformationRepository
                .findLanguagesByUserId(userId);

        return !completedLanguages.containsAll(selectedLanguages);
    }

    @Override
    public List<String> getMissingLanguages(Long userId){
        // Get all languages the user has selected for translations
        List<Language> selectedLanguages = portfolioTranslationLanguageService
                .findLanguagesByUserId(userId);

        // Get all languages the user has completed translations for
        List<Language> completedLanguages = personalInformationRepository
                .findLanguagesByUserId(userId);

        // Find the missing languages
        selectedLanguages.removeAll(completedLanguages);

        // return this format code:language
        return selectedLanguages.stream()
                .map(language -> language.getLanguage() + "(" + language.getCode() + ")")
                .toList();
    }

    // -------------------------------- find Personal Information --------------------------------
    @Override
    public PersonalInformation getPersonalInformation(String username, String languageCode) {
        User user = userService.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("User not found")
        );

        Language lang = portfolioTranslationLanguageService.getLanguageByCode(languageCode);


        return personalInformationRepository.findByUserAndLanguageAndIsDeletedFalseAndIsArchivedFalse(user, lang)
                .orElseThrow(() -> new PersonalInformationNotFoundException("Personal information not found"));
    }

}
