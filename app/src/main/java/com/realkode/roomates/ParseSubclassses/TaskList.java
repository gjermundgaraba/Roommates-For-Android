package com.realkode.roomates.ParseSubclassses;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.Serializable;

@ParseClassName("TaskList")
public class TaskList extends ParseObject implements Serializable {

    public static final String LIST_NAME = "listName";
    public static final String HOUSEHOLD = "household";
    public static final String CREATED_BY = "createdBy";
    public static final String UPDATED_BY = "updatedBy";
    public static final String DONE = "done";

    public String getListName() {
        return getString(LIST_NAME);
    }

    public void setListName(String listName) {
        put(LIST_NAME, listName);
    }

    public Household getHousehold() {
        return (Household) getParseObject(HOUSEHOLD);
    }

    public void setHousehold(Household household) {
        put(HOUSEHOLD, household);
    }

    public User getCreatedBy() {
        return (User) getParseUser(CREATED_BY);
    }

    public void setCreatedBy(ParseUser createdBy) {
        put(CREATED_BY, createdBy);
    }

    public void setUpdatedBy(User updatedBy) {
        put(UPDATED_BY, updatedBy);
    }

    public Boolean getDone() {
        return getBoolean(DONE);
    }

    public void setDone(boolean done) {
        put(DONE, done);
    }
}
