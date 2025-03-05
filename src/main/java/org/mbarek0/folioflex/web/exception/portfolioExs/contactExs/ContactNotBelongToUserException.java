package org.mbarek0.folioflex.web.exception.portfolioExs.contactExs;

public class ContactNotBelongToUserException extends RuntimeException {
    public ContactNotBelongToUserException(String message) {
        super(message);
    }
}
