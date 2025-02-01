package org.mbarek0.folioflex.repository;

import org.mbarek0.folioflex.model.Language;
import org.mbarek0.folioflex.model.portfolio_components.WorkExperience;
import org.mbarek0.folioflex.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkExperienceRepository extends JpaRepository<WorkExperience, Long> {

    // Find by ID and not deleted/archived
    Optional<WorkExperience> findByIdAndIsDeletedFalseAndIsArchivedFalse(Long id);

    // Find all by user and not deleted/archived, with pagination and sorting
    Page<WorkExperience> findAllByUserAndIsDeletedFalseAndIsArchivedFalse(User user, Pageable pageable);
    List<WorkExperience> findAllByUserAndIsDeletedFalseAndIsArchivedFalse(User user);


    // Find by user, language, and not deleted/archived
    Optional<WorkExperience> findByUserAndLanguageAndIsDeletedFalseAndIsArchivedFalse(User user, Language language);

    // Check if a work experience exists for a user and language
    boolean existsByUserAndLanguageAndIsDeletedFalseAndIsArchivedFalse(User user, Language language);

    @Query("SELECT we.language FROM WorkExperience we WHERE we.user.id = :userId AND we.isDeleted = false AND we.isArchived = false")
    List<Language> findLanguagesByUserId(@Param("userId") Long userId);

    boolean existsByUserAndCompanyLogoAndIsDeletedFalseAndIsArchivedFalse(User user, String companyLogo);

    @Query("SELECT MAX(we.displayOrder) FROM WorkExperience we " +
            "WHERE we.user = :user AND we.isDeleted = false AND we.isArchived = false")
    Integer findMaxDisplayOrderByUserAndIsDeletedFalseAndIsArchivedFalse(@Param("user") User user);}