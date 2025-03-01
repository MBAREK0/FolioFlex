package org.mbarek0.folioflex.repository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.mbarek0.folioflex.model.Language;
import org.mbarek0.folioflex.model.User;
import org.mbarek0.folioflex.model.portfolio_components.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    Optional<Skill> findByIdAndIsDeletedFalseAndIsArchivedFalse(Long id);

    List<Skill> findAllByUserAndIsDeletedFalseAndIsArchivedFalse(User user);

    Optional<Skill> findByUserAndLanguageAndIsDeletedFalseAndIsArchivedFalse(User user, Language language);

    @Query("SELECT s.language FROM Skill s WHERE s.user.id = :userId AND s.isDeleted = false AND s.isArchived = false")
    List<Language> findLanguagesByUserId(@Param("userId") Long userId);

    boolean existsByUserAndIconValueAndIsDeletedFalseAndIsArchivedFalse(User user, String iconValue);

    @Query("SELECT MAX(s.displayOrder) FROM Skill s " +
            "WHERE s.user = :user AND s.isDeleted = false AND s.isArchived = false")
    Integer findMaxDisplayOrderByUserAndIsDeletedFalseAndIsArchivedFalse(@Param("user") User user);

    boolean existsBySkillId(UUID skillId);

    boolean existsByUserAndLanguageAndSkillIdAndIsDeletedFalseAndIsArchivedFalse(User user, Language lang, UUID skillId);

    Optional<Skill> findByUserAndSkillIdAndLanguageAndIsDeletedFalseAndIsArchivedFalse(User user, UUID uuid, Language language);

    List<Skill> findAllBySkillIdAndIsDeletedFalseAndIsArchivedFalse(UUID uuid);

    List<Skill> findAllByUserAndLanguageAndIsDeletedFalseAndIsArchivedFalseOrderByDisplayOrder(User user, Language language);

    List<Skill> findAllByUserAndSkillIdAndIsDeletedFalseAndIsArchivedFalseOrderByDisplayOrder(User user, UUID skillId);

    List<Skill> findAllBySkillIdAndIsDeletedFalse(UUID uuid);

    @Query("""
    SELECT COUNT(s) > 0 FROM Skill s 
    WHERE s.user = :user
    AND LOWER(s.skillName) = LOWER(:skillName) 
    AND s.isDeleted = false 
    AND s.isArchived = false
""")
    boolean existsByUserAndSkillNameAndIsDeletedFalseAndIsArchivedFalse(
            @Param("user") User user,
            @Param("skillName") String skillName
    );


}