package org.mbarek0.folioflex.web.exception.authenticationExs;

public class AuthenticatedUserNotFoundInDatabaseException extends   RuntimeException{
    public AuthenticatedUserNotFoundInDatabaseException(String message) {
        super(message);
    }
}
