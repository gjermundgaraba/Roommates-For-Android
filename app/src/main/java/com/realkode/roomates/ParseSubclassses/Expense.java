package com.realkode.roomates.ParseSubclassses;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.ArrayList;

/**
 * Parse subclass for the "Expense" table.
 */
@ParseClassName("Expense")
public class Expense extends ParseObject {

    private static final String NAME = "name";
    private static final String TOTAL_AMOUNT = "totalAmount";
    private static final String OWED = "owed";
    private static final String HOUSEHOLD = "household";
    private static final String DETAILS = "details";
    private static final String NOT_PAID_UP = "notPaidUp";
    private static final String PAID_UP = "paidUp";

    public String getName() {
        return getString(NAME);
    }

    public void setName(String name) {
        put(NAME, name);
    }

    public Number getTotalAmount() {
        return getNumber(TOTAL_AMOUNT);
    }

    public void setTotalAmount(Double totalAmount) {
        put(TOTAL_AMOUNT, totalAmount);
    }

    public User getOwed() {
        return (User) getParseUser(OWED);
    }

    public void setOwed(User owed) {
        put(OWED, owed);
    }

    public Household getHousehold() {
        return (Household) getParseObject(HOUSEHOLD);
    }

    public void setHousehold(Household household) {
        put(HOUSEHOLD, household);
    }

    public String getDetails() {
        return getString(DETAILS);
    }

    public void setDetails(String details) {
        put(DETAILS, details);
    }

    public ArrayList<User> getNotPaidUp() {
        return new ArrayList<User>(this.<User>getList(NOT_PAID_UP));
    }

    public void setNotPaidUp(ArrayList<User> arrayList) {
        put(NOT_PAID_UP, arrayList);
    }

    public ArrayList<User> getPaidUp() {
        return new ArrayList<User>(this.<User>getList(PAID_UP));
    }

    public void setPaidUp(ArrayList<User> arrayList) {
        put(PAID_UP, arrayList);
    }

    /* Helpers */

    int getNumberOfPeoplePaidUp() {
        return getPaidUp().size();
    }

    public int getNumberOfPeopleNotPaidUp() {
        return getNotPaidUp().size();
    }

    public int getNumberOfPeopleInExpense() {
        return getNumberOfPeoplePaidUp() + getNumberOfPeopleNotPaidUp();
    }

    public boolean currentUserIsOwed() {
        String owedUserObjectId = getOwed().getObjectId();
        String currentUserObjectId = User.getCurrentUser().getObjectId();
        return owedUserObjectId.equals(currentUserObjectId);
    }
}
