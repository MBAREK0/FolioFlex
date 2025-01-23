package org.mbarek0.folioflex.repository;

import org.mbarek0.folioflex.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsernameAndDeletedFalse(String username);

    Optional<User> findByEmailAndDeletedFalse(String email);

    Page<User> findByUsernameContainingOrEmailContainingAndDeletedFalse( String username,
                                       String email,
                                       Pageable pageable);

    Optional<User> findByVerificationToken(String verificationToken);

    List<User> findAllByVerificationToken(String token);
}

