package com.realkode.roomates.Feed;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.SaveCallback;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.ParseSubclassses.Note;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;

public class AddNoteOnClickListener implements DialogInterface.OnClickListener {

    EditText noteEditText;
    Context context;
    NoteAdapter noteAdapter;

    AddNoteOnClickListener(EditText noteEditText, NoteAdapter noteAdapter, Context context) {
        this.noteEditText = noteEditText;
        this.noteAdapter = noteAdapter;
        this.context = context;
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        setUpAndSaveNote();
    }

    private void setUpAndSaveNote() {
        String noteText = noteEditText.getText().toString();
        User currentUser = User.getCurrentUser();

        ParseACL noteAcl = getNoteAcl(currentUser);

        if (!noteText.isEmpty()) {
            Note note = setUpNote(noteText, currentUser, noteAcl);
            saveNoteInBackground(note);
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

    private void saveNoteInBackground(Note note) {
        note.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException arg0) {
                ToastMaker.makeLongToast(context.getString(R.string.note_added_toast), context);

                noteAdapter.loadObjects();
            }
        });
    }
}
