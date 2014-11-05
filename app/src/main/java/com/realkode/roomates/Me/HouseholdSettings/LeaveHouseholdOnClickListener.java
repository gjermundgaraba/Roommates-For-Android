package com.realkode.roomates.Me.HouseholdSettings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.parse.FunctionCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.RefreshCallback;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;

class LeaveHouseholdOnClickListener implements View.OnClickListener {
    private final Context context;
    private final Activity activity;

    LeaveHouseholdOnClickListener(Activity activity) {
        this.context = activity;
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        leave_household_dialog();
    }

    private void leave_household_dialog() {

        final AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(context);
        myAlertDialog.setTitle(R.string.dialog_title_leave_household)
                .setMessage(R.string.dialog_message_leave_household)
                .setPositiveButton(R.string.button_confirm_leave_household, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                leaveHousehold();
                            }
                        });

        myAlertDialog.show();


    }

    private void leaveHousehold() {
        final ProgressDialog leaveHouseholdProgress = ProgressDialog
                .show(context, context.getString(R.string.leaving_household), context.getString(R.string.please_wait),
                        true);

        User.getCurrentUser().leaveHousehold(new FunctionCallback<Object>() {
            @Override
            public void done(Object o, ParseException exception) {
                leaveHouseholdProgress.dismiss();
                if (exception == null) {
                    final ProgressDialog refreshUserProgress = ProgressDialog.show(context, context.getString(R.string.refreshing_user), context.getString(R.string.please_wait));
                    User.getCurrentUser().refreshInBackground(new RefreshCallback() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            refreshUserProgress.dismiss();
                            User.refreshChannels();
                            activity.finish();
                        }
                    });
                } else {
                    ToastMaker.makeShortToast(context.getString(R.string.warning_could_not_leave_household), context);
                }
            }
        });
    }
}
