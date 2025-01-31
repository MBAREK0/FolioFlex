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
    @Mapping(target = "profilePhoto", source = "profilePhoto", qualifiedByName = "mapMultipartFileToString")
    @Mapping(target = "backgroundBanner", source = "backgroundBanner", qualifiedByName = "mapMultipartFileToString")
    PersonalInformation toEntity(CreatePersonalInformationVM vm);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "language.code", target = "languageCode")
    PersonalInformationVM toVM(PersonalInformation entity);

    // Custom mapping method for MultipartFile -> String
    @org.mapstruct.Named("mapMultipartFileToString")
    default String mapMultipartFileToString(MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            // Here, you can implement the logic to save the file and return the URL/path.
            // For simplicity, we return the file's original filename.
            return file.getOriginalFilename();
        }
        return null;
    }
}
