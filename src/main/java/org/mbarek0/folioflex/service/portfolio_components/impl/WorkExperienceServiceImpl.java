package org.mbarek0.folioflex.service.portfolio_components.impl;

import lombok.AllArgsConstructor;
import org.mbarek0.folioflex.model.Language;
import org.mbarek0.folioflex.model.User;
import org.mbarek0.folioflex.model.portfolio_components.WorkExperience;
import org.mbarek0.folioflex.repository.WorkExperienceRepository;
import org.mbarek0.folioflex.service.authentication.AuthenticationService;
import org.mbarek0.folioflex.service.aws.S3Service;
import org.mbarek0.folioflex.service.portfolio_components.WorkExperienceService;
import org.mbarek0.folioflex.service.translation.PortfolioTranslationLanguageService;
import org.mbarek0.folioflex.service.user.UserService;
import org.mbarek0.folioflex.web.exception.portfolioExs.work_experienceExs.*;
import org.mbarek0.folioflex.web.exception.skillExs.SkillNotBelongToUserException;
import org.mbarek0.folioflex.web.exception.translationExs.UserDontHaveLanguageException;
import org.mbarek0.folioflex.web.exception.userExs.UserNotFoundException;
import org.mbarek0.folioflex.web.vm.request.portfolio_components.ReorderRequest;
import org.mbarek0.folioflex.web.vm.request.portfolio_components.WorkExperienceRequestVM;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@AllArgsConstructor
@Service
public class WorkExperienceServiceImpl implements WorkExperienceService {

    private final PortfolioTranslationLanguageService portfolioTranslationLanguageService;
    private final UserService userService;
    private final WorkExperienceRepository workExperienceRepository;
    private final S3Service s3Service;
    private final AuthenticationService authenticationService;

    @Override
    public List<WorkExperience> createWorkExperience(List<WorkExperienceRequestVM> request, MultipartFile companyLogoFile) {
        User user = authenticationService.getAuthenticatedUser();

        Long userLanguageCount = portfolioTranslationLanguageService.findLanguagesCountByUserId(user.getId());

        if (userLanguageCount == 0) throw new UserNotFoundException("User does not have any languages");

        if (userLanguageCount != request.size()) throw new InvalidWorkExperienceDataException("Number of languages in request does not match user's languages");


        int expectedSize = List.of(request.get(0).getSkills()).size();

        boolean isValid = request.stream()
                .skip(1)
                .allMatch(r -> List.of(r.getSkills()).size() == expectedSize);

        if (!isValid) {
            throw new InvalidWorkExperienceDataException("Number of skills in request must be the same for all languages");
        }

        UUID experienceId = getUniqueExperienceId();
        String companyLogoUrl = s3Service.uploadFile(companyLogoFile);

        int displayOrder = workExperienceRepository.findAllByUserAndIsDeletedFalseAndIsArchivedFalse(user).isEmpty() ?
                0 : workExperienceRepository.findMaxDisplayOrderByUserAndIsDeletedFalseAndIsArchivedFalse(user) + 1;

        return request.stream()
                .map(req -> {
                    Language lang = portfolioTranslationLanguageService.getLanguageByCode(req.getLanguageCode());
                    return saveWorkExperience(req, user, lang, experienceId, companyLogoUrl, displayOrder);
                })
                .toList();

    }

