package com.gjermundbjaanes.apps.roommates.me.nohousehold;

import com.gjermundbjaanes.apps.roommates.helpers.ParseCloudFunctionNames;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;

import java.util.HashMap;

public class HouseholdCreator {
    private String householdName;

    public HouseholdCreator(String householdName) {
        this.householdName = householdName;
    }

    public void create(FunctionCallback<Object> callback) {

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("householdName", householdName);

        ParseCloud.callFunctionInBackground(ParseCloudFunctionNames.CREATE_NEW_HOUSEHOLD, params, callback);
    }
}
