package org.mbarek0.folioflex.repository;

import org.mbarek0.folioflex.model.Language;
import org.mbarek0.folioflex.model.PortfolioTranslationLanguage;
import org.mbarek0.folioflex.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioTranslationLanguageRepository extends JpaRepository<PortfolioTranslationLanguage,Long> {

    List<PortfolioTranslationLanguage> findByUser(User user);

    List<PortfolioTranslationLanguage> findByLanguage(Language language);

    PortfolioTranslationLanguage findByUserAndLanguage(User user, Language language);

    boolean existsByLanguageAndUser(Language language, User user);

    PortfolioTranslationLanguage findByisPrimary(boolean primary);

    @Query("SELECT ptl.language FROM PortfolioTranslationLanguage ptl WHERE ptl.user.id = :userId")
    List<Language> findLanguagesByUserId(@Param("userId") Long userId);}
