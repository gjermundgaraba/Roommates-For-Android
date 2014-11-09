package com.realkode.roomates;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.PushService;
import com.realkode.roomates.ParseSubclassses.Event;
import com.realkode.roomates.ParseSubclassses.Expense;
import com.realkode.roomates.ParseSubclassses.Household;
import com.realkode.roomates.ParseSubclassses.Installation;
import com.realkode.roomates.ParseSubclassses.Invitation;
import com.realkode.roomates.ParseSubclassses.Note;
import com.realkode.roomates.ParseSubclassses.TaskList;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.ParseSubclassses.TaskListElement;

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
