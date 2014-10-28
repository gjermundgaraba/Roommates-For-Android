package com.realkode.roomates.ParseSubclassses;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Invitation")
public class Invitation extends ParseObject {
    public Household getHousehold() {
        return (Household) getParseObject("household");
    }

    public void setHousehold(Household household) {
        put("household", household);
    }

    public User getInviter() {
        return (User) getParseUser("inviter");
    }
}
