package com.realkode.roomates.Me.NoHousehold;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.realkode.roomates.Helpers.ParseCloudFunctionNames;

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
