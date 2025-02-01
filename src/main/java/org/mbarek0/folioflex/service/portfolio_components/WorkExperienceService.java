package org.mbarek0.folioflex.service.portfolio_components;

import org.mbarek0.folioflex.model.portfolio_components.WorkExperience;
import org.mbarek0.folioflex.web.vm.request.CreateWorkExperienceVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WorkExperienceService {
    WorkExperience createWorkExperience(CreateWorkExperienceVM request);
    WorkExperience getWorkExperience(Long id);
    Page<WorkExperience> getAllWorkExperiences(String username, Pageable pageable);
    WorkExperience updateWorkExperience(Long id, CreateWorkExperienceVM request);
    void deleteWorkExperience(Long id, Long userId);
    boolean hasMissingTranslations(Long userId);
    List<String> getMissingLanguages(Long userId);
}