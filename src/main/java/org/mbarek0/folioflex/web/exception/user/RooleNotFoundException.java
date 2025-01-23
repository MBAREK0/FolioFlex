package org.mbarek0.folioflex.web.exception.user;

public class RooleNotFoundException extends RuntimeException {
    public RooleNotFoundException(String roleNotFound) {
        super(roleNotFound);
    }
}
