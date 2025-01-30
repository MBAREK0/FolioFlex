package org.mbarek0.folioflex.web.exception.userExs;

public class RooleNotFoundException extends RuntimeException {
    public RooleNotFoundException(String roleNotFound) {
        super(roleNotFound);
    }
}
