package org.mbarek0.folioflex.web.exception.translationExs;

public class PortfolioTranslationLanguageAlreadyExistsException extends RuntimeException {
    public PortfolioTranslationLanguageAlreadyExistsException(Long userId) {
        super("Portfolio translation language already exists for user with ID: " + userId);
    }
}