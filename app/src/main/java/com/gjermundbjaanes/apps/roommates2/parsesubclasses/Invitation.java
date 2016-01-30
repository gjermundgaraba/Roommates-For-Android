package com.gjermundbjaanes.apps.roommates2.parsesubclasses;

import com.gjermundbjaanes.apps.roommates2.helpers.ParseCloudFunctionNames;
import com.parse.FunctionCallback;
import com.parse.ParseClassName;
import com.parse.ParseCloud;
import com.parse.ParseObject;

import java.util.HashMap;

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

    public void accept(FunctionCallback<Object> functionCallback) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("invitationId", getObjectId());

        ParseCloud.callFunctionInBackground(ParseCloudFunctionNames.ACCEPT_INVITATION, params, functionCallback);
    }
}
