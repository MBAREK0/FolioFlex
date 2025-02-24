package org.mbarek0.folioflex.web.exception.portfolioExs.certificationExs;

public class CertificationNotBelongToUserException extends RuntimeException{
    public CertificationNotBelongToUserException(String message) {
        super(message);
    }
}