    private WorkExperience saveWorkExperience(WorkExperienceRequestVM request, User user, Language lang, UUID experienceId, String companyLogoUrl, int displayOrder) {

        if (!Objects.equals(user.getId(), request.getUserId())) {
            throw new WorkExperienceNotBelongToUserException("Experience does not belong to user");
        }

        if (!portfolioTranslationLanguageService.existsByUserAndLanguage(user, lang)) {
            throw new UserDontHaveLanguageException("User is not allowed to use this language");
        }

        if (workExperienceRepository.existsByUserAndLanguageAndExperienceIdAndIsDeletedFalseAndIsArchivedFalse(user, lang,experienceId) ) {
            throw new WorkExperienceAlreadyExistsException(
                    "Work experience already exists for this language: "
                    + lang.getLanguage() + "(" + lang.getCode() + ") And this experience Id:"
                    + experienceId );
        }

        WorkExperience workExperience = new WorkExperience();
        workExperience.setExperienceId(experienceId);
        workExperience.setUser(user);
        workExperience.setLanguage(lang);
        workExperience.setCompanyName(request.getCompanyName());
        workExperience.setJobTitle(request.getJobTitle());
        workExperience.setCompanyLogo(companyLogoUrl);
        workExperience.setLocation(request.getLocation());
        workExperience.setStartDate(request.getStartDate());
        workExperience.setEndDate(request.getEndDate());
        workExperience.setDisplayOrder(displayOrder);
        workExperience.setDescription(request.getDescription());
        workExperience.setUpdatedAt(LocalDateTime.now());
        workExperience.setCreatedAt(LocalDateTime.now());
        workExperience.setArchived(false);
        workExperience.setDeleted(false);
        if (request.getSkills() != null)
            workExperience.setSkills(List.of(request.getSkills()));

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

        User user = userService.findByUsername(username);

        Language language = languageCode == null ?
                portfolioTranslationLanguageService.getPrimaryLanguage(user):
                portfolioTranslationLanguageService.getLanguageByCode(languageCode);

        return workExperienceRepository.findAllByUserAndLanguageAndIsDeletedFalseAndIsArchivedFalseOrderByDisplayOrder(user, language);
    }

    @Override
    public List<WorkExperience> getAllWorkExperiences(String username, UUID experienceId) {
        User user = userService.findByUsername(username);

        List<WorkExperience>  workExperiences = workExperienceRepository.findAllByUserAndExperienceIdAndIsDeletedFalseAndIsArchivedFalseOrderByDisplayOrder(user, experienceId);
        if (workExperiences.isEmpty())
            throw new WorkExperienceNotFoundException("Work experience not found with experience ID: " + experienceId);
        return workExperiences;
    }

    @Override
    public WorkExperience getWorkExperience(String username, UUID uuid, String languageCode) {
        User user = userService.findByUsername(username);

        Language language = languageCode == null ?
                portfolioTranslationLanguageService.getPrimaryLanguage(user):
                portfolioTranslationLanguageService.getLanguageByCode(languageCode);

        return workExperienceRepository.findByUserAndExperienceIdAndLanguageAndIsDeletedFalseAndIsArchivedFalse(user, uuid, language)
                .orElseThrow(() -> new WorkExperienceNotFoundException("Work experience not found"));
    }


    // --------------------------------------- Update Work Experience ---------------------------------------


    @Override
    public List<WorkExperience> updateWorkExperience(UUID uuid, List<WorkExperienceRequestVM> workExperienceVM, MultipartFile companyLogoFile) {

        User user = authenticationService.getAuthenticatedUser();

        if (workExperienceVM.isEmpty())
            throw new InvalidWorkExperienceDataException("Work experience data is empty");

        workExperienceVM.forEach(req -> {
            if (req.getExperienceId() == null || req.getExperienceId().equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
                throw new InvalidWorkExperienceDataException("Experience ID is missing in request for language: " + req.getLanguageCode());
            }
            if (!req.getExperienceId().equals(uuid)) {
                throw new InvalidWorkExperienceDataException("Experience ID in request does not match the path  for language: " + req.getLanguageCode());
            }
        });

        if (workExperienceVM.get(0).getExperienceId() == null)
            throw new InvalidWorkExperienceDataException("Experience ID is required");
        if (workExperienceVM.stream().anyMatch(req -> !req.getExperienceId().equals(uuid)))
            throw new InvalidWorkExperienceDataException("Experience ID in request does not match the path");

        if (workExperienceVM.size() != new HashSet<>(workExperienceVM).size())
            throw new InvalidWorkExperienceDataException("Duplicate languages in request");

        if (workExperienceVM.size() != portfolioTranslationLanguageService.findLanguagesCountByUserId(workExperienceVM.get(0).getUserId()))
            throw new InvalidWorkExperienceDataException("Number of languages in request does not match user's languages");

        int expectedSize = List.of(workExperienceVM.get(0).getSkills()).size();

        boolean isValid = workExperienceVM.stream()
                .skip(1)
                .allMatch(r -> List.of(r.getSkills()).size() == expectedSize);

        if (!isValid) {
            throw new InvalidWorkExperienceDataException("Number of skills in request must be the same for all languages");
        }

        List<WorkExperience> workExperiences = workExperienceRepository.findAllByExperienceIdAndIsDeletedFalseAndIsArchivedFalse(uuid);

        if (workExperiences.isEmpty())
            throw new WorkExperienceNotFoundException("Work experience not found with experience ID: " + uuid);


        workExperiences.forEach(workExperience -> {
            WorkExperienceRequestVM request = workExperienceVM.stream()
                    .filter(req -> req.getLanguageCode().equals(workExperience.getLanguage().getCode()))
                    .findFirst()
                    .orElseThrow(() -> new WorkExperienceNotFoundException("Work experience not found for language: " + workExperience.getLanguage().getLanguage()));

            String companyLogoUrl = companyLogoFile == null ? workExperience.getCompanyLogo()  : s3Service.uploadFile(companyLogoFile);

            if (!Objects.equals(request.getUserId(), user.getId())) {
                throw new WorkExperienceNotBelongToUserException("Experience does not belong to user");
            }

            workExperience.setJobTitle(request.getJobTitle());
            workExperience.setCompanyName(request.getCompanyName());
            workExperience.setCompanyLogo(companyLogoUrl);
            workExperience.setLocation(request.getLocation());
            workExperience.setStartDate(request.getStartDate());
            workExperience.setEndDate(request.getEndDate());
            workExperience.setDescription(request.getDescription());
            if(request.getSkills() != null)
                workExperience.setSkills(new ArrayList<>(List.of(request.getSkills())));
            workExperience.setUpdatedAt(LocalDateTime.now());

        });

        return workExperienceRepository.saveAll(workExperiences);
    }

