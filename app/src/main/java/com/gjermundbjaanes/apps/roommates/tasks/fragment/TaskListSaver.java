package com.gjermundbjaanes.apps.roommates.tasks.fragment;

import android.content.Context;

import com.gjermundbjaanes.apps.roommates.helpers.ToastMaker;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.TaskList;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.User;
import com.parse.SaveCallback;

public class TaskListSaver {
    private final SaveCallback saveCallback;
    private final Context context;

    public TaskListSaver(SaveCallback saveCallback, Context context) {
        this.saveCallback = saveCallback;
        this.context = context;
    }

    public void saveTaskList(String taskListName) {
        if (taskListName.isEmpty()) {
            ToastMaker.makeLongToast("Tasklist name cannot be empty", context);
        } else {
            TaskList taskList = new TaskList();
            taskList.setListName(taskListName);
            taskList.setDone(false);
            taskList.setCreatedBy(User.getCurrentUser());
            taskList.setHousehold(User.getCurrentUser().getActiveHousehold());
            taskList.setUpdatedBy(User.getCurrentUser());

            taskList.saveInBackground(saveCallback);
        }
    }
}
