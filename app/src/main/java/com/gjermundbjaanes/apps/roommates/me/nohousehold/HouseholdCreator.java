package com.gjermundbjaanes.apps.roommates.me.nohousehold;

import android.app.ProgressDialog;
import android.content.Context;

import com.gjermundbjaanes.apps.roommates.R;
import com.gjermundbjaanes.apps.roommates.helpers.ParseCloudFunctionNames;
import com.gjermundbjaanes.apps.roommates.helpers.ToastMaker;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;

import java.util.HashMap;

public class HouseholdCreator {
    private String householdName;
    private Context context;

    public HouseholdCreator(String householdName, Context context) {
        this.householdName = householdName;
        this.context = context;
    }

    public void create(FunctionCallback<Object> callback, ProgressDialog progressDialog) {

        if (householdName.trim().isEmpty()) {
            ToastMaker.makeShortToast(R.string.household_name_cannot_be_empty, context);
            progressDialog.dismiss();
        } else {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("householdName", householdName);

            ParseCloud.callFunctionInBackground(ParseCloudFunctionNames.CREATE_NEW_HOUSEHOLD, params, callback);
        }
    }
}
