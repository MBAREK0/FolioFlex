package org.mbarek0.folioflex.web.exception.translationExs;

public class LanguageNotFoundException extends RuntimeException {
    public LanguageNotFoundException(Long languageId) {
        super("Language not found with ID: " + languageId);
    }
    public LanguageNotFoundException(String languageId) {
        super("Language not found with code: " + languageId);
    }
}