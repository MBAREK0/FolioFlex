package org.mbarek0.folioflex.web.exception.translationExs;

public class EnglishLanguageNotFoundException extends RuntimeException {
    public EnglishLanguageNotFoundException() {
        super("English language not found in the database");
    }
}