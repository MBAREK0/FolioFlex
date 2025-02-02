package org.mbarek0.folioflex.repository;

import org.mbarek0.folioflex.model.Language;
import org.mbarek0.folioflex.model.User;
import org.mbarek0.folioflex.model.portfolio_components.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {

    List<Education> findAllByUserAndIsDeletedFalseAndIsArchivedFalse(User user);
    boolean existsByEducationId(UUID educationId);

    @Query("SELECT MAX(e.displayOrder) FROM Education e " +
            "WHERE e.user = :user AND e.isDeleted = false AND e.isArchived = false")
    Integer findMaxDisplayOrderByUserAndIsDeletedFalseAndIsArchivedFalse(@Param("user") User user);

    boolean existsByUserAndLanguageAndEducationIdAndIsDeletedFalseAndIsArchivedFalse(User user, Language lang, UUID experienceId);
    // Other repository methods
}