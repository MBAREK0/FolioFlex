package org.mbarek0.folioflex.service.translation;

import org.mbarek0.folioflex.model.Language;
import org.mbarek0.folioflex.model.PortfolioTranslationLanguage;
import org.mbarek0.folioflex.model.User;
import org.mbarek0.folioflex.web.exception.InvalidInputException;
import org.mbarek0.folioflex.web.exception.translationExs.EnglishLanguageNotFoundException;
import org.mbarek0.folioflex.web.exception.translationExs.LanguageNotFoundException;
import org.mbarek0.folioflex.web.exception.translationExs.PortfolioTranslationLanguageAlreadyExistsException;
import org.mbarek0.folioflex.web.exception.userExs.UserNotFoundException;
import org.mbarek0.folioflex.web.vm.request.translation.PortfolioTranslationLanguageRequestVM;

import java.util.List;

public interface PortfolioTranslationLanguageService {

    List<PortfolioTranslationLanguage> save(List<PortfolioTranslationLanguageRequestVM> createPortfolioTranslationLanguageVMs)
            throws InvalidInputException, EnglishLanguageNotFoundException, LanguageNotFoundException;

    PortfolioTranslationLanguage save(Long userId, Long languageId)
            throws UserNotFoundException, LanguageNotFoundException, PortfolioTranslationLanguageAlreadyExistsException;

    List<PortfolioTranslationLanguage> getAllPortfolioTranslationLanguagesForUser(Long userId)
            throws UserNotFoundException;

    List<Language> getAllPortfolioTranslationLanguage();

    PortfolioTranslationLanguage updatePortfolioTranslationLanguagePrimary(Long userId, Long languageId)
            throws UserNotFoundException, LanguageNotFoundException, InvalidInputException;

    void deletePortfolioTranslationLanguage(Long userId, Long languageId);

    Language getLanguageByCode(String s);

    boolean existsByUserAndLanguage(User user, Language lang);

    List<Language> findLanguagesByUserId(Long userId);

    Language getPrimaryLanguage(User user);

    Long findLanguagesCountByUserId(Long id);
}