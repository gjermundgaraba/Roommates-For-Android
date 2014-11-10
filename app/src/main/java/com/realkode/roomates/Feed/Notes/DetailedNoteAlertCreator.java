package com.realkode.roomates.Feed.Notes;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.realkode.roomates.ParseSubclassses.Note;
import com.realkode.roomates.R;

public class DetailedNoteAlertCreator {

    public static AlertDialog create(Note note, Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View detailNoteDialogView = inflater.inflate(R.layout.dialog_note_details, null);

        TextView noteTextView = (TextView) detailNoteDialogView.findViewById(R.id.noteDetailsTextView);
        noteTextView.setText(note.getBody());

        TextView extraInfo = (TextView) detailNoteDialogView.findViewById(R.id.note_detail_extra);
        extraInfo.setText(context.getString(R.string.written) + " " + note.getCreatedAt().toString());

        String title = context.getString(R.string.note_by) + " " + note.getCreatedBy().getDisplayName();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setView(detailNoteDialogView);

        return alertDialogBuilder.create();
    }
}
