package org.mbarek0.folioflex.service.portfolio_components;

import org.mbarek0.folioflex.model.portfolio_components.Education;
import org.mbarek0.folioflex.web.vm.request.portfolio_components.EducationRequestVM;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EducationService {
    List<Education> createEducation(List<EducationRequestVM> requests, MultipartFile schoolLogoFile);
    // Other methods for GET, PUT, DELETE, etc.
}