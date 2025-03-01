package org.mbarek0.folioflex.web.exception.authenticationExs;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
