package com.realkode.roomates.ParseSubclassses;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Invitation")
public class Invitation extends ParseObject {

    private static final String HOUSEHOLD = "household";
    private static final String INVITER = "inviter";

    public Household getHousehold() {
        return (Household) getParseObject(HOUSEHOLD);
    }

    public void setHousehold(Household household) {
        put(HOUSEHOLD, household);
    }

    public User getInviter() {
        return (User) getParseUser(INVITER);
    }
}
