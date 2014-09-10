package com.realkode.roomates.ParseSubclassses;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Parse subclass for the "Invitation" table.
 */
@ParseClassName("Invitation")
public class Invitation extends ParseObject {
    public Household getHousehold() { return (Household)getParseObject("household"); }
    public void setHousehold(Household household) { put("household", household); }

    public User getInvitee() { return (User)getParseUser("invitee"); }
    public void setInvitee(User invitee) { put("invitee", invitee); }

    public User getInviter() { return (User)getParseUser("inviter"); }
    public void setInviter(User inviter) { put("inviter", inviter); }
}
