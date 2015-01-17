package com.gjermundbjaanes.apps.roommates.me.householdsettings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gjermundbjaanes.apps.roommates.R;
import com.gjermundbjaanes.apps.roommates.helpers.Constants;
import com.gjermundbjaanes.apps.roommates.helpers.ParseCloudFunctionNames;
import com.gjermundbjaanes.apps.roommates.helpers.ToastMaker;
import com.gjermundbjaanes.apps.roommates.helpers.Utils;
import com.gjermundbjaanes.apps.roommates.me.HouseholdMembersAdapter;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.Household;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.User;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.RefreshCallback;

import java.util.HashMap;


public class MyHouseholdActivity extends Activity {
    private TextView textViewHouseholdName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_household_active);

        textViewHouseholdName = (TextView) findViewById(R.id.textViewHouseholdName);

        Button leaveHouseholdBtn = (Button) findViewById(R.id.buttonLeaveHousehold);
        leaveHouseholdBtn.setOnClickListener(new LeaveHouseholdOnClickListener());

        Button inviteUserBtn = (Button) findViewById(R.id.buttonInvite);
        inviteUserBtn.setOnClickListener(new InviteUserOnClickListener());

        ListView membersListView = (ListView) findViewById(R.id.householdMembersListView);
        HouseholdMembersAdapter membersListViewAdapter = new HouseholdMembersAdapter(this);
        membersListView.setAdapter(membersListViewAdapter);

        setHouseholdNameTitle();
    }

    private void setHouseholdNameTitle() {
        ParseQuery<Household> query = new ParseQuery<Household>(Household.class);
        Utils.setSafeQueryCaching(query);
        String householdObjectId = User.getCurrentUser().getActiveHousehold().getObjectId();
        query.getInBackground(householdObjectId, new GetCallback<Household>() {
            @Override
            public void done(Household household, ParseException e) {
                textViewHouseholdName.setText(household.getHouseholdName());
            }
        });
    }

    private class LeaveHouseholdOnClickListener implements View.OnClickListener {
        Context context = MyHouseholdActivity.this;

        @Override
        public void onClick(View v) {
            showLeaveHouseholdDialog();
        }

        private void showLeaveHouseholdDialog() {
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
                                Intent intent = new Intent(Constants.NEED_TO_REFRESH);
                                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                                MyHouseholdActivity.this.finish();
                            }
                        });
                    } else {
                        ToastMaker.makeShortToast(R.string.warning_could_not_leave_household, context);
                    }
                }
            });
        }
    }

    private class InviteUserOnClickListener implements View.OnClickListener {
        private final Context context = MyHouseholdActivity.this;

        @Override
        public void onClick(View v) {
            inviteUserToHousehold();
        }

        private void inviteUserToHousehold() {
            LayoutInflater inflater = LayoutInflater.from(context);
            View inviteRoommateDialog = inflater.inflate(R.layout.dialog_invite_roommate, null);

            final EditText inviteField = (EditText) inviteRoommateDialog.findViewById(R.id.inviteUsernameEditText);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle(R.string.dialog_title_invite_user)
                    .setPositiveButton(R.string.dialog_button_confirm_invite, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            inviteRoommate(inviteField.getText().toString());
                        }
                    })
                    .setView(inviteRoommateDialog);

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            alertDialog.show();
        }

        public void inviteRoommate(String inviteeUsername) {
            String householdObjectId = User.getCurrentUser().getActiveHousehold().getObjectId();

            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("username", inviteeUsername);
            params.put("householdId", householdObjectId);
            final ProgressDialog inviteProgress = ProgressDialog
                    .show(context, context.getString(R.string.progressdialog_title_inviting_user),
                            context.getString(R.string.progressdialog_message_inviting_user), true);

            ParseCloud.callFunctionInBackground(ParseCloudFunctionNames.INVITE_USER_TO_HOUSEHOLD, params, new FunctionCallback<Object>() {
                @Override
                public void done(Object noReturnValue, ParseException e) {
                    inviteProgress.dismiss();
                    if (e == null) {
                        ToastMaker.makeShortToast(R.string.toast_user_was_invited, context);

                    } else {
                        CharSequence text = e.getMessage();
                        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}


