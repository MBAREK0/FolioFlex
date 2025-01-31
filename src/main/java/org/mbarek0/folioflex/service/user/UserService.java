package org.mbarek0.folioflex.service.user;

import org.mbarek0.folioflex.model.User;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String userName);

    Page<User> getAllUsers(int page, int size);

    Page<User> searchByUsernameOrCin(String searchKeyword, int page, int size);

    Optional<User> findByEmail(String email);

    User findUserById(Long id);

    boolean existsById(Long id);

    boolean markUserAsDeleted(Long userId);

}