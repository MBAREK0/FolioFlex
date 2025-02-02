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
import org.mbarek0.folioflex.web.exception.portfolioExs.work_experienceExs.*;
import org.mbarek0.folioflex.web.exception.translationExs.UserDontHaveLanguageException;
import org.mbarek0.folioflex.web.exception.userExs.UserNotFoundException;
import org.mbarek0.folioflex.web.vm.request.portfolio_components.WorkExperienceRequestVM;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class WorkExperienceServiceImpl implements WorkExperienceService {

    private final PortfolioTranslationLanguageService portfolioTranslationLanguageService;
    private final UserService userService;
    private final WorkExperienceRepository workExperienceRepository;
    private final S3Service s3Service;

    @Override
    public List<WorkExperience> createWorkExperience(List<WorkExperienceRequestVM> request, MultipartFile companyLogoFile) {
        User user = userService.findUserById(request.get(0).getUserId());

        Long userLanguageCount = portfolioTranslationLanguageService.findLanguagesCountByUserId(user.getId());

        if (userLanguageCount == 0) throw new UserNotFoundException("User does not have any languages");

        if (userLanguageCount != request.size()) throw new InvalidWorkExperienceDataException("Number of languages in request does not match user's languages");

        UUID experienceId = getUniqueExperienceId();
        String companyLogoUrl = s3Service.uploadFile(companyLogoFile);

        final int displayOrder ;

        if (!workExperienceRepository.findAllByUserAndIsDeletedFalseAndIsArchivedFalse(user).isEmpty()) {

            Integer maxDisplayOrder = workExperienceRepository.findMaxDisplayOrderByUserAndIsDeletedFalseAndIsArchivedFalse(user);
            displayOrder = maxDisplayOrder == null ? 0 : maxDisplayOrder + 1;

        }else displayOrder = 0;


        return request.stream()
                .map(req -> {
                    Language lang = portfolioTranslationLanguageService.getLanguageByCode(req.getLanguageCode());
                    return saveWorkExperience(req, user, lang, experienceId, companyLogoUrl, displayOrder);
                })
                .toList();

    }

    private WorkExperience saveWorkExperience(WorkExperienceRequestVM request, User user, Language lang, UUID experienceId, String companyLogoUrl, int displayOrder) {

        if (!portfolioTranslationLanguageService.existsByUserAndLanguage(user, lang)) {
            throw new UserDontHaveLanguageException("User is not allowed to use this language");
        }

        if (workExperienceRepository.existsByUserAndLanguageAndExperienceIdAndIsDeletedFalseAndIsArchivedFalse(user, lang,experienceId) ) {
            throw new WorkExperienceAlreadyExistsException(
                    "Work experience already exists for this language: "
                    + lang.getLanguage() + "(" + lang.getCode() + ") And this experiencr Id:"
                    + experienceId );
        }

        WorkExperience workExperience = new WorkExperience();
        workExperience.setExperienceId(experienceId);
        workExperience.setUser(user);
        workExperience.setLanguage(lang);
        workExperience.setJobTitle(request.getJobTitle());
        workExperience.setCompanyName(request.getCompanyName());
        workExperience.setCompanyLogo(companyLogoUrl);
        workExperience.setLocation(request.getLocation());
        workExperience.setStartDate(request.getStartDate());
        workExperience.setEndDate(request.getEndDate());
        workExperience.setDescription(request.getDescription());
        workExperience.setDisplayOrder(displayOrder);
        workExperience.setCreatedAt(LocalDateTime.now());
        workExperience.setUpdatedAt(LocalDateTime.now());
        workExperience.setArchived(false);
        workExperience.setDeleted(false);

        return workExperienceRepository.save(workExperience);
    }

    private UUID getUniqueExperienceId() {
        UUID experienceId = UUID.randomUUID();
        while (workExperienceRepository.existsByExperienceId(experienceId)) {
            experienceId = UUID.randomUUID();
        }
        return experienceId;
    }


    // --------------------------------------- Fetch Work Experience ---------------------------------------

    @Override
    public List<WorkExperience> getAllWorkExperiences(String username, String languageCode) {

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));


        Language language = languageCode == null ?
                portfolioTranslationLanguageService.getPrimaryLanguage(user):
                portfolioTranslationLanguageService.getLanguageByCode(languageCode);

        return workExperienceRepository.findAllByUserAndLanguageAndIsDeletedFalseAndIsArchivedFalse(user, language);
    }

    @Override
    public List<WorkExperience> getAllWorkExperiences(String username, UUID experienceId) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        return workExperienceRepository.findAllByUserAndExperienceIdAndIsDeletedFalseAndIsArchivedFalse(user, experienceId);
    }

    @Override
    public WorkExperience getAWorkExperiencs(String username, UUID uuid, String s) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        Language language = s == null ?
                portfolioTranslationLanguageService.getPrimaryLanguage(user):
                portfolioTranslationLanguageService.getLanguageByCode(s);

        return workExperienceRepository.findByUserAndExperienceIdAndLanguageAndIsDeletedFalseAndIsArchivedFalse(user, uuid, language)
                .orElseThrow(() -> new WorkExperienceNotFoundException("Work experience not found"));
    }


    // --------------------------------------- Update Work Experience ---------------------------------------


    @Override
    public List<WorkExperience> updateWorkExperience(UUID uuid, List<WorkExperienceRequestVM> workExperienceVM, MultipartFile companyLogoFile) {


        if (workExperienceVM.isEmpty())
            throw new InvalidWorkExperienceDataException("Work experience data is empty");
        if (workExperienceVM.get(0).getExperienceId() == null)
            throw new InvalidWorkExperienceDataException("Experience ID is required");
        if (workExperienceVM.stream().anyMatch(req -> !req.getExperienceId().equals(uuid)))
            throw new InvalidWorkExperienceDataException("Experience ID in request does not match the path");

        if (workExperienceVM.size() != new HashSet<>(workExperienceVM).size())
            throw new InvalidWorkExperienceDataException("Duplicate languages in request");

        if (workExperienceVM.size() != portfolioTranslationLanguageService.findLanguagesCountByUserId(workExperienceVM.get(0).getUserId()))
            throw new InvalidWorkExperienceDataException("Number of languages in request does not match user's languages");

        List<WorkExperience> workExperiences = workExperienceRepository.findAllByExperienceIdAndIsDeletedFalseAndIsArchivedFalse(uuid);

        if (workExperiences.isEmpty())
            throw new WorkExperienceNotFoundException("Work experience not found with experience ID: " + uuid);


        workExperiences.forEach(workExperience -> {
            WorkExperienceRequestVM request = workExperienceVM.stream()
                    .filter(req -> req.getLanguageCode().equals(workExperience.getLanguage().getCode()))
                    .findFirst()
                    .orElseThrow(() -> new WorkExperienceNotFoundException("Work experience not found for language: " + workExperience.getLanguage().getLanguage()));

            String companyLogoUrl = companyLogoFile == null ? workExperience.getCompanyLogo()  : s3Service.uploadFile(companyLogoFile);

            workExperience.setJobTitle(request.getJobTitle());
            workExperience.setCompanyName(request.getCompanyName());
            workExperience.setCompanyLogo(companyLogoUrl);
            workExperience.setLocation(request.getLocation());
            workExperience.setStartDate(request.getStartDate());
            workExperience.setEndDate(request.getEndDate());
            workExperience.setDescription(request.getDescription());
            workExperience.setUpdatedAt(LocalDateTime.now());

        });

        return workExperienceRepository.saveAll(workExperiences);
    }

}