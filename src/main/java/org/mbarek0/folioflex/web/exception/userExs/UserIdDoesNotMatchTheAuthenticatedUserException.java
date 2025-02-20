package org.mbarek0.folioflex.web.exception.userExs;

public class UserIdDoesNotMatchTheAuthenticatedUserException extends RuntimeException{
    public UserIdDoesNotMatchTheAuthenticatedUserException(String message) {
        super(message);
    }
}
