package org.mbarek0.folioflex.web.vm.mapper;

import org.mbarek0.folioflex.model.portfolio_components.Certification;
import org.mbarek0.folioflex.web.vm.response.portfolio_components.CertificationResponseVM;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CertificationMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "language.code", target = "languageCode")
    @Mapping(source = "certificationId", target = "certificationId")
    CertificationResponseVM toVM(Certification certification);
}