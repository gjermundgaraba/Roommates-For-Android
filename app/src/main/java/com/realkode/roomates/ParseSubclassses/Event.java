package com.realkode.roomates.ParseSubclassses;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.ArrayList;

@ParseClassName("Event")
public class Event extends ParseObject {

    public static final String TYPE = "type";
    public static final String USER = "user";
    public static final String HOUSEHOLD = "household";
    public static final String OBJECTS = "objects";
    public static final int JOIN = 0;
    public static final int LEAVE = 1;
    public static final int CREATED_TASK_LIST = 2;
    public static final int FINISHED_TASK_LIST = 3;
    public static final int CREATED_EXPENSE = 4;
    public static final int SETTLED_EXPENSE = 5;

    Number getType() {
        return getNumber(TYPE);
    }

    User getUser() {
        return (User) getParseUser(USER);
    }

    Household getHousehold() {
        return (Household) getParseObject(HOUSEHOLD);
    }

    ArrayList<?> getObjects() {
        return new ArrayList(getList(OBJECTS));
    }

    public String getEventDescription() {
        int eventType = getType().intValue();
        String descriptionString;

        switch (eventType) {
            case JOIN:
                descriptionString = getUser().getDisplayName() + " joined " + getHousehold().getHouseholdName();
                break;
            case LEAVE:
                descriptionString = getUser().getDisplayName() + " left " + getHousehold().getHouseholdName();
                break;
            case CREATED_TASK_LIST:
                TaskList newTaskList = (TaskList) getObjects().get(0);
                if (newTaskList == null) {
                    return "<Task List Deleted>";
                }
                descriptionString = getUser().getDisplayName() + " created a new task list: " +
                        newTaskList.getListName();
                break;
            case FINISHED_TASK_LIST:
                TaskList finishedTaskList = (TaskList) getObjects().get(0);
                if (finishedTaskList == null) {
                    return "<Task List Deleted>";
                }
                descriptionString = getUser().getDisplayName() + " finished a task list: " +
                        finishedTaskList.getListName();
                break;
            case CREATED_EXPENSE:
                Expense newExpense = (Expense) getObjects().get(0);
                if (newExpense == null) {
                    return "<Expense Deleted>";
                }
                descriptionString = getUser().getDisplayName() + " created a new expense: " +
                        newExpense.getName();
                break;
            case SETTLED_EXPENSE:
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
