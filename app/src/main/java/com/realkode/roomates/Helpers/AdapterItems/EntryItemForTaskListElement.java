package com.realkode.roomates.Helpers.AdapterItems;

import com.realkode.roomates.ParseSubclassses.TaskListElement;

public class EntryItemForTaskListElement extends EntryItem {
    private final TaskListElement taskListElement;

    public EntryItemForTaskListElement(String title, String subtitle, TaskListElement taskListElement) {
        super(title, subtitle);
        this.taskListElement = taskListElement;
    }

    public TaskListElement getTaskListElement() {
        return taskListElement;
    }
}

