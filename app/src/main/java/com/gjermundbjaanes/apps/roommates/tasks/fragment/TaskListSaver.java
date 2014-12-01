package com.gjermundbjaanes.apps.roommates.tasks.fragment;

import com.gjermundbjaanes.apps.roommates.parsesubclasses.TaskList;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.User;
import com.parse.SaveCallback;

public class TaskListSaver {
    private final SaveCallback saveCallback;

    public TaskListSaver(SaveCallback saveCallback) {
        this.saveCallback = saveCallback;
    }

    public void saveTaskList(String taskListName) {
        TaskList taskList = new TaskList();
        taskList.setListName(taskListName);
        taskList.setDone(false);
        taskList.setCreatedBy(User.getCurrentUser());
        taskList.setHousehold(User.getCurrentUser().getActiveHousehold());
        taskList.setUpdatedBy(User.getCurrentUser());

        taskList.saveInBackground(saveCallback);
    }
}