    @Override
    public List<WorkExperience> reorder(List<ReorderRequest> reorderRequests) {


        List<Integer> displayOrders = reorderRequests.stream()
                .map(ReorderRequest::getDisplayOrder)
                .toList();
        if (displayOrders.size() != new HashSet<>(displayOrders).size())
            throw new InvalidWorkExperienceDataException("Duplicate display orders in request");


        if (reorderRequests.isEmpty())
            throw new InvalidWorkExperienceDataException("Reorder request is empty");

        Language lang = portfolioTranslationLanguageService.getLanguageByCode(reorderRequests.get(0).getLanguageCode());

       return reorderRequests.stream()
                .map(req -> {
                    List<WorkExperience> workExperiences = workExperienceRepository.findALlByExperienceIdAndIsDeletedFalseAndIsArchivedFalse(req.getComponentId());
                    if (workExperiences.isEmpty())
                        throw new WorkExperienceNotFoundException("Work experience not found with experience ID: " + req.getComponentId());

                    workExperiences.forEach(we -> we.setDisplayOrder(req.getDisplayOrder()));

                    workExperienceRepository.saveAll(workExperiences);

                    return workExperiences
                            .stream()
                            .filter(we -> we.getLanguage().equals(lang))
                            .findFirst()
                            .orElseThrow(() -> new WorkExperienceNotFoundException("Work experience not found for language: " + lang.getLanguage()));

                })
                .toList();
    }

    // --------------------------------------- Delete & archive Work Experience ---------------------------------------

    @Override
    public List<WorkExperience> deleteWorkExperience(UUID uuid) {

        User user = authenticationService.getAuthenticatedUser();

        // mark as deleted
        List<WorkExperience> workExperiences = workExperienceRepository.findAllByExperienceIdAndIsDeletedFalse(uuid);

        if (workExperiences.isEmpty())
            throw new WorkExperienceNotFoundException("Work experience not found with experience ID: " + uuid);

        if (workExperiences.stream().anyMatch(workExperience -> !workExperience.getUser().equals(user)))
            throw new SkillNotBelongToUserException("Experience does not belong to user");

        workExperiences.forEach(workExperience -> {
            workExperience.setDeleted(true);
            workExperience.setUpdatedAt(LocalDateTime.now());
        });

        return workExperienceRepository.saveAll(workExperiences);
    }

    @Override
    public List<WorkExperience> archiveWorkExperience(UUID uuid) {
        // mark as archived
        List<WorkExperience> workExperiences = workExperienceRepository.findAllByExperienceIdAndIsDeletedFalseAndIsArchivedFalse(uuid);

        if (workExperiences.isEmpty())
            throw new WorkExperienceNotFoundException("Work experience not found with experience ID: " + uuid);

        workExperiences.forEach(workExperience -> {
            workExperience.setArchived(true);
            workExperience.setUpdatedAt(LocalDateTime.now());
        });

        return workExperienceRepository.saveAll(workExperiences);
    }
}