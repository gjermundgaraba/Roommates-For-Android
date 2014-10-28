package com.realkode.roomates.ParseSubclassses;

import com.parse.ParseClassName;
import com.parse.ParseInstallation;

/**
 * Parse subclass for the "_Installation" table.
 */
@ParseClassName("_Installation")
public class Installation extends ParseInstallation {

    public static Installation getCurrentInstallation() {
        return (Installation) ParseInstallation.getCurrentInstallation();
    }

    public User getUser() {
        return (User) getParseUser("user");
    }

    public void setUser(User user) {
        put("user", user);
    }

    public Household getHousehold() {
        return (Household) getParseObject("household");
    }

    public void setHousehold(Household household) {
        put("household", household);
    }

    public void reset() {
        remove("user");
        remove("household");
    }
}
