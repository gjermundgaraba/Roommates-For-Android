package com.gjermundbjaanes.apps.roommates2.helpers.adapteritems;

import com.gjermundbjaanes.apps.roommates2.parsesubclasses.TaskList;

public class EntryItemForTaskList extends EntryItem {
    private final TaskList taskList;

    public EntryItemForTaskList(String title, String subtitle, TaskList taskList) {
        super(title, subtitle);
        this.taskList = taskList;
    }

    public TaskList getTaskList() {
        return taskList;
    }
}
