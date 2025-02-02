package org.mbarek0.folioflex.web.vm.mapper;


import org.mbarek0.folioflex.model.portfolio_components.WorkExperience;
import org.mbarek0.folioflex.web.vm.response.portfolio_components.WorkExperienceResponseVM;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface WorkExperienceMapper {

    WorkExperienceMapper INSTANCE = Mappers.getMapper(WorkExperienceMapper.class);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "language.code", target = "languageCode")
    @Mapping(source = "experienceId", target = "experienceId")
    WorkExperienceResponseVM toVM(WorkExperience workExperience);
}