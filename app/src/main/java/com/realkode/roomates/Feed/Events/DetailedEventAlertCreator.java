package com.realkode.roomates.Feed.Events;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.realkode.roomates.ParseSubclassses.Event;
import com.realkode.roomates.R;

public class DetailedEventAlertCreator {

    public static AlertDialog create(Event event, Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View detailEventDialogView = inflater.inflate(R.layout.dialog_event_details, null);

        TextView eventTextView = (TextView) detailEventDialogView.findViewById(R.id.eventDetailsTextView);
        eventTextView.setText(event.getEventDescription());

        TextView eventExtraTextView = (TextView) detailEventDialogView.findViewById(R.id.event_detail_extra);
        eventExtraTextView.setText(event.getCreatedAt().toString());

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setTitle(context.getString(R.string.title_event_details));
        alertDialogBuilder.setView(detailEventDialogView);


        return alertDialogBuilder.create();
    }
}
