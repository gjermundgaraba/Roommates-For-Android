package com.gjermundbjaanes.apps.roommates.me.nohousehold;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gjermundbjaanes.apps.roommates.R;
import com.gjermundbjaanes.apps.roommates.helpers.Utils;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.Invitation;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.User;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

/**
 * List adapter for the invitations
 */
class InvitationAdapter extends ParseQueryAdapter<Invitation> {
    public InvitationAdapter(Context context) {

        super(context, new ParseQueryAdapter.QueryFactory<Invitation>() {
            public ParseQuery<Invitation> create() {
                ParseQuery<Invitation> query = new ParseQuery<Invitation>(Invitation.class);
                Utils.setSafeQueryCaching(query);
                query.whereEqualTo("invitee", User.getCurrentUser());
                query.include("inviter");
                query.include("household");
                query.orderByDescending("createdAt");

                return query;
            }
        });
    }

    @Override
    public View getItemView(Invitation invitation, View view, ViewGroup parent) {
        if (view == null) {
            view = View.inflate(getContext(), R.layout.list_invitations_layout, null);
        }

        super.getItemView(invitation, view, parent);
        TextView invitedByTextView = (TextView) view.findViewById(R.id.invitedByTextView);
        invitedByTextView.setText(invitation.getInviter().getDisplayName());

        TextView householdTextView = (TextView) view.findViewById(R.id.householdTextView);
        householdTextView.setText(invitation.getHousehold().getHouseholdName());

        return view;
    }


}
