package com.realkode.roomates.Tasks.Fragment;

import com.parse.SaveCallback;
import com.realkode.roomates.ParseSubclassses.TaskList;
import com.realkode.roomates.ParseSubclassses.User;

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
