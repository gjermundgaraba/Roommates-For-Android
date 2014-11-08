package com.realkode.roomates.Feed.Events;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.realkode.roomates.ParseSubclassses.Event;
import com.realkode.roomates.R;

public class ShowEventDetailOnItemClickListener implements AdapterView.OnItemClickListener {
    private final Context context;

    public ShowEventDetailOnItemClickListener(Context context) {
        this.context = context;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View detailEventDialogView = inflater.inflate(R.layout.dialog_event_details, null);

        Event selectedEvent = (Event) adapterView.getItemAtPosition(position);

        TextView eventTextView = (TextView) detailEventDialogView.findViewById(R.id.eventDetailsTextView);
        eventTextView.setText(selectedEvent.getEventDescription());

        TextView eventExtraTextView = (TextView) detailEventDialogView.findViewById(R.id.event_detail_extra);
        eventExtraTextView.setText(selectedEvent.getCreatedAt().toString());

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder
                .setTitle(context.getString(R.string.title_event_details))
                .setView(detailEventDialogView);


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
