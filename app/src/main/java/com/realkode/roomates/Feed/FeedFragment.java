package com.realkode.roomates.Feed;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.realkode.roomates.ParseSubclassses.Event;
import com.realkode.roomates.ParseSubclassses.Note;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;
import com.realkode.roomates.RefreshableFragment;


public class FeedFragment extends Fragment implements RefreshableFragment {

    private NoteAdapter noteAdapter;
    private FeedAdapter feedAdapter;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        // If user is not member of a household, no need to do much
        if (User.loggedInAndMemberOfAHousehold()) {
            setUpListViews();
        }

        return rootView;
    }

    private void setUpListViews() {
        ListView noteListView = (ListView) rootView.findViewById(R.id.noteListView);
        noteAdapter = new NoteAdapter(getActivity());
        noteListView.setAdapter(noteAdapter);
        noteListView.setOnItemClickListener(new ShowNoteDetailedOnItemClickListener());

        ListView eventListView = (ListView) rootView.findViewById(R.id.eventListView);
        feedAdapter = new FeedAdapter(getActivity());
        eventListView.setAdapter(feedAdapter);
        eventListView.setOnItemClickListener(new ShowEventDetailOnItemClickListener());
    }

    @Override
    public void onResume() {
        super.onResume();

        if (noteAdapter == null || feedAdapter == null) {
            setUpListViews();
        }
    }

    private class ShowEventDetailOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Event selectedEvent = (Event)adapterView.getItemAtPosition(position);

            LayoutInflater inflater = LayoutInflater.from(FeedFragment.this.getActivity());
            View detailEventDialogView = inflater.inflate(R.layout.dialog_event_details, null);
            TextView eventTextView = (TextView) detailEventDialogView.findViewById(R.id.eventDetailsTextView);
            eventTextView.setText(getString(R.string.event_happened) + selectedEvent.getCreatedAt().toString() + "\n" + selectedEvent.getDescriptionString());

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

            alertDialogBuilder.setTitle(getString(R.string.title_event_details))
                    .setNegativeButton(getString(R.string.button_ok), null)
                    .setView(detailEventDialogView);


            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    private class ShowNoteDetailedOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Note selectedNote = (Note)adapterView.getItemAtPosition(position);
            String title = getString(R.string.note_by) + selectedNote.getCreatedBy().getDisplayName();

            LayoutInflater inflater = LayoutInflater.from(FeedFragment.this.getActivity());
            View detailNoteDialogView = inflater.inflate(R.layout.dialog_note_details, null);
            TextView noteTextView = (TextView) detailNoteDialogView.findViewById(R.id.noteDetailsTextView);
            noteTextView.setText(getString(R.string.written) + selectedNote.getCreatedAt().toString() + "\n" + selectedNote.getBody());

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

            alertDialogBuilder.setTitle(title)
                    .setNegativeButton(getString(R.string.button_ok), null)
                    .setView(detailNoteDialogView);


            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }


    public void refreshFragment() {
        this.noteAdapter.loadObjects();
        this.feedAdapter.loadObjects();
    }

    public void startCreateNewNoteDialog() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View noteDialogView = inflater.inflate(R.layout.dialog_new_note, null);
        EditText noteEditText = (EditText) noteDialogView.findViewById(R.id.inviteUsernameEditText);
        Context context = getActivity();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        alertDialogBuilder.setTitle(context.getString(R.string.title_create_new_note))
                .setPositiveButton(context.getString(R.string.button_add_note), new AddNoteOnClickListener(noteEditText, noteAdapter, context))
                .setNegativeButton(context.getString(R.string.button_cancel), null)
                .setView(noteDialogView);


        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
    }
}