package org.mbarek0.folioflex.web.vm.mapper;


import org.mapstruct.Mapper;
import org.mbarek0.folioflex.model.User;
import org.mbarek0.folioflex.web.vm.request.authentication.RegisterVM;

@Mapper(componentModel = "spring")
public interface UserVMMapper {
    User registerVMtoUser(RegisterVM registerVM);
}
