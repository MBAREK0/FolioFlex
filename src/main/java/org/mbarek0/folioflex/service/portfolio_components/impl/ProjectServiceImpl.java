package org.mbarek0.folioflex.service.portfolio_components.impl;

import lombok.RequiredArgsConstructor;
import org.mbarek0.folioflex.model.User;
import org.mbarek0.folioflex.model.language.Language;
import org.mbarek0.folioflex.model.portfolio_components.project.Project;
import org.mbarek0.folioflex.model.portfolio_components.project.ProjectScreenshot;
import org.mbarek0.folioflex.repository.ProjectRepository;
import org.mbarek0.folioflex.service.authentication.AuthenticationService;
import org.mbarek0.folioflex.service.aws.S3Service;
import org.mbarek0.folioflex.service.portfolio_components.ProjectService;
import org.mbarek0.folioflex.service.translation.PortfolioTranslationLanguageService;
import org.mbarek0.folioflex.web.exception.portfolioExs.projectExs.InvalidProjectDataException;
import org.mbarek0.folioflex.web.exception.portfolioExs.projectExs.ProjectAlreadyExistsException;
import org.mbarek0.folioflex.web.exception.portfolioExs.projectExs.ProjectNotBelongToUserException;
import org.mbarek0.folioflex.web.exception.portfolioExs.projectExs.UserDontHaveLanguageException;
import org.mbarek0.folioflex.web.vm.request.portfolio_components.ProjectRequestVM;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final PortfolioTranslationLanguageService portfolioTranslationLanguageService;
    private final ProjectRepository projectRepository;
    private final S3Service s3Service;
    private final AuthenticationService authenticationService;

    @Override
    public List<Project> createProjects(List<ProjectRequestVM> request, MultipartFile projectLogoFile, List<MultipartFile> screenshots) {
        User user = authenticationService.getAuthenticatedUser();

        Long userLanguageCount = portfolioTranslationLanguageService.findLanguagesCountByUserId(user.getId());

        if (userLanguageCount == 0) throw new UserDontHaveLanguageException("User does not have any languages");

        if (userLanguageCount != request.size())
            throw new InvalidProjectDataException("Number of languages in request does not match user's languages");

        int expectedSize = List.of(request.get(0).getSkills()).size();

        boolean isValid = request.stream()
                .skip(1)
                .allMatch(r -> List.of(r.getSkills()).size() == expectedSize);

        if (!isValid) {
            throw new InvalidProjectDataException("Number of skills in request must be the same for all languages");
        }

        UUID projectId = getUniqueProjectId();
        String projectLogoUrl = s3Service.uploadFile(projectLogoFile);

        AtomicInteger index = new AtomicInteger(0);
        List<ProjectScreenshot> screenshotUrls = screenshots.stream()
                .map(screenshot -> saveProjectScreenshot(screenshot, index.getAndIncrement()))
                .toList();

        int displayOrder = projectRepository.findAllByUserAndIsDeletedFalseAndIsArchivedFalse(user).isEmpty() ?
                0 : projectRepository.findMaxDisplayOrderByUserAndIsDeletedFalseAndIsArchivedFalse(user) + 1;

        return request.stream()
                .map(req -> {
                    Language lang = portfolioTranslationLanguageService.getLanguageByCode(req.getLanguageCode());
                    return saveProject(req, user, lang, projectId, projectLogoUrl, screenshotUrls, displayOrder);
                })
                .toList();
    }

    private ProjectScreenshot saveProjectScreenshot(MultipartFile screenshot, int orderDisplay) {
        String screenshotUrl = s3Service.uploadFile(screenshot);
        return ProjectScreenshot.builder()
                .screenshotPath(screenshotUrl)
                .orderDisplay(orderDisplay)
                .isArchived(false)
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private Project saveProject(ProjectRequestVM request, User user, Language lang, UUID projectId, String projectLogoUrl, List<ProjectScreenshot> screenshotUrls, int displayOrder) {

        if (!Objects.equals(user.getId(), request.getUserId())) {
            throw new ProjectNotBelongToUserException("Project does not belong to user");
        }

        if (!portfolioTranslationLanguageService.existsByUserAndLanguage(user, lang)) {
            throw new UserDontHaveLanguageException("User is not allowed to use this language");
        }

        if (projectRepository.existsByUserAndLanguageAndProjectIdAndIsDeletedFalseAndIsArchivedFalse(user, lang, projectId)) {
            throw new ProjectAlreadyExistsException(
                    "Project already exists for this language: "
                            + lang.getLanguage() + "(" + lang.getCode() + ") And this project Id: "
                            + projectId);
        }

        Project project = new Project();
        project.setProjectId(projectId);
        project.setUser(user);
        project.setLanguage(lang);
        project.setProjectName(request.getProjectName());
        project.setDescription(request.getDescription());
        project.setProjectLogo(projectLogoUrl);
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        project.setDemoLink(request.getDemoLink());
        project.setSlug(request.getSlug());
        project.setOrderDisplay(displayOrder);
        project.setStatus(request.getStatus());
        if (request.getSkills() != null)
            project.setSkills(List.of(request.getSkills()));
        if (request.getTags() != null)
            project.setTags(List.of(request.getTags()));
        project.setIsDeleted(false);
        project.setIsArchived(false);
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());



        // Ensure screenshots are properly linked to the project
        screenshotUrls.forEach(screenshot -> screenshot.setProject(project));
        project.setScreenshots(screenshotUrls);

        return projectRepository.save(project);
    }

    private UUID getUniqueProjectId() {
        UUID projectId = UUID.randomUUID();
        while (projectRepository.existsByProjectId(projectId)) {
            projectId = UUID.randomUUID();
        }
        return projectId;
    }


}