package com.gjermundbjaanes.apps.roommates.feed.notes;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gjermundbjaanes.apps.roommates.R;
import com.gjermundbjaanes.apps.roommates.helpers.Utils;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.Note;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.User;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

public class NoteAdapter extends ParseQueryAdapter<Note> {

    private static final int NOTES_PER_PAGE = 4;

    public NoteAdapter(Context context) {
        super(context, new ParseQueryAdapter.QueryFactory<Note>() {
            public ParseQuery<Note> create() {
                ParseQuery<Note> query = new ParseQuery<Note>(Note.class);
                Utils.setSafeQueryCaching(query);
                query.include("createdBy");
                query.whereEqualTo("household", User.getCurrentUser().getActiveHousehold());
                query.orderByDescending("createdAt");
                return query;
            }
        });

        setObjectsPerPage(NOTES_PER_PAGE);
    }

    @Override
    public View getItemView(Note note, View view, ViewGroup parent) {
        if (view == null) {
            view = View.inflate(getContext(), R.layout.list_note_feed_layout, null);
        }

        TextView titleTextView = (TextView) view.findViewById(R.id.bodyTextView);
        titleTextView.setText(note.getBody());

        TextView createdByTextView = (TextView) view.findViewById(R.id.createdByTextView);
        createdByTextView.setText(note.getCreatedBy().getDisplayName());

        return view;
    }


    @Override
    public View getNextPageView(View view, ViewGroup parent) {
        if (view == null) {
            view = View.inflate(getContext(), R.layout.list_load_more, null);
        }

        TextView textView = (TextView) view.findViewById(R.id.loadMore);
        textView.setText(R.string.load_more);
        return view;
    }

}
