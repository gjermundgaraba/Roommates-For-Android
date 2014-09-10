package com.realkode.roomates.Feed;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.realkode.roomates.ParseSubclassses.Event;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;

import java.util.List;

/**
 * The listadapter to view the event feed.
 */
public class FeedAdapter extends ParseQueryAdapter<Event> {

    public FeedAdapter(Context context) {
        super(context, new ParseQueryAdapter.QueryFactory<Event>() {
            public ParseQuery create() {
                ParseQuery query = new ParseQuery("Event");
                query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
                query.whereEqualTo("household", User.getCurrentUser().getActiveHousehold());
                query.include("household");
                query.include("user");
                query.include("objects");
                query.orderByDescending("createdAt");

                return query;
            }
        });
        // How many objects that shall be loaded on the screen before clicking load more.
        setObjectsPerPage(4);
    }

    // Customize the layout by overriding getItemView
    @Override
    public View getItemView(Event event, View view, ViewGroup parent) {
        view = View.inflate(getContext(), R.layout.list_event_feed_layout, null);

        //super.getItemView(event, view, parent);


        // Add the title view
        TextView titleTextView = (TextView) view.findViewById(R.id.eventFeedTextView);
        //titleTextView.setText(event.getDescription());
        titleTextView.setText(event.getDescriptionString());
        System.out.println("TextView Width: " + titleTextView.getWidth());
        return view;
    }

    // Load more elements in the feed.
    @Override
    public View getNextPageView(View v,ViewGroup parent){
        if (v == null)
            v = View.inflate(getContext(),R.layout.list_load_more,null);

//         v = super.getNextPageView(v,parent);

        TextView textView = (TextView) v.findViewById(R.id.loadMore);
        textView.setText("Load more...");
        System.out.println("TextView Width: " + textView.getWidth());
        return v;
    }


}