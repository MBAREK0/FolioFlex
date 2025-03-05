package org.mbarek0.folioflex.repository;

import org.mbarek0.folioflex.model.User;
import org.mbarek0.folioflex.model.language.Language;
import org.mbarek0.folioflex.model.portfolio_components.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findAllByUserAndIsDeletedFalseAndIsArchivedFalse(User user);

    @Query("SELECT MAX(p.orderDisplay) FROM Project p " +
            "WHERE p.user = :user AND p.isDeleted = false AND p.isArchived = false")
    Integer findMaxDisplayOrderByUserAndIsDeletedFalseAndIsArchivedFalse(User user);

    boolean existsByUserAndLanguageAndProjectIdAndIsDeletedFalseAndIsArchivedFalse(User user, Language lang, UUID projectId);

    boolean existsByProjectId(UUID projectId);
}
