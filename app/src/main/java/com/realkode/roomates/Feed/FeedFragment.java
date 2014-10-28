package com.realkode.roomates.Feed;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;

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

        if (User.loggedInAndMemberOfAHousehold()) {
            setUpListViews();
        }

        return rootView;
    }

    private void setUpListViews() {
        ListView noteListView = (ListView) rootView.findViewById(R.id.noteListView);
        noteAdapter = new NoteAdapter(getActivity());
        noteListView.setAdapter(noteAdapter);
        noteListView.setOnItemClickListener(new ShowNoteDetailedOnItemClickListener(getActivity()));

        ListView eventListView = (ListView) rootView.findViewById(R.id.eventListView);
        feedAdapter = new FeedAdapter(getActivity());
        eventListView.setAdapter(feedAdapter);
        eventListView.setOnItemClickListener(new ShowEventDetailOnItemClickListener(getActivity()));
    }

    @Override
    public void onResume() {
        super.onResume();

        if (noteAdapter == null || feedAdapter == null) {
            setUpListViews();
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

        alertDialogBuilder.setTitle(R.string.title_create_new_note).setPositiveButton(R.string.button_add_note,
                new AddNoteOnClickListener(noteEditText, noteAdapter, context))
                .setNegativeButton(R.string.button_cancel, null).setView(noteDialogView);


        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
    }
}