package com.gjermundbjaanes.apps.roommates;

import com.gjermundbjaanes.apps.roommates.parsesubclasses.Event;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.Expense;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.Household;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.Installation;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.Invitation;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.Note;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.TaskList;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.TaskListElement;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.User;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.PushService;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initParse();
        initPush();
        initInstallation();
        registerSubclasses();
        initFacebook();
    }

    private void initParse() {
        Parse.initialize(this, "XXX", "XXX");
    }

    private void initPush() {
        PushService.setDefaultPushCallback(this, MainActivity.class);
    }

    private void initInstallation() {
        ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
        if (parseInstallation.getObjectId() != null) {
            parseInstallation.saveInBackground();
        }
    }

    private void initFacebook() {
        ParseFacebookUtils.initialize("XXX");
    }

    private void registerSubclasses() {
        ParseObject.registerSubclass(Expense.class);
        ParseObject.registerSubclass(TaskList.class);
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(TaskListElement.class);
        ParseObject.registerSubclass(Event.class);
        ParseObject.registerSubclass(Note.class);
        ParseObject.registerSubclass(Household.class);
        ParseObject.registerSubclass(Invitation.class);
        ParseObject.registerSubclass(Installation.class);
    }
}
