package org.mbarek0.folioflex.web.exception.skillExs;

public class SkillNotBelongToUserException extends RuntimeException {
    public SkillNotBelongToUserException(String message) {
        super(message);
    }
}
