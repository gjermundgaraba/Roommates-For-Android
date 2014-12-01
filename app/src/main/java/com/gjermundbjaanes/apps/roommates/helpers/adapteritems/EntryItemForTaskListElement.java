package com.gjermundbjaanes.apps.roommates.helpers.adapteritems;

import com.gjermundbjaanes.apps.roommates.parsesubclasses.TaskListElement;

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

