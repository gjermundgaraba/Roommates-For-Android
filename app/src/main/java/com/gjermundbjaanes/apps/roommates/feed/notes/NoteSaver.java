package com.gjermundbjaanes.apps.roommates.feed.notes;

import com.gjermundbjaanes.apps.roommates.parsesubclasses.Note;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.User;
import com.parse.ParseACL;
import com.parse.SaveCallback;

public class NoteSaver {
    private final SaveCallback saveCallback;

    public NoteSaver(SaveCallback saveCallback) {
        this.saveCallback = saveCallback;
    }

    public void saveNote(String noteText) {
        User currentUser = User.getCurrentUser();

        ParseACL noteAcl = getNoteAcl(currentUser);

        if (!noteText.isEmpty()) {
            Note note = setUpNote(noteText, currentUser, noteAcl);
            note.saveInBackground(saveCallback);
        }
    }

    private ParseACL getNoteAcl(User currentUser) {
        ParseACL acl = new ParseACL();
        acl.setRoleReadAccess("household-" + currentUser.getActiveHousehold().getObjectId(), true);
        return acl;
    }

    private Note setUpNote(String noteText, User currentUser, ParseACL acl) {
        Note note = new Note();
        note.setCreatedBy(currentUser);
        note.setBody(noteText);
        note.setHousehold(currentUser.getActiveHousehold());
        note.setACL(acl);

        return note;
    }

}
