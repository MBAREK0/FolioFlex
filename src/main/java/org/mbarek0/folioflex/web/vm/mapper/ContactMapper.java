package org.mbarek0.folioflex.web.vm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mbarek0.folioflex.model.portfolio_components.Contact;
import org.mbarek0.folioflex.web.vm.response.portfolio_components.ContactResponseVM;

@Mapper(componentModel = "spring")
public interface ContactMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "language.code", target = "languageCode")
    @Mapping(source = "contactId", target = "contactId")
    ContactResponseVM toVM(Contact contact);
}