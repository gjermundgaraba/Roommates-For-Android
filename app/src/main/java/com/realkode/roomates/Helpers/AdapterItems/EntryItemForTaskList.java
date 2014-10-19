package com.realkode.roomates.Helpers.AdapterItems;

import com.realkode.roomates.ParseSubclassses.TaskList;

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
