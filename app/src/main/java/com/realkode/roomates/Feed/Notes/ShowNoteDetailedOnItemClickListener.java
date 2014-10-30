package com.realkode.roomates.Feed.Notes;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.realkode.roomates.ParseSubclassses.Note;
import com.realkode.roomates.R;

public class ShowNoteDetailedOnItemClickListener implements AdapterView.OnItemClickListener {
    private final Context context;

    public ShowNoteDetailedOnItemClickListener(Context context) {
        this.context = context;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Note selectedNote = (Note) adapterView.getItemAtPosition(position);
        String title = context.getString(R.string.note_by) + selectedNote.getCreatedBy().getDisplayName();

        LayoutInflater inflater = LayoutInflater.from(context);
        View detailNoteDialogView = inflater.inflate(R.layout.dialog_note_details, null);
        TextView noteTextView = (TextView) detailNoteDialogView.findViewById(R.id.noteDetailsTextView);
        noteTextView.setText(context.getString(R.string.written) + " " + selectedNote.getCreatedAt().toString() +
                        "\n" + selectedNote.getBody());

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setTitle(title).setNegativeButton(context.getString(R.string.button_ok), null)
                .setView(detailNoteDialogView);


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
