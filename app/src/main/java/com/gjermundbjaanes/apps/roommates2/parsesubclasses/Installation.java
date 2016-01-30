package com.gjermundbjaanes.apps.roommates2.parsesubclasses;

import com.parse.ParseClassName;
import com.parse.ParseInstallation;

@ParseClassName("_Installation")
public class Installation extends ParseInstallation {

    private static final String USER = "user";
    private static final String HOUSEHOLD = "household";

    public static Installation getCurrentInstallation() {
        return (Installation) ParseInstallation.getCurrentInstallation();
    }

    public User getUser() {
        return (User) getParseUser(USER);
    }

    public void setUser(User user) {
        put(USER, user);
    }

    public Household getHousehold() {
        return (Household) getParseObject(HOUSEHOLD);
    }

    public void setHousehold(Household household) {
        put(HOUSEHOLD, household);
    }

    public void reset() {
        remove(USER);
        remove(HOUSEHOLD);
    }
}
