package com.gjermundbjaanes.apps.roommates2.parsesubclasses;

import com.gjermundbjaanes.apps.roommates2.helpers.ParseCloudFunctionNames;
import com.parse.FunctionCallback;
import com.parse.ParseACL;
import com.parse.ParseClassName;
import com.parse.ParseCloud;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.HashMap;

@ParseClassName("_User")
public class User extends ParseUser {

    private static final String DISPLAY_NAME = "displayName";
    private static final String PROFILE_PICTURE = "profilePicture";
    private static final String ACTIVE_HOUSEHOLD = "activeHousehold";

    public static User getCurrentUser() {
        return (User) ParseUser.getCurrentUser();
    }

    public static boolean someoneIsLoggedIn() {
        return getCurrentUser() != null;
    }

    public static boolean loggedInAndMemberOfAHousehold() {
        return (getCurrentUser() != null && getCurrentUser().getActiveHousehold() != null);
    }

    public static void refreshChannels() {
        Installation currentInstallation = Installation.getCurrentInstallation();

        currentInstallation.reset();

        ParseACL defaultACL = new ParseACL();

        if (someoneIsLoggedIn()) {
            User currentUser = User.getCurrentUser();
            currentInstallation.setUser(currentUser); // Subscribes to your user channel

            if (currentUser.isMemberOfAHousehold()) {
                String householdRoleName = getHouseholdChannel(currentUser);
                defaultACL.setRoleReadAccess(householdRoleName, true);
                defaultACL.setRoleWriteAccess(householdRoleName, true);

                currentInstallation.setHousehold(currentUser.getActiveHousehold()); // Subscribe to your household channel
            }
        }

        ParseACL.setDefaultACL(defaultACL, false);

        currentInstallation.saveEventually();
    }

    private static String getHouseholdChannel(User currentUser) {
        return "household-" + currentUser.getActiveHousehold().getObjectId();
    }

    public String getDisplayName() {
        return getString(DISPLAY_NAME);
    }

    public void setDisplayName(String displayName) {
        put(DISPLAY_NAME, displayName);
    }

    public ParseFile getProfilePicture() {
        return getParseFile(PROFILE_PICTURE);
    }

    public void setProfilePicture(ParseFile profilePicture) {
        put(PROFILE_PICTURE, profilePicture);
    }

    public Household getActiveHousehold() {
        return (Household) getParseObject(ACTIVE_HOUSEHOLD);
    }

    private boolean isMemberOfAHousehold() {
        return this.getActiveHousehold() != null;
    }

    public void leaveHousehold(FunctionCallback<Object> callback) {
        if (this.isMemberOfAHousehold()) {
            String householdObjectId = getActiveHousehold().getObjectId();

            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("householdId", householdObjectId);

            ParseCloud.callFunctionInBackground(ParseCloudFunctionNames.LEAVE_HOUSEHOLD, params, callback);
        }
    }


}
