package com.gjermundbjaanes.apps.roommates2.parsesubclasses;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Household")
public class Household extends ParseObject {

    private static final String HOUSEHOLD_NAME = "householdName";

    public String getHouseholdName() {
        return getString(HOUSEHOLD_NAME);
    }
}
