package com.realkode.roomates.ParseSubclassses;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.ArrayList;

/**
 * Parse subclass for the "Expense" table.
 */
@ParseClassName("Expense")
public class Expense extends ParseObject {


    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        put("name", name);
    }

    public Number getTotalAmount() {
        return getNumber("totalAmount");
    }

    public void setTotalAmount(Double totalAmount) {
        put("totalAmount", totalAmount);
    }

    public User getOwed() {
        return (User) getParseUser("owed");
    }

    public void setOwed(User owed) {
        put("owed", owed);
    }

    public Household getHousehold() {
        return (Household) getParseObject("household");
    }

    public void setHousehold(Household household) {
        put("household", household);
    }

    public String getDetails() {
        return getString("details");
    }

    public void setDetails(String details) {
        put("details", details);
    }

    public ArrayList<User> getNotPaidUp() {
        return new ArrayList<User>(this.<User>getList("notPaidUp"));
    }

    public void setNotPaidUp(ArrayList<User> arrayList) {
        put("notPaidUp", arrayList);
    }

    public ArrayList<User> getPaidUp() {
        return new ArrayList<User>(this.<User>getList("paidUp"));
    }

    public void setPaidUp(ArrayList<User> arrayList) {
        put("paidUp", arrayList);
    }

    public int getNumberOfPeoplePaidUp() {
        return getPaidUp().size();
    }

    public int getNumberOfPeopleNotPaidUp() {
        return getNotPaidUp().size();
    }

    public int getNumberOfPeopleInExpense() {
        return getNumberOfPeoplePaidUp() + getNumberOfPeopleNotPaidUp();
    }
}
