package com.gjermundbjaanes.apps.roommates2.parsesubclasses;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Note")
public class Note extends ParseObject {

    private static final String BODY = "body";
    private static final String CREATED_BY = "createdBy";
    private static final String HOUSEHOLD = "household";

    public String getBody() {
        return getString(BODY);
    }

    public void setBody(String body) {
        put(BODY, body);
    }

    public User getCreatedBy() {
        return (User) getParseUser(CREATED_BY);
    }

    public void setCreatedBy(User createdBy) {
        put(CREATED_BY, createdBy);
    }

    public Household getHousehold() {
        return (Household) getParseObject(HOUSEHOLD);
    }

    public void setHousehold(Household household) {
        put(HOUSEHOLD, household);
    }
}
