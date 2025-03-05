package org.mbarek0.folioflex.web.vm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mbarek0.folioflex.model.portfolio_components.project.Project;
import org.mbarek0.folioflex.web.vm.response.portfolio_components.ProjectResponseVM;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "language.code", target = "languageCode")
    @Mapping(source = "screenshots", target = "screenshots")
    ProjectResponseVM toVM(Project project);
}
