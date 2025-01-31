package org.mbarek0.folioflex.service.user.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mbarek0.folioflex.model.User;
import org.mbarek0.folioflex.repository.UserRepository;
import org.mbarek0.folioflex.service.user.UserService;
import org.mbarek0.folioflex.web.exception.userExs.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findByUsername(String userName) {
        if (userName == null || userName.isEmpty()) {
           throw new IllegalArgumentException("Username cannot be null");
        }
        return userRepository.findByUsernameAndDeletedFalse(userName);

    }

    @Override
    public Page<User> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<User> userPage = userRepository.findAll(pageable);

        return userPage;
    }

    @Override
    public Page<User> searchByUsernameOrCin(String searchKeyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return userRepository.findByUsernameContainingOrEmailContainingAndDeletedFalse(searchKeyword, searchKeyword, pageable);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        return userRepository.findByEmailAndDeletedFalse(email);
    }

    @Override
    public User findUserById(Long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id cannot be less than or equal to 0");
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public boolean existsById(Long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id cannot be less than or equal to 0");
        }
        return userRepository.existsById(id);
    }

    @Override
    @Transactional
    public boolean markUserAsDeleted(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setDeleted(true);
        userRepository.save(user);
        return true;
    }
}
