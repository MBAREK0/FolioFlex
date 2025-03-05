package org.mbarek0.folioflex.repository;

import org.mbarek0.folioflex.model.portfolio_components.Certification;
import org.mbarek0.folioflex.model.language.Language;
import org.mbarek0.folioflex.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {

    Optional<Certification> findByIdAndIsDeletedFalseAndIsArchivedFalse(Long id);

    List<Certification> findAllByUserAndIsDeletedFalseAndIsArchivedFalse(User user);

    Optional<Certification> findByUserAndLanguageAndIsDeletedFalseAndIsArchivedFalse(User user, Language language);

    @Query("SELECT c.language FROM Certification c WHERE c.user.id = :userId AND c.isDeleted = false AND c.isArchived = false")
    List<Language> findLanguagesByUserId(@Param("userId") Long userId);

    boolean existsByUserAndCertificationImageAndIsDeletedFalseAndIsArchivedFalse(User user, String certificationImage);

    @Query("SELECT MAX(c.displayOrder) FROM Certification c " +
            "WHERE c.user = :user AND c.isDeleted = false AND c.isArchived = false")
    Integer findMaxDisplayOrderByUserAndIsDeletedFalseAndIsArchivedFalse(@Param("user") User user);

    boolean existsByCertificationId(UUID certificationId);

    boolean existsByUserAndLanguageAndCertificationIdAndIsDeletedFalseAndIsArchivedFalse(User user, Language lang, UUID certificationId);

    Optional<Certification> findByUserAndCertificationIdAndLanguageAndIsDeletedFalseAndIsArchivedFalse(User user, UUID certificationId, Language language);

    List<Certification> findAllByCertificationIdAndIsDeletedFalseAndIsArchivedFalse(UUID certificationId);

    List<Certification> findAllByUserAndLanguageAndIsDeletedFalseAndIsArchivedFalseOrderByDisplayOrder(User user, Language language);

    List<Certification> findAllByUserAndCertificationIdAndIsDeletedFalseAndIsArchivedFalseOrderByDisplayOrder(User user, UUID certificationId);

    List<Certification> findAllByCertificationIdAndIsDeletedFalse(UUID certificationId);
}