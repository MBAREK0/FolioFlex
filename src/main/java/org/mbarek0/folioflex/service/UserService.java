package org.mbarek0.folioflex.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mbarek0.folioflex.model.User;
import org.mbarek0.folioflex.repository.UserRepository;
import org.mbarek0.folioflex.web.exception.user.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final LocalDateTime licenseExpirationDate = LocalDateTime.now().plusYears(1);


    public Optional<User> findByUsername(String userName) {
        if (userName == null || userName.isEmpty()) {
           throw new IllegalArgumentException("Username cannot be null");
        }
        return userRepository.findByUsernameAndDeletedFalse(userName);

    }


    public Page<User> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<User> userPage = userRepository.findAll(pageable);

        return userPage;
    }

    public Page<User> searchByUsernameOrCin(String searchKeyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return userRepository.findByUsernameContainingOrEmailContainingAndDeletedFalse(searchKeyword, searchKeyword, pageable);
    }

    public Optional<User> findByEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        return userRepository.findByEmailAndDeletedFalse(email);
    }

    public User findUserById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }



    @Transactional
    public boolean markUserAsDeleted(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setDeleted(true);
        userRepository.save(user);
        return true;
    }
}
