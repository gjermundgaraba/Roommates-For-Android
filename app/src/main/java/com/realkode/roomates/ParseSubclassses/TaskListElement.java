package com.realkode.roomates.ParseSubclassses;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;

@ParseClassName("TaskListElement")
public class TaskListElement extends ParseObject {
    public String getElementName() {
        return getString("elementName");
    }

    public void setElementName(String elementName) {
        put("elementName", elementName);
    }

    public void setTaskList(TaskList taskList) {
        put("taskList", taskList);
    }

    public User getCreatedBy() {
        try {
            return (User) getParseUser("createdBy").fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (User) getParseUser("createdBy");
    }

    public void setCreatedBy(User createdBy) {
        put("createdBy", createdBy);
    }

    public void setUpdatedBy(User updatedBy) {
        put("updatedBy", updatedBy);
    }

    public User getFinishedBy() {
        return (User) getParseUser("finishedBy");
    }

    void setFinishedBy(User finishedBy) {
        put("finishedBy", finishedBy);
    }

    public Boolean getDone() {
        return getFinishedBy() != null;
    }

    public void setDone(boolean done) {
        if (done) {
            setFinishedBy(User.getCurrentUser());
        } else {
            remove("finishedBy");
        }
    }
}
