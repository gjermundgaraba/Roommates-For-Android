package com.realkode.roomates.ParseSubclassses;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.ArrayList;

@ParseClassName("Event")
public class Event extends ParseObject {
    Number getType() {
        return getNumber("type");
    }

    User getUser() {
        return (User) getParseUser("user");
    }

    Household getHousehold() {
        return (Household) getParseObject("household");
    }

    ArrayList<?> getObjects() {
        // If problems, use a switch on getType.intValue
        return new ArrayList(getList("objects"));
    }

    // Get description for different types of events
    public String getDescriptionString() {
        int type = getType().intValue();
        String descriptionString;

        switch (type) {
            case 0: // Join
                descriptionString = getUser().getDisplayName() + " joined " + getHousehold().getHouseholdName();
                break;
            case 1: // Leave
                descriptionString = getUser().getDisplayName() + " left " + getHousehold().getHouseholdName();
                break;
            case 2: // created tasklist
                TaskList newTaskList = (TaskList) getObjects().get(0);
                if (newTaskList == null) {
                    return "<Task List Deleted>";
                }
                descriptionString = getUser().getDisplayName() + " created a new task list: " +
                        newTaskList.getListName();
                break;
            case 3: // finished taskList
                TaskList finishedTaskList = (TaskList) getObjects().get(0);
                if (finishedTaskList == null) {
                    return "<Task List Deleted>";
                }
                descriptionString = getUser().getDisplayName() + " finished a task list: " +
                        finishedTaskList.getListName();
                break;
            case 4: // created expense
                Expense newExpense = (Expense) getObjects().get(0);
                if (newExpense == null) {
                    return "<Expense Deleted>";
                }
                descriptionString = getUser().getDisplayName() + " created a new expense: " +
                        newExpense.getName();
                break;
            case 5: // settled expense
                Expense settledeExpense = (Expense) getObjects().get(0);
                if (settledeExpense == null) {
                    return "<Expense Deleted>";
                }
                descriptionString = getUser().getDisplayName() + " created a new expense: " +
                        settledeExpense.getName();
                break;
            default:
                descriptionString = "ERROR";
                break;
        }

        return descriptionString;
    }

}
