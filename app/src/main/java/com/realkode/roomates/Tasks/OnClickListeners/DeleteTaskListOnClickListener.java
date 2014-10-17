package com.realkode.roomates.Tasks.OnClickListeners;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.realkode.roomates.Helpers.Constants;
import com.realkode.roomates.ParseSubclassses.TaskList;
import com.realkode.roomates.R;

public class DeleteTaskListOnClickListener implements Dialog.OnClickListener {
    Activity activity;
    Context context;
    TaskList taskList;

    public DeleteTaskListOnClickListener(Activity activity, TaskList taskList) {
        this.activity = activity;
        this.context = activity;
        this.taskList = taskList;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        final ProgressDialog progressDialog = ProgressDialog.show(context,
                context.getString(R.string.deleting_list), context.getString(R.string.please_wait), true);

        taskList.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                progressDialog.dismiss();
                Intent intent = new Intent(Constants.NEED_TO_REFRESH);
                LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
                activity.finish();
            }
        });
    }
}
