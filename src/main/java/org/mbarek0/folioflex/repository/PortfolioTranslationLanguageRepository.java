package org.mbarek0.folioflex.repository;

import org.mbarek0.folioflex.model.Language;
import org.mbarek0.folioflex.model.PortfolioTranslationLanguage;
import org.mbarek0.folioflex.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioTranslationLanguageRepository extends JpaRepository<PortfolioTranslationLanguage,Long> {

    List<PortfolioTranslationLanguage> findByUser(User user);

    List<PortfolioTranslationLanguage> findByLanguage(Language language);

    PortfolioTranslationLanguage findByUserIdAndLanguage(Long user_id, Language language);
}
