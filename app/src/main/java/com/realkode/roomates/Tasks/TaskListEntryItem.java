package com.realkode.roomates.Tasks;

import com.realkode.roomates.ParseSubclassses.TaskList;

public class TaskListEntryItem extends EntryItem {
    public final TaskList taskList;

    public TaskListEntryItem(String title, String subtitle, TaskList taskList) {
        super(title, subtitle);
        this.taskList = taskList;
    }
}
