package org.mbarek0.folioflex.repository;

import org.mbarek0.folioflex.model.language.Language;
import org.mbarek0.folioflex.model.User;
import org.mbarek0.folioflex.model.portfolio_components.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findAllByUserAndIsDeletedFalseAndIsArchivedFalse(User user);

    @Query("SELECT MAX(c.displayOrder) FROM Contact c " +
            "WHERE c.user = :user AND c.isDeleted = false AND c.isArchived = false")
    int findMaxDisplayOrderByUserAndIsDeletedFalseAndIsArchivedFalse(User user);

    boolean existsByUserAndLanguageAndContactIdAndIsDeletedFalseAndIsArchivedFalse(User user, Language lang, UUID contactId);

    boolean existsByContactId(UUID contactId);

    List<Contact> findAllByUserAndLanguageAndIsDeletedFalseAndIsArchivedFalseOrderByDisplayOrder(User user, Language language);

    List<Contact> findAllByUserAndContactIdAndIsDeletedFalseAndIsArchivedFalseOrderByDisplayOrder(User user, UUID contactId);

    Optional<Contact> findByUserAndContactIdAndLanguageAndIsDeletedFalseAndIsArchivedFalse(User user, UUID uuid, Language language);

    Optional<Contact> findByContactIdAndIsDeletedFalse(UUID contactId);
}
