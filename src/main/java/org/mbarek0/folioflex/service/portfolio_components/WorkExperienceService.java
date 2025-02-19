package org.mbarek0.folioflex.service.portfolio_components;

import org.mbarek0.folioflex.model.portfolio_components.WorkExperience;
import org.mbarek0.folioflex.web.vm.request.portfolio_components.ReorderRequest;
import org.mbarek0.folioflex.web.vm.request.portfolio_components.WorkExperienceRequestVM;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface WorkExperienceService {
    
    List<WorkExperience> createWorkExperience(List<WorkExperienceRequestVM> request, MultipartFile companyLogoFile);
  
    List<WorkExperience> getAllWorkExperiences(String username, String languageCode);
  
    List<WorkExperience> getAllWorkExperiences(String username, UUID experienceId);

    WorkExperience getWorkExperience(String username, UUID uuid, String languageCode);

    List<WorkExperience> updateWorkExperience(UUID uuid, List<WorkExperienceRequestVM> workExperienceVM, MultipartFile companyLogoFile);

    List<WorkExperience> reorder(List<ReorderRequest> reorderRequests);

    List<WorkExperience> deleteWorkExperience(UUID uuid);

    List<WorkExperience> archiveWorkExperience(UUID uuid);
}