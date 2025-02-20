package org.mbarek0.folioflex.service.user;

import org.mbarek0.folioflex.model.User;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface UserService {
    User findByUsername(String userName);

    User findByEmail(String email);

    User findUserById(Long id);

    boolean existsById(Long id);

    boolean markUserAsDeleted(Long userId);

}