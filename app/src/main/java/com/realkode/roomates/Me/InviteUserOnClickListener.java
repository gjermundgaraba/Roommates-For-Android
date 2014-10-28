package com.realkode.roomates.Me;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;

import java.util.HashMap;
import java.util.Locale;

class InviteUserOnClickListener implements View.OnClickListener {
    private final Context context;
    private View inviteRoommateDialog;

    InviteUserOnClickListener(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        inviteUserToHousehold();
    }

    private void inviteUserToHousehold() {
        LayoutInflater inflater = LayoutInflater.from(context);
        inviteRoommateDialog = inflater.inflate(R.layout.dialog_invite_roommate, null);


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(R.string.dialog_title_invite_user)
                .setPositiveButton(R.string.dialog_button_confirm_invite, new InviteDialogOnClickListener())
                .setView(inviteRoommateDialog);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
    }

    private class InviteDialogOnClickListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            final EditText inviteeEditText = (EditText) inviteRoommateDialog.findViewById(R.id.inviteUsernameEditText);
            String inviteeUsername = inviteeEditText.getText().toString().toLowerCase(Locale.ENGLISH);
            String householdObjectId = User.getCurrentUser().getActiveHousehold().getObjectId();

            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("username", inviteeUsername);
            params.put("householdId", householdObjectId);
            final ProgressDialog inviteProgress = ProgressDialog
                    .show(context, context.getString(R.string.progressdialog_title_inviting_user),
                            context.getString(R.string.progressdialog_message_inviting_user), true);

            ParseCloud.callFunctionInBackground("inviteUserToHousehold", params, new FunctionCallback<Object>() {
                        @Override
                        public void done(Object noReturnValue, ParseException e) {
                            inviteProgress.dismiss();
                            if (e == null) {
                                ToastMaker.makeShortToast(context.getString(R.string.toast_user_was_invited), context);

                            } else {
                                CharSequence text = e.getMessage();
                                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }
}
