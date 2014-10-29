package com.realkode.roomates.Feed;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.realkode.roomates.ParseSubclassses.Event;
import com.realkode.roomates.R;

class ShowEventDetailOnItemClickListener implements AdapterView.OnItemClickListener {
    private final Context context;

    ShowEventDetailOnItemClickListener(Context context) {
        this.context = context;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Event selectedEvent = (Event) adapterView.getItemAtPosition(position);

        LayoutInflater inflater = LayoutInflater.from(context);
        View detailEventDialogView = inflater.inflate(R.layout.dialog_event_details, null);
        TextView eventTextView = (TextView) detailEventDialogView.findViewById(R.id.eventDetailsTextView);
        eventTextView.setText(context.getString(R.string.event_happened) + " " +
                selectedEvent.getCreatedAt().toString() + "\n" +
                selectedEvent.getEventDescription());

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setTitle(context.getString(R.string.title_event_details))
                .setNegativeButton(context.getString(R.string.button_ok), null).setView(detailEventDialogView);


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
