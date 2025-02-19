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
import org.mbarek0.folioflex.web.vm.request.portfolio_components.ReorderRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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


    @Override
    public List<Education> getAllEducation(String username, String languageCode) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        Language language = languageCode == null ?
                portfolioTranslationLanguageService.getPrimaryLanguage(user) :
                portfolioTranslationLanguageService.getLanguageByCode(languageCode);

        return educationRepository.findAllByUserAndLanguageAndIsDeletedFalseAndIsArchivedFalseOrderByDisplayOrder(user, language);
    }

    @Override
    public List<Education> getAllEducation(String username, UUID educationId) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        return educationRepository.findAllByUserAndEducationIdAndIsDeletedFalseAndIsArchivedFalseOrderByDisplayOrder(user, educationId);
    }

    @Override
    public Education getEducation(String username, UUID uuid, String languageCode) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        Language language = languageCode == null ?
                portfolioTranslationLanguageService.getPrimaryLanguage(user) :
                portfolioTranslationLanguageService.getLanguageByCode(languageCode);

        return educationRepository.findByUserAndEducationIdAndLanguageAndIsDeletedFalseAndIsArchivedFalse(user, uuid, language)
                .orElseThrow(() -> new EducationNotFoundException("Education not found with education ID: " + uuid));
    }

    @Override
    public List<Education> updateEducation(UUID uuid, List<EducationRequestVM> educationRequests, MultipartFile schoolLogoFile) {
        // check if the education belongs to the user
        User user = userService.findUserById(educationRequests.get(0).getUserId());




        if (educationRequests.isEmpty()) {
            throw new InvalidEducationDataException("Education data is empty");
        }

        educationRequests.forEach(req -> {
            if (req.getEducationId() == null || req.getEducationId().equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
                throw new InvalidEducationDataException("Education ID is missing in request for language: " + req.getLanguageCode());
            }
            if (!req.getEducationId().equals(uuid)) {
                throw new InvalidEducationDataException("Education ID in request does not match the path for language: " + req.getLanguageCode());
            }
        });

        if (educationRequests.stream().anyMatch(req -> !req.getEducationId().equals(uuid))) {
            throw new InvalidEducationDataException("Education ID in request does not match the path");
        }

        if (educationRequests.size() != new HashSet<>(educationRequests).size()) {
            throw new InvalidEducationDataException("Duplicate languages in request");
        }

        if (educationRequests.size() != portfolioTranslationLanguageService.findLanguagesCountByUserId(educationRequests.get(0).getUserId())) {
            throw new InvalidEducationDataException("Number of languages in request does not match user's languages");
        }

        List<Education> educations = educationRepository.findAllByEducationIdAndIsDeletedFalseAndIsArchivedFalse(uuid);

        if (educations.isEmpty()) {
            throw new EducationNotFoundException("Education not found with education ID: " + uuid);
        }

        String schoolLogoUrl = schoolLogoFile != null ? s3Service.uploadFile(schoolLogoFile) : educations.get(0).getSchoolLogo();

        educations.forEach(education -> {
            EducationRequestVM request = educationRequests.stream()
                    .filter(req -> req.getLanguageCode().equals(education.getLanguage().getCode()))
                    .findFirst()
                    .orElseThrow(() -> new EducationNotFoundException("Education not found for language: " + education.getLanguage().getLanguage()));

            if (request.getUserId() != user.getId()) {
                throw new EducationNotBelongToUserException("Education does not belong to the user");
            }

            education.setSchoolName(request.getSchoolName());
            education.setSchoolLogo(schoolLogoUrl);
            education.setDegree(request.getDegree());
            education.setFieldOfStudy(request.getFieldOfStudy());
            education.setStartDate(request.getStartDate());
            education.setEndDate(request.getEndDate());
            education.setUpdatedAt(LocalDateTime.now());
        });

        return educationRepository.saveAll(educations);
    }

    @Override
    public List<Education> reorder(List<ReorderRequest> reorderRequests) {
        if (reorderRequests.isEmpty()) {
            throw new InvalidEducationDataException("Reorder request is empty");
        }

        List<Integer> displayOrders = reorderRequests.stream()
                .map(ReorderRequest::getDisplayOrder)
                .toList();

        if (displayOrders.size() != new HashSet<>(displayOrders).size()) {
            throw new InvalidEducationDataException("Duplicate display orders in request");
        }

        Language lang = portfolioTranslationLanguageService.getLanguageByCode(reorderRequests.get(0).getLanguageCode());

        return reorderRequests.stream()
                .map(req -> {
                    List<Education> educations = educationRepository.findAllByEducationIdAndIsDeletedFalseAndIsArchivedFalse(req.getComponentId());
                    if (educations.isEmpty()) {
                        throw new EducationNotFoundException("Education not found with education ID: " + req.getComponentId());
                    }

                    educations.forEach(edu -> edu.setDisplayOrder(req.getDisplayOrder()));
                    educationRepository.saveAll(educations);

                    return educations.stream()
                            .filter(edu -> edu.getLanguage().equals(lang))
                            .findFirst()
                            .orElseThrow(() -> new EducationNotFoundException("Education not found for language: " + lang.getLanguage()));
                })
                .toList();
    }

    @Override
    public List<Education> deleteEducation(UUID uuid) {
        List<Education> educations = educationRepository.findAllByEducationIdAndIsDeletedFalse(uuid);

        if (educations.isEmpty()) {
            throw new EducationNotFoundException("Education not found with education ID: " + uuid);
        }

        educations.forEach(education -> {
            education.setDeleted(true);
            education.setUpdatedAt(LocalDateTime.now());
        });

        return educationRepository.saveAll(educations);
    }

    @Override
    public List<Education> archiveEducation(UUID uuid) {
        List<Education> educations = educationRepository.findAllByEducationIdAndIsDeletedFalseAndIsArchivedFalse(uuid);

        if (educations.isEmpty()) {
            throw new EducationNotFoundException("Education not found with education ID: " + uuid);
        }

        educations.forEach(education -> {
            education.setArchived(true);
            education.setUpdatedAt(LocalDateTime.now());
        });

        return educationRepository.saveAll(educations);
    }




}