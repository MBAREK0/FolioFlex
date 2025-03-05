package org.mbarek0.folioflex.repository;

import org.mbarek0.folioflex.model.language.Language;
import org.mbarek0.folioflex.model.portfolio_components.WorkExperience;
import org.mbarek0.folioflex.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkExperienceRepository extends JpaRepository<WorkExperience, Long> {

    Optional<WorkExperience> findByIdAndIsDeletedFalseAndIsArchivedFalse(Long id);
    
    List<WorkExperience> findAllByUserAndIsDeletedFalseAndIsArchivedFalse(User user);
    
    Optional<WorkExperience> findByUserAndLanguageAndIsDeletedFalseAndIsArchivedFalse(User user, Language language);
    
    @Query("SELECT we.language FROM WorkExperience we WHERE we.user.id = :userId AND we.isDeleted = false AND we.isArchived = false")
    List<Language> findLanguagesByUserId(@Param("userId") Long userId);

    boolean existsByUserAndCompanyLogoAndIsDeletedFalseAndIsArchivedFalse(User user, String companyLogo);

    @Query("SELECT MAX(we.displayOrder) FROM WorkExperience we " +
            "WHERE we.user = :user AND we.isDeleted = false AND we.isArchived = false")
    Integer findMaxDisplayOrderByUserAndIsDeletedFalseAndIsArchivedFalse(@Param("user") User user);

    boolean existsByExperienceId(UUID experienceId);

    boolean existsByUserAndLanguageAndExperienceIdAndIsDeletedFalseAndIsArchivedFalse(User user, Language lang, UUID experienceId);

    Optional<WorkExperience> findByUserAndExperienceIdAndLanguageAndIsDeletedFalseAndIsArchivedFalse(User user, UUID uuid, Language language);

    List<WorkExperience> findAllByExperienceIdAndIsDeletedFalseAndIsArchivedFalse(UUID uuid);

    List<WorkExperience> findALlByExperienceIdAndIsDeletedFalseAndIsArchivedFalse(UUID componentId);

    List<WorkExperience> findAllByUserAndLanguageAndIsDeletedFalseAndIsArchivedFalseOrderByDisplayOrder(User user, Language language);

    List<WorkExperience> findAllByUserAndExperienceIdAndIsDeletedFalseAndIsArchivedFalseOrderByDisplayOrder(User user, UUID experienceId);

    List<WorkExperience> findAllByExperienceIdAndIsDeletedFalse(UUID uuid);
}