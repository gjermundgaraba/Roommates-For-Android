package com.realkode.roomates.Tasks.OnClickListeners;


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
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;

public class CreateNewTaskListOnClickListener implements DialogInterface.OnClickListener {
    Context context;
    EditText taskListNameInput;

    public CreateNewTaskListOnClickListener(Context context, EditText taskListNameInput) {
        this.context = context;
        this.taskListNameInput = taskListNameInput;
    }

    @Override
    public void onClick(DialogInterface dialog, int id) {
        TaskList taskList = new TaskList();
        String taskListName = taskListNameInput.getText().toString();
        taskList.setListName(taskListName);
        taskList.setDone(false);
        taskList.setCreatedBy(User.getCurrentUser());
        taskList.setHousehold(User.getCurrentUser().getActiveHousehold());
        taskList.setUpdatedBy(User.getCurrentUser());

        taskList.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Intent intent = new Intent(Constants.NEED_TO_REFRESH);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                ToastMaker.makeShortToast(R.string.toast_new_task_list_added, context);
            }
        });


    }
}
