package org.mbarek0.folioflex.repository;

import org.mbarek0.folioflex.model.Language;
import org.mbarek0.folioflex.model.PortfolioTranslationLanguage;
import org.mbarek0.folioflex.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioTranslationLanguageRepository extends JpaRepository<PortfolioTranslationLanguage,Long> {

    List<PortfolioTranslationLanguage> findByUserAndIsDeletedFalse(User user);

    Optional<PortfolioTranslationLanguage> findByUserAndLanguageAndIsDeletedFalse(User user, Language language);

    boolean existsByLanguageAndUserAndIsDeletedFalse(Language language, User user);

    Optional<PortfolioTranslationLanguage> findByisPrimaryAndIsDeletedFalse(boolean primary);

    @Query("SELECT ptl.language FROM PortfolioTranslationLanguage ptl WHERE ptl.user.id = :userId AND ptl.isDeleted = false")
    List<Language> findLanguagesByUserId(@Param("userId") Long userId);

    Optional<PortfolioTranslationLanguage> findByUserAndIsPrimaryAndIsDeletedFalse(User user, boolean b);
}
