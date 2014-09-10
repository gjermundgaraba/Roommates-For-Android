package com.realkode.roomates;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.realkode.roomates.ParseSubclassses.Event;
import com.realkode.roomates.ParseSubclassses.Expense;
import com.realkode.roomates.ParseSubclassses.Household;
import com.realkode.roomates.ParseSubclassses.Installation;
import com.realkode.roomates.ParseSubclassses.Invitation;
import com.realkode.roomates.ParseSubclassses.Note;
import com.realkode.roomates.ParseSubclassses.TaskList;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.ParseSubclassses.TaskListElement;

/**
 * Application-class that is called before anything else happens in the app.
 * Initalizing the Parse-connection and the Facebook-connection.
 * This is also called when application opens in background.
 */
public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        // Set up Parse and Push
        ParseObject.registerSubclass(Expense.class);
        ParseObject.registerSubclass(TaskList.class);
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(TaskListElement.class);
        ParseObject.registerSubclass(Event.class);
        ParseObject.registerSubclass(Note.class);
        ParseObject.registerSubclass(Household.class);
        ParseObject.registerSubclass(Invitation.class);
        ParseObject.registerSubclass(Installation.class);
        Parse.initialize(this, "XXX",
                "XXX");
        ParseFacebookUtils.initialize("XXX");
        super.onCreate();
    }
}
