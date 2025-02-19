package org.mbarek0.folioflex.service.portfolio_components;

import org.mbarek0.folioflex.model.portfolio_components.Education;
import org.mbarek0.folioflex.web.vm.request.portfolio_components.EducationRequestVM;
import org.mbarek0.folioflex.web.vm.request.portfolio_components.ReorderRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface EducationService {
    List<Education> createEducation(List<EducationRequestVM> requests, MultipartFile schoolLogoFile);

    List<Education> getAllEducation(String username, String languageCode);

    List<Education> getAllEducation(String username, UUID educationId);

    List<Education> updateEducation(UUID uuid, List<EducationRequestVM> educationRequests, MultipartFile schoolLogoFile);

    List<Education> reorder(List<ReorderRequest> reorderRequests);

    List<Education> deleteEducation(UUID uuid);

    List<Education> archiveEducation(UUID uuid);

    Education getEducation(String username, UUID uuid, String languageCode);
    // Other methods for GET, PUT, DELETE, etc.
}