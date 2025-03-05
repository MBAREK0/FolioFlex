package org.mbarek0.folioflex.web.exception.portfolioExs.projectExs;

public class ProjectNotBelongToUserException extends RuntimeException{
    public ProjectNotBelongToUserException(String message){
        super(message);
    }
}
