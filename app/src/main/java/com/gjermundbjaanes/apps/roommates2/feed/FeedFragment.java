package com.gjermundbjaanes.apps.roommates2.feed;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.gjermundbjaanes.apps.roommates2.AddBehaviourFragment;
import com.gjermundbjaanes.apps.roommates2.R;
import com.gjermundbjaanes.apps.roommates2.RefreshableFragment;
import com.gjermundbjaanes.apps.roommates2.feed.events.DetailedEventAlertCreator;
import com.gjermundbjaanes.apps.roommates2.feed.events.EventAdapter;
import com.gjermundbjaanes.apps.roommates2.feed.notes.DetailedNoteAlertCreator;
import com.gjermundbjaanes.apps.roommates2.feed.notes.NoteAdapter;
import com.gjermundbjaanes.apps.roommates2.feed.notes.NoteSaver;
import com.gjermundbjaanes.apps.roommates2.helpers.Constants;
import com.gjermundbjaanes.apps.roommates2.helpers.ToastMaker;
import com.gjermundbjaanes.apps.roommates2.parsesubclasses.Event;
import com.gjermundbjaanes.apps.roommates2.parsesubclasses.Note;
import com.parse.ParseException;
import com.parse.SaveCallback;


public class FeedFragment extends Fragment implements RefreshableFragment, AddBehaviourFragment {

    private NoteAdapter noteAdapter;
    private EventAdapter eventAdapter;
    private View rootView;
    private NoteSaver noteSaver = new NoteSaver(new NoteSaveCallback());

    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshFragment();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        setUpListViews();

        setUpBroadcastReceiver();

        return rootView;
    }

    private void setUpBroadcastReceiver() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        broadcastManager.registerReceiver(mMessageReceiver, new IntentFilter(Constants.NEED_TO_REFRESH));
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