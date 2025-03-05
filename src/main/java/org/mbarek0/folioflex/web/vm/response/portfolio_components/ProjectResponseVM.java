package org.mbarek0.folioflex.web.vm.response.portfolio_components;


import lombok.*;
import org.mbarek0.folioflex.model.enums.ProjectStatus;
import org.mbarek0.folioflex.model.portfolio_components.project.ProjectScreenshot;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponseVM {

    private Long id;
    private Long userId;
    private UUID projectId;
    private String languageCode;
    private String projectName;
    private String projectLogo;
    private List<ProjectScreenshot> screenshots;
    private String description;
    private String demoLink;
    private String slug;
    private Integer orderDisplay;
    private ProjectStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private String[] skills;
    private String[] tags;
}
