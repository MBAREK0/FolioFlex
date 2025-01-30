package org.mbarek0.folioflex.repository;

import org.mbarek0.folioflex.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {
    Optional<Language> findByCode(String code);
    Optional<Language> findByLanguage(String language);
}
