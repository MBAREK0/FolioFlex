package org.mbarek0.folioflex.web.vm.mapper;

import org.mbarek0.folioflex.model.portfolio_components.Skill;
import org.mbarek0.folioflex.web.vm.response.portfolio_components.SkillResponseVM;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SkillMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "language.code", target = "languageCode")
    @Mapping(source = "skillId", target = "skillId")
    SkillResponseVM toVM(Skill skill);
}