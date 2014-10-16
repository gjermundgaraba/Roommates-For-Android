package com.realkode.roomates.Tasks.OnClickListeners;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.SaveCallback;
import com.realkode.roomates.Helpers.Constants;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.ParseSubclassses.TaskList;
import com.realkode.roomates.R;

public class RenameTaskListOnClickListener implements DialogInterface.OnClickListener {
    private Activity activity;
    private Context context;
    private TaskList taskList;
    private EditText listNameField;

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
