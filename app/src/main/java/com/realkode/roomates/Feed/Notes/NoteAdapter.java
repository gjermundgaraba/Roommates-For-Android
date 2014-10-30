package com.realkode.roomates.Feed.Notes;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.realkode.roomates.ParseSubclassses.Note;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;

public class NoteAdapter extends ParseQueryAdapter<Note> {

    private static final int NOTES_PER_PAGE = 4;

    public NoteAdapter(Context context) {
        super(context, new ParseQueryAdapter.QueryFactory<Note>() {
            public ParseQuery<Note> create() {
                ParseQuery<Note> query = new ParseQuery<Note>(Note.class);
                query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
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
