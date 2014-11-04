package com.realkode.roomates.Feed.Events;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.realkode.roomates.Helpers.Utils;
import com.realkode.roomates.ParseSubclassses.Event;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;

public class EventAdapter extends ParseQueryAdapter<Event> {

    private static final int EVENTS_PER_PAGE = 4;

    public EventAdapter(Context context) {
        super(context, new EventQueryFactory());

        setObjectsPerPage(EVENTS_PER_PAGE);
    }

    @Override
    public View getItemView(Event event, View view, ViewGroup parent) {
        view = View.inflate(getContext(), R.layout.list_event_feed_layout, null);

        TextView titleTextView = (TextView) view.findViewById(R.id.eventFeedTextView);
        titleTextView.setText(event.getEventDescription());
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


    private static class EventQueryFactory implements QueryFactory<Event> {
        public ParseQuery<Event> create() {
            ParseQuery<Event> query = new ParseQuery<Event>(Event.class);
            Utils.setSafeQueryCaching(query);
            query.whereEqualTo("household", User.getCurrentUser().getActiveHousehold());
            query.include("household");
            query.include("user");
            query.include("objects");
            query.orderByDescending("createdAt");

            return query;
        }
    }
}