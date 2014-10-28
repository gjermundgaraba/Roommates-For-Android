package com.realkode.roomates.Me;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;

import com.parse.*;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.ParseSubclassses.Invitation;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;

import java.util.HashMap;

class AcceptInvitationOnClickListener implements View.OnClickListener {
    private final WithoutHouseholdActivity activity;
    private final Context context;

    AcceptInvitationOnClickListener(WithoutHouseholdActivity activity) {
        this.activity = activity;
        this.context = activity;
    }

    @Override
    public void onClick(View v) {
        acceptInvitation();
    }

    private void acceptInvitation() {
        Invitation selectedInvitation = activity.getSelectedInvitation();

        if (selectedInvitation != null) {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("invitationId", selectedInvitation.getObjectId());

            ProgressDialog acceptInvitationProgress = ProgressDialog
                    .show(context, activity.getString(R.string.progressdialog_title_accepting_invitation),
                            activity.getString(R.string.progressdialog_message_accepting_invitation), true);
            ParseCloud.callFunctionInBackground("acceptInvitation", params,
                    new AcceptInviteCallBack<Object>(acceptInvitationProgress));
        }
    }

    private class AcceptInviteCallBack<T> extends FunctionCallback<T> {
        final ProgressDialog progressDialog;

        AcceptInviteCallBack(ProgressDialog progressDialog) {
            super();
            this.progressDialog = progressDialog;
        }

        @Override
        public void done(Object household, ParseException e) {
            progressDialog.dismiss();
            if (e == null) {
                User.getCurrentUser().refreshInBackground(new RefreshCallback() {

                    @Override
                    public void done(ParseObject object, ParseException e) {
                        User.refreshChannels();
                        activity.finish();
                    }
                });
            } else {
                ToastMaker.makeShortToast(activity.getString(R.string.toast_could_not_accept_invitation), context);
            }
        }
    }
}
