package com.realkode.roomates.Me;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.RefreshCallback;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;

import java.util.HashMap;

public class CreateHouseholdOnClickListener implements DialogInterface.OnClickListener {
    Activity activityToFinish;
    Context context;
    EditText householdNameInput;

    CreateHouseholdOnClickListener(EditText householdNameInput, Activity activityToFinish) {
        this.activityToFinish = activityToFinish;
        this.context = activityToFinish;
        this.householdNameInput = householdNameInput;
    }

    @Override
    public void onClick(DialogInterface dialog, int id) {
        String householdName = householdNameInput.getText().toString();

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("householdName", householdName);
        final ProgressDialog createHouseholdProgress = ProgressDialog.show(context,
                context.getString(R.string.progress_dialog_title_creating_household),
                context.getString(R.string.progress_dialog_message_creating_household),
                true);
        ParseCloud.callFunctionInBackground("createNewHousehold", params,
                new FunctionCallback<Object>() {
                    @Override
                    public void done(Object obj, ParseException e) {
                        createHouseholdProgress.dismiss();
                        if (e == null) {
                            ToastMaker.makeShortToast(R.string.toast_household_created_successfully, context);
                            User.getCurrentUser().refreshInBackground(new RefreshCallback() {

                                @Override
                                public void done(ParseObject object, ParseException e) {
                                    User.refreshChannels();
                                    activityToFinish.finish();
                                }
                            });
                        } else {
                            ToastMaker.makeShortToast(R.string.toast_created_household_unsuccesfully, context);
                        }
                    }
                }
        );
    }
}
