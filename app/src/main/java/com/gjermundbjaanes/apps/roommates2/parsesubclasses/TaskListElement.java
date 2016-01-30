package com.gjermundbjaanes.apps.roommates2.parsesubclasses;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;

@ParseClassName("TaskListElement")
public class TaskListElement extends ParseObject {

    private static final String ELEMENT_NAME = "elementName";
    private static final String FINISHED_BY = "finishedBy";
    private static final String CREATED_BY = "createdBy";
    private static final String UPDATED_BY = "updatedBy";
    private static final String TASK_LIST = "taskList";

    public String getElementName() {
        return getString(ELEMENT_NAME);
    }

    public void setElementName(String elementName) {
        put(ELEMENT_NAME, elementName);
    }

    public void setTaskList(TaskList taskList) {
        put(TASK_LIST, taskList);
    }

    public User getCreatedBy() {
        try {
            return (User) getParseUser(CREATED_BY).fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (User) getParseUser(CREATED_BY);
    }

    public void setCreatedBy(User createdBy) {
        put(CREATED_BY, createdBy);
    }

    public void setUpdatedBy(User updatedBy) {
        put(UPDATED_BY, updatedBy);
    }

    public User getFinishedBy() {
        return (User) getParseUser(FINISHED_BY);
    }

    void setFinishedBy(User finishedBy) {
        put(FINISHED_BY, finishedBy);
    }

    public Boolean getDone() {
        return getFinishedBy() != null;
    }

    public void setDone(boolean done) {
        if (done) {
            setFinishedBy(User.getCurrentUser());
        } else {
            remove(FINISHED_BY);
        }
    }
}
