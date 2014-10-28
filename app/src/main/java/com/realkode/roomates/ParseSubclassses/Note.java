package com.realkode.roomates.ParseSubclassses;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Parse subclass for the "Note" table.
 */
@ParseClassName("Note")
public class Note extends ParseObject {
    public String getBody() {
        return getString("body");
    }

    public void setBody(String body) {
        put("body", body);
    }

    public User getCreatedBy() {
        return (User) getParseUser("createdBy");
    }

    public void setCreatedBy(User createdBy) {
        put("createdBy", createdBy);
    }

    public Household getHousehold() {
        return (Household) getParseObject("household");
    }

    public void setHousehold(Household household) {
        put("household", household);
    }
}
