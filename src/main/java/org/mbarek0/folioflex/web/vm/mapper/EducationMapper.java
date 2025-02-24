package org.mbarek0.folioflex.web.vm.mapper;

import org.mbarek0.folioflex.model.portfolio_components.Education;
import org.mbarek0.folioflex.web.vm.response.portfolio_components.EducationResponseVM;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EducationMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "language.code", target = "languageCode")
    @Mapping(source = "educationId", target = "educationId")
    EducationResponseVM toVM(Education education);
}