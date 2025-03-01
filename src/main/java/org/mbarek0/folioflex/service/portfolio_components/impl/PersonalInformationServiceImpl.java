package org.mbarek0.folioflex.service.portfolio_components.impl;

import lombok.AllArgsConstructor;
import org.mbarek0.folioflex.model.Language;
import org.mbarek0.folioflex.model.User;
import org.mbarek0.folioflex.model.portfolio_components.PersonalInformation;
import org.mbarek0.folioflex.repository.PersonalInformationRepository;
import org.mbarek0.folioflex.service.authentication.AuthenticationService;
import org.mbarek0.folioflex.service.aws.S3Service;
import org.mbarek0.folioflex.service.portfolio_components.PersonalInformationService;
import org.mbarek0.folioflex.service.translation.PortfolioTranslationLanguageService;
import org.mbarek0.folioflex.service.user.UserService;
import org.mbarek0.folioflex.web.exception.portfolioExs.personal_informationExs.InvalidImageUrlException;
import org.mbarek0.folioflex.web.exception.portfolioExs.personal_informationExs.PersonalInformationAlreadyExistsException;
import org.mbarek0.folioflex.web.exception.portfolioExs.personal_informationExs.PersonalInformationNotBelongToUser;
import org.mbarek0.folioflex.web.exception.portfolioExs.personal_informationExs.PersonalInformationNotFoundException;
import org.mbarek0.folioflex.web.exception.translationExs.UserDontHaveLanguageException;
import org.mbarek0.folioflex.web.exception.userExs.UserIdDoesNotMatchTheAuthenticatedUserException;
import org.mbarek0.folioflex.web.vm.request.portfolio_components.PersonalInformationRequestVM;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor
@Service
public class PersonalInformationServiceImpl implements PersonalInformationService {

    private final PortfolioTranslationLanguageService portfolioTranslationLanguageService;
    private final UserService userService;
    private final PersonalInformationRepository personalInformationRepository;
    private final S3Service s3Service;
    private final AuthenticationService authenticationService;


    // -------------------------------- Create Personal Information --------------------------------
    @Override
    public PersonalInformation createPersonalInformation(PersonalInformationRequestVM request) {
        Language lang = portfolioTranslationLanguageService.getLanguageByCode(request.getLanguageCode());
        User user = authenticationService.getAuthenticatedUser();

        if (!Objects.equals(user.getId(), request.getUserId()))
            throw new UserIdDoesNotMatchTheAuthenticatedUserException("User id does not match the authenticated user");

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

        return !new HashSet<>(completedLanguages).containsAll(selectedLanguages);
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
        User user = userService.findByUsername(username);
        Language lang ;

        if (languageCode == null || languageCode.isEmpty())
            lang = portfolioTranslationLanguageService.getPrimaryLanguage(user);
        else
            lang = portfolioTranslationLanguageService.getLanguageByCode(languageCode);


        return personalInformationRepository.findByUserAndLanguageAndIsDeletedFalseAndIsArchivedFalse(user, lang)
                .orElseThrow(() -> new PersonalInformationNotFoundException("Personal information not found"));
    }

    @Override
    public List<PersonalInformation> getAllPersonalInformation(String username) {
        User user = userService.findByUsername(username);
        return personalInformationRepository.findAllByUserAndIsDeletedFalseAndIsArchivedFalse(user);
    }


    // -------------------------------- Update Personal Information --------------------------------
    @Override
    public PersonalInformation updatePersonalInformation(Long id, PersonalInformationRequestVM request) {

        PersonalInformation personalInformation = personalInformationRepository.findById(id)
                .orElseThrow(() -> new PersonalInformationNotFoundException("Personal information not found"));

        User user = userService.findUserById(request.getUserId());
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Language lang = portfolioTranslationLanguageService.getLanguageByCode(request.getLanguageCode());

        if (!portfolioTranslationLanguageService.existsByUserAndLanguage(user, lang)) {
            throw new UserDontHaveLanguageException("User is not allowed to use this language");
        }

        if (!personalInformation.getUser().equals(user) || !authenticatedUser.equals(user)) {
            throw new PersonalInformationNotBelongToUser("Personal information does not belong to user");
        }

        Optional<PersonalInformation> existingInfo = personalInformationRepository
                .findByUserAndLanguageAndIsDeletedFalseAndIsArchivedFalse(user, lang);

        if (existingInfo.isPresent() && !existingInfo.get().getId().equals(id)) {
            throw new PersonalInformationAlreadyExistsException("Personal information already exists for this language");
        }

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

        personalInformation.setLanguage(lang);
        personalInformation.setFirstName(request.getFirstName());
        personalInformation.setLastName(request.getLastName());
        personalInformation.setProfilePhoto(profilePhotoUrl);
        personalInformation.setBackgroundBanner(backgroundBannerUrl);
        personalInformation.setAbout(request.getAbout());
        personalInformation.setLocation(request.getLocation());
        personalInformation.setHeadline(request.getHeadline());
        personalInformation.setUpdatedAt(LocalDateTime.now());

        return personalInformationRepository.save(personalInformation);
    }




}
