package com.realkode.roomates.Tasks;

import com.realkode.roomates.ParseSubclassses.TaskListElement;

public class TaskListElementEntryItem extends EntryItem {
    public final TaskListElement taskListElement;

    public TaskListElementEntryItem(String title, String subtitle, TaskListElement taskListElement) {
        super(title, subtitle);
        this.taskListElement = taskListElement;
    }
}

