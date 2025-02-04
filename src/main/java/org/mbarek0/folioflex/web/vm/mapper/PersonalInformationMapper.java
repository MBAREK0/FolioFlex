package org.mbarek0.folioflex.web.vm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.mbarek0.folioflex.model.portfolio_components.PersonalInformation;
import org.mbarek0.folioflex.web.vm.request.CreatePersonalInformationVM;
import org.mbarek0.folioflex.web.vm.response.PersonalInformationVM;
import org.springframework.web.multipart.MultipartFile;

@Mapper(componentModel = "spring")
public interface PersonalInformationMapper {

    PersonalInformationMapper INSTANCE = Mappers.getMapper(PersonalInformationMapper.class);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "language", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "isArchived", ignore = true)
    @Mapping(target = "profilePhoto", ignore = true)  // Handled in service
    @Mapping(target = "backgroundBanner", ignore = true)  // Handled in service
    PersonalInformation toEntity(CreatePersonalInformationVM vm);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "language.code", target = "languageCode")
    @Mapping(source = "profilePhoto", target = "profilePhoto")  // Map entity field to VM field
    @Mapping(source = "backgroundBanner", target = "backgroundBanner")  // Map entity field to VM field
    PersonalInformationVM toVM(PersonalInformation entity);
}