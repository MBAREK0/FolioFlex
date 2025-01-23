package org.mbarek0.folioflex.web.exception.user;

public class UserNameAlreadyExistsException extends RuntimeException {
    public UserNameAlreadyExistsException(String s) {
        super(s);
    }
}
