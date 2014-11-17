package com.realkode.roomates.Feed;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.SaveCallback;
import com.realkode.roomates.AddBehaviourFragment;
import com.realkode.roomates.Feed.Events.DetailedEventAlertCreator;
import com.realkode.roomates.Feed.Events.EventAdapter;
import com.realkode.roomates.Feed.Notes.DetailedNoteAlertCreator;
import com.realkode.roomates.Feed.Notes.NoteAdapter;
import com.realkode.roomates.Feed.Notes.NoteSaver;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.ParseSubclassses.Event;
import com.realkode.roomates.ParseSubclassses.Note;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;
import com.realkode.roomates.RefreshableFragment;


public class FeedFragment extends Fragment implements RefreshableFragment, AddBehaviourFragment {

    private NoteAdapter noteAdapter;
    private EventAdapter eventAdapter;
    private View rootView;
    private NoteSaver noteSaver = new NoteSaver(new NoteSaveCallback());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        if (User.loggedInAndMemberOfAHousehold()) {
            setUpListViews();
        }

        return rootView;
    }

    private void setUpListViews() {
        setUpNoteListView();
        setUpEventListView();
    }

    private void setUpEventListView() {
        ListView eventListView = (ListView) rootView.findViewById(R.id.eventListView);
        eventAdapter = new EventAdapter(getActivity());
        eventListView.setAdapter(eventAdapter);
        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event selectedEvent = (Event) parent.getItemAtPosition(position);
                DetailedEventAlertCreator.create(selectedEvent, getActivity()).show();
            }
        });
    }

    private void setUpNoteListView() {
        ListView noteListView = (ListView) rootView.findViewById(R.id.noteListView);
        noteAdapter = new NoteAdapter(getActivity());
        noteListView.setAdapter(noteAdapter);
        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note selectedNote = (Note) parent.getItemAtPosition(position);
                DetailedNoteAlertCreator.create(selectedNote, getActivity()).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (noteAdapter == null || eventAdapter == null) {
            setUpListViews();
        }
    }


    public void refreshFragment() {
        this.noteAdapter.loadObjects();
        this.eventAdapter.loadObjects();
    }

    private void startCreateNewNoteDialog() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View noteDialogView = inflater.inflate(R.layout.dialog_new_note, null);

        final EditText noteField = (EditText) noteDialogView.findViewById(R.id.inviteUsernameEditText);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        alertDialogBuilder
                .setTitle(R.string.title_create_new_note)
                .setPositiveButton(R.string.button_add_note, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String noteText = noteField.getText().toString();
                        noteSaver.saveNote(noteText);
                    }
                })
                .setNegativeButton(R.string.button_cancel, null)
                .setView(noteDialogView);


        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
    }

    @Override
    public void add() {
        startCreateNewNoteDialog();
    }

    private class NoteSaveCallback extends SaveCallback {

        @Override
        public void done(ParseException e) {
            noteAdapter.loadObjects();
            ToastMaker.makeLongToast(R.string.note_added_toast, getActivity());
        }
    }
}