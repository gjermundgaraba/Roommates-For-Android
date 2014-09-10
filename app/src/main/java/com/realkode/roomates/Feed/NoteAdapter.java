package com.realkode.roomates.Feed;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.realkode.roomates.ParseSubclassses.Note;
import com.realkode.roomates.R;

import java.util.List;

/**
 * Adapter to the list with notes
 */
public class NoteAdapter extends ParseQueryAdapter<Note>  {

    public NoteAdapter(Context context) {
        super(context, new ParseQueryAdapter.QueryFactory<Note>() {
            public ParseQuery create() {
                ParseQuery query = new ParseQuery("Note");
                query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
                query.include("createdBy");
                query.whereEqualTo("household", ParseUser.getCurrentUser().getParseObject("activeHousehold"));
                query.orderByDescending("createdAt");
                return query;
            }
        });
        // Number of list items to load before clicking load more.
        setObjectsPerPage(4);
    }

    // Customize the layout by overriding getItemView
    @Override
    public View getItemView(Note note, View view, ViewGroup parent) {
        if (view == null) {
            view = View.inflate(getContext(), R.layout.list_note_feed_layout, null);

        }

        super.getItemView(note, view, parent);


        // Add the title view
        TextView titleTextView = (TextView) view.findViewById(R.id.bodyTextView);
        titleTextView.setText(note.getBody());

        TextView createdByTextView = (TextView) view.findViewById(R.id.createdByTextView);
        createdByTextView.setText(note.getCreatedBy().getDisplayName());




        return view;
    }



    @Override
    public View getNextPageView(View v,ViewGroup parent){
        if (v == null)
            v = View.inflate(getContext(),R.layout.list_load_more,null);

//         v = super.getNextPageView(v,parent);

        TextView textView = (TextView) v.findViewById(R.id.loadMore);
        textView.setText("Load more...");
        return v;
    }

}
