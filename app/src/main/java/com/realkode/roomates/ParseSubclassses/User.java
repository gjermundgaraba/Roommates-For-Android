package com.realkode.roomates.ParseSubclassses;

import com.parse.ParseACL;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

/**
 * Parse subclass for the "_User" table.
 */
@ParseClassName("_User")
public class User extends ParseUser {

    public String getDisplayName() {
        return getString("displayName");
    }

    public void setDisplayName(String displayName) {
        put("displayName", displayName);
    }

    public ParseFile getProfilePicture() {
        return getParseFile("profilePicture");
    }

    public void setProfilePicture(ParseFile profilePicture) {
        put("profilePicture", profilePicture);
    }

    public Household getActiveHousehold() {
        return (Household) getParseObject("activeHousehold");
    }

    public static User getCurrentUser() {
        return (User) ParseUser.getCurrentUser();
    }

    public static boolean someoneIsLoggedIn() {
        return getCurrentUser() != null;
    }

    public static boolean loggedInAndMemberOfAHousehold() {
        return (getCurrentUser() != null && getCurrentUser().getActiveHousehold() != null);
    }

    public boolean isMemberOfHousehold() {
        return this.getActiveHousehold() != null;
    }

    public static void refreshChannels() {
        Installation currentInstallation = Installation.getCurrentInstallation();

        // Start by remove all channels
        currentInstallation.reset();

        // Set up empty ACL
        ParseACL defaultACL = new ParseACL();


        if (someoneIsLoggedIn()) {
            // If logged in, add user channel
            User currentUser = User.getCurrentUser();
            currentInstallation.setUser(currentUser);

            if (currentUser.isMemberOfHousehold()) {
                // Set up ACL
                String householdRoleName = "household-" + currentUser.getActiveHousehold().getObjectId();
                defaultACL.setRoleReadAccess(householdRoleName, true);
                defaultACL.setRoleWriteAccess(householdRoleName, true);

                // If member of a household, add household channel
                currentInstallation.setHousehold(currentUser.getActiveHousehold());
            }
        }

        ParseACL.setDefaultACL(defaultACL, false);

        currentInstallation.saveEventually();
    }


}
