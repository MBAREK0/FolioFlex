package org.mbarek0.folioflex.web.exception.portfolioExs.personal_informationExs;

public class PersonalInformationNotBelongToUser extends RuntimeException{
    public PersonalInformationNotBelongToUser(String msg){
        super(msg);
    }
}
