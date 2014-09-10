package com.realkode.roomates.ParseSubclassses;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Parse subclass for the "Household" table.
 */
@ParseClassName("Household")
public class Household extends ParseObject {
    public String getHouseholdName() { return getString("householdName"); }
    public void setHouseholdName(String householdName) { put("householdName", householdName); }
}
