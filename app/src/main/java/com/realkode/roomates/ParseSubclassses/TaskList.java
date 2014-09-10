package com.realkode.roomates.ParseSubclassses;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.Serializable;

/**
 * Parse subclass for the "TaskList" table.
 */
@ParseClassName("TaskList")
public class TaskList extends ParseObject implements Serializable{
    public String getListName() {
        return getString("listName");
    }

    public void setListName(String listName) {
        put("listName", listName);
    }

    public Household getHousehold() {
        return (Household)getParseObject("household");
    }

    public void setHousehold(Household household) {
        put("household", household);
    }

    public User getCreatedBy() {
        return (User)getParseUser("createdBy");
    }

    public void setCreatedBy(ParseUser createdBy) {
        put("createdBy", createdBy);
    }

    public User getUpdatedBy() {
        return (User)getParseUser("updatedBy");
    }

    public void setUpdatedBy(User updatedBy) {
        put("updatedBy", updatedBy);
    }

    public Boolean getDone() {
        return getBoolean("done");
    }

    public void setDone(boolean done) {
        put("done", done);
    }
}
