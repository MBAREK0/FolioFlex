package org.mbarek0.folioflex.service.portfolio_components.impl;

import lombok.AllArgsConstructor;
import org.mbarek0.folioflex.model.Language;
import org.mbarek0.folioflex.model.User;
import org.mbarek0.folioflex.model.portfolio_components.Education;
import org.mbarek0.folioflex.repository.EducationRepository;
import org.mbarek0.folioflex.service.aws.S3Service;
import org.mbarek0.folioflex.service.portfolio_components.EducationService;
import org.mbarek0.folioflex.service.translation.PortfolioTranslationLanguageService;
import org.mbarek0.folioflex.service.user.UserService;
import org.mbarek0.folioflex.web.exception.portfolioExs.educationExs.*;
import org.mbarek0.folioflex.web.exception.translationExs.UserDontHaveLanguageException;
import org.mbarek0.folioflex.web.exception.userExs.UserNotFoundException;
import org.mbarek0.folioflex.web.vm.request.portfolio_components.EducationRequestVM;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class EducationServiceImpl implements EducationService {

    private final PortfolioTranslationLanguageService portfolioTranslationLanguageService;
    private final UserService userService;
    private final EducationRepository educationRepository;
    private final S3Service s3Service;

    @Override
    public List<Education> createEducation(List<EducationRequestVM> requests, MultipartFile schoolLogoFile) {
        // Validate the user
        User user = userService.findUserById(requests.get(0).getUserId());
        if (user == null) {
            throw new UserNotFoundException("User not found with ID: " + requests.get(0).getUserId());
        }

        // Validate the number of languages
        Long userLanguageCount = portfolioTranslationLanguageService.findLanguagesCountByUserId(user.getId());
        if (userLanguageCount == 0) {
            throw new UserDontHaveLanguageException("User does not have any languages");
        }
        if (userLanguageCount != requests.size()) {
            throw new InvalidEducationDataException("Number of languages in request does not match user's languages");
        }

        // Generate a unique education ID
        UUID educationId = getUniqueEducationId();

        // Upload the school logo to S3
        String schoolLogoUrl = s3Service.uploadFile(schoolLogoFile);

        // Determine the display order
        int displayOrder = educationRepository.findAllByUserAndIsDeletedFalseAndIsArchivedFalse(user).isEmpty() ?
                0 : educationRepository.findMaxDisplayOrderByUserAndIsDeletedFalseAndIsArchivedFalse(user) + 1;

        // Save each education entry
        return requests.stream()
                .map(request -> saveEducation(request, user, educationId, schoolLogoUrl, displayOrder))
                .toList();
    }

    private Education saveEducation(EducationRequestVM request, User user, UUID educationId, String schoolLogoUrl, int displayOrder) {
        // Validate the language
        Language language = portfolioTranslationLanguageService.getLanguageByCode(request.getLanguageCode());
        if (!portfolioTranslationLanguageService.existsByUserAndLanguage(user, language)) {
            throw new UserDontHaveLanguageException("User is not allowed to use this language");
        }

        // Check for duplicate entries
        if (educationRepository.existsByUserAndLanguageAndEducationIdAndIsDeletedFalseAndIsArchivedFalse(user, language, educationId)) {
            throw new EducationAlreadyExistsException(
                    "Education already exists for this language: " + language.getLanguage() + "(" + language.getCode() + ")");
        }

        // Create and save the education entity
        Education education = new Education();
        education.setEducationId(educationId);
        education.setUser(user);
        education.setLanguage(language);
        education.setSchoolName(request.getSchoolName());
        education.setSchoolLogo(schoolLogoUrl);
        education.setDegree(request.getDegree());
        education.setFieldOfStudy(request.getFieldOfStudy());
        education.setStartDate(request.getStartDate());
        education.setEndDate(request.getEndDate());
        education.setDisplayOrder(displayOrder);
        education.setDeleted(false);
        education.setArchived(false);
        education.setCreatedAt(LocalDateTime.now());
        education.setUpdatedAt(LocalDateTime.now());

        return educationRepository.save(education);
    }

    private UUID getUniqueEducationId() {
        UUID educationId = UUID.randomUUID();
        while (educationRepository.existsByEducationId(educationId)) {
            educationId = UUID.randomUUID();
        }
        return educationId;
    }
}