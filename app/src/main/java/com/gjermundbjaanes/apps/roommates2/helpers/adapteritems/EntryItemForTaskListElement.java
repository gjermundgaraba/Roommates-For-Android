package com.gjermundbjaanes.apps.roommates2.helpers.adapteritems;

import com.gjermundbjaanes.apps.roommates2.parsesubclasses.TaskListElement;

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

