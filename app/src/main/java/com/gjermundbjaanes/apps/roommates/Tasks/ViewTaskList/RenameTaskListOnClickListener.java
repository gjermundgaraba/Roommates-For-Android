package com.gjermundbjaanes.apps.roommates.tasks.viewtasklist;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.EditText;

import com.gjermundbjaanes.apps.roommates.R;
import com.gjermundbjaanes.apps.roommates.helpers.Constants;
import com.gjermundbjaanes.apps.roommates.helpers.ToastMaker;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.TaskList;
import com.parse.ParseException;
import com.parse.SaveCallback;

public class RenameTaskListOnClickListener implements DialogInterface.OnClickListener {
    private final Activity activity;
    private final Context context;
    private final TaskList taskList;
    private final EditText listNameField;

    public RenameTaskListOnClickListener(Activity activity, TaskList taskList, EditText listNameField) {
        this.activity = activity;
        this.context = activity;
        this.taskList = taskList;
        this.listNameField = listNameField;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        String taskListName = listNameField.getText().toString();
        taskList.setListName(taskListName);
        activity.setTitle(taskListName);

        taskList.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                ToastMaker.makeShortToast(R.string.task_list_name_changed, context);

                Intent intent = new Intent(Constants.EXPENSE_NEED_TO_REFRESH);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });
    }
}
