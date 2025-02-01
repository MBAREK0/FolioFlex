package org.mbarek0.folioflex.service.portfolio_components.impl;

import lombok.AllArgsConstructor;
import org.mbarek0.folioflex.model.Language;
import org.mbarek0.folioflex.model.User;
import org.mbarek0.folioflex.model.portfolio_components.WorkExperience;
import org.mbarek0.folioflex.repository.WorkExperienceRepository;
import org.mbarek0.folioflex.service.aws.S3Service;
import org.mbarek0.folioflex.service.portfolio_components.WorkExperienceService;
import org.mbarek0.folioflex.service.translation.PortfolioTranslationLanguageService;
import org.mbarek0.folioflex.service.user.UserService;
import org.mbarek0.folioflex.web.exception.portfolioExs.personal_informationExs.InvalidImageUrlException;
import org.mbarek0.folioflex.web.exception.portfolioExs.work_experienceExs.*;
import org.mbarek0.folioflex.web.exception.translationExs.UserDontHaveLanguageException;
import org.mbarek0.folioflex.web.exception.userExs.UserNotFoundException;
import org.mbarek0.folioflex.web.vm.request.CreateWorkExperienceVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class WorkExperienceServiceImpl implements WorkExperienceService {

    private final PortfolioTranslationLanguageService portfolioTranslationLanguageService;
    private final UserService userService;
    private final WorkExperienceRepository workExperienceRepository;
    private final S3Service s3Service;

    @Override
    public WorkExperience createWorkExperience(CreateWorkExperienceVM request) {
        Language lang = portfolioTranslationLanguageService.getLanguageByCode(request.getLanguageCode());
        User user = userService.findUserById(request.getUserId());

        if (!portfolioTranslationLanguageService.existsByUserAndLanguage(user, lang)) {
            throw new UserDontHaveLanguageException("User is not allowed to use this language");
        }

        if (workExperienceRepository.existsByUserAndLanguageAndIsDeletedFalseAndIsArchivedFalse(user, lang)) {
            throw new WorkExperienceAlreadyExistsException("Work experience already exists for this language");
        }

        String companyLogoUrl = resolveImageUrl(request.getCompanyLogoFile(), request.getCompanyLogoUrl(), user);

        WorkExperience workExperience = new WorkExperience();
        workExperience.setUser(user);
        workExperience.setLanguage(lang);
        workExperience.setJobTitle(request.getJobTitle());
        workExperience.setCompanyName(request.getCompanyName());
        workExperience.setCompanyLogo(companyLogoUrl);
        workExperience.setLocation(request.getLocation());
        workExperience.setStartDate(request.getStartDate());
        workExperience.setEndDate(request.getEndDate());
        workExperience.setDescription(request.getDescription());
        if (workExperienceRepository.findAllByUserAndIsDeletedFalseAndIsArchivedFalse(user).isEmpty()) {
            workExperience.setDisplayOrder(0);
        } else {
            Integer maxDisplayOrder = workExperienceRepository.findMaxDisplayOrderByUserAndIsDeletedFalseAndIsArchivedFalse(user);
            workExperience.setDisplayOrder(maxDisplayOrder == null ? 0 : maxDisplayOrder + 1);
        }
        workExperience.setCreatedAt(LocalDateTime.now());
        workExperience.setUpdatedAt(LocalDateTime.now());
        workExperience.setArchived(false);
        workExperience.setDeleted(false);

        return workExperienceRepository.save(workExperience);
    }

    private String resolveImageUrl(MultipartFile file, String url, User user) {
        if (file != null && !file.isEmpty()) {
            return s3Service.uploadFile(file);
        } else if (url != null && !url.isEmpty()) {
            validateImageUrlOwnership(url, user);
            return url;
        }
        return null;
    }

    private void validateImageUrlOwnership(String url, User user) {
        boolean isUrlValid = workExperienceRepository.existsByUserAndCompanyLogoAndIsDeletedFalseAndIsArchivedFalse(user, url);
        if (!isUrlValid) {
            throw new InvalidImageUrlException("Image URL does not belong to user");
        }
    }

    // --------------------------------------- Fetch Work Experience ---------------------------------------
    @Override
    public WorkExperience getWorkExperience(Long id) {
        return workExperienceRepository.findByIdAndIsDeletedFalseAndIsArchivedFalse(id)
                .orElseThrow(() -> new WorkExperienceNotFoundException("Work experience not found"));
    }

    @Override
    public Page<WorkExperience> getAllWorkExperiences(String username, Pageable pageable) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return workExperienceRepository.findAllByUserAndIsDeletedFalseAndIsArchivedFalse(user, pageable);
    }

    @Override
    public WorkExperience updateWorkExperience(Long id, CreateWorkExperienceVM request) {
        WorkExperience workExperience = workExperienceRepository.findByIdAndIsDeletedFalseAndIsArchivedFalse(id)
                .orElseThrow(() -> new WorkExperienceNotFoundException("Work experience not found"));

        User user = userService.findUserById(request.getUserId());
        Language lang = portfolioTranslationLanguageService.getLanguageByCode(request.getLanguageCode());

        if (!portfolioTranslationLanguageService.existsByUserAndLanguage(user, lang)) {
            throw new UserDontHaveLanguageException("User is not allowed to use this language");
        }

        if (!workExperience.getUser().equals(user)) {
            throw new WorkExperienceNotFoundException("Work experience not found");
        }

        if (workExperienceRepository.existsByUserAndLanguageAndIsDeletedFalseAndIsArchivedFalse(user, lang)
                && !workExperience.getId().equals(id)) {
            throw new WorkExperienceAlreadyExistsException("Work experience already exists for this language");
        }

        String companyLogoUrl = resolveImageUrl(request.getCompanyLogoFile(), request.getCompanyLogoUrl(), user);

        workExperience.setLanguage(lang);
        workExperience.setJobTitle(request.getJobTitle());
        workExperience.setCompanyName(request.getCompanyName());
        workExperience.setCompanyLogo(companyLogoUrl);
        workExperience.setLocation(request.getLocation());
        workExperience.setStartDate(request.getStartDate());
        workExperience.setEndDate(request.getEndDate());
        workExperience.setDescription(request.getDescription());
        workExperience.setDisplayOrder(workExperience.getDisplayOrder());
        workExperience.setUpdatedAt(LocalDateTime.now());

        return workExperienceRepository.save(workExperience);
    }

    @Override
    public void deleteWorkExperience(Long id, Long userId) {
        WorkExperience workExperience = workExperienceRepository.findByIdAndIsDeletedFalseAndIsArchivedFalse(id)
                .orElseThrow(() -> new WorkExperienceNotFoundException("Work experience not found"));

        if (!workExperience.getUser().getId().equals(userId)) {
            throw new WorkExperienceNotFoundException("Work experience not found");
        }

        workExperience.setDeleted(true);
        workExperience.setUpdatedAt(LocalDateTime.now());
        workExperienceRepository.save(workExperience);
    }

    @Override
    public boolean hasMissingTranslations(Long userId) {
        List<Language> selectedLanguages = portfolioTranslationLanguageService.findLanguagesByUserId(userId);
        List<Language> completedLanguages = workExperienceRepository.findLanguagesByUserId(userId);
        return !completedLanguages.containsAll(selectedLanguages);
    }

    @Override
    public List<String> getMissingLanguages(Long userId) {
        List<Language> selectedLanguages = portfolioTranslationLanguageService.findLanguagesByUserId(userId);
        List<Language> completedLanguages = workExperienceRepository.findLanguagesByUserId(userId);
        selectedLanguages.removeAll(completedLanguages);
        return selectedLanguages.stream()
                .map(language -> language.getLanguage() + "(" + language.getCode() + ")")
                .toList();
    }
}