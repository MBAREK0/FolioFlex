package org.mbarek0.folioflex.service.portfolio_components;

import org.mbarek0.folioflex.model.portfolio_components.project.Project;
import org.mbarek0.folioflex.web.vm.request.portfolio_components.ProjectRequestVM;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProjectService {
    List<Project> createProjects(List<ProjectRequestVM> request, MultipartFile projectLogoFile, List<MultipartFile> screenshots);
}
