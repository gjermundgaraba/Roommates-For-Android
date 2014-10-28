package com.realkode.roomates.Expenses.EditPeople;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;

import java.util.ArrayList;

class HouseholdMembersAdapterEditExpense extends ParseQueryAdapter<User> {
    private final ArrayList<String> objectIDs;

    public HouseholdMembersAdapterEditExpense(Context context, ArrayList<String> objectIDs) {

        super(context, new ParseQueryAdapter.QueryFactory<User>() {

            public ParseQuery<User> create() {
                ParseQuery<User> query = new ParseQuery<User>(User.class);
                query.whereEqualTo("activeHousehold", User.getCurrentUser().getActiveHousehold());
                query.orderByAscending("displayName");
                return query;
            }
        });
        this.objectIDs = objectIDs;
    }

    @Override
    public View getItemView(User user, View view, ViewGroup parent) {
        if (view == null) {
            view = View.inflate(getContext(), R.layout.list_household_members_layout_edit_expense, null);
        }

        super.getItemView(user, view, parent);

        // Add and download the image
        ParseImageView profilePic = (ParseImageView) view.findViewById(R.id.icon);
        ParseFile imageFile = user.getProfilePicture();

        if (imageFile != null) {
            profilePic.setParseFile(imageFile);
            profilePic.loadInBackground();

        }

        // Trying to set the background of the list element gray if user is already member of expense
        if (objectIDs.contains(user.getObjectId())) {
            RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.relativeLayout1);
            layout.setBackgroundColor(Color.LTGRAY);
        }

        // Set the list elements properties
        TextView titleTextView = (TextView) view.findViewById(R.id.text1);
        titleTextView.setText(user.getDisplayName());
        titleTextView.setTextColor(Color.BLACK);
        TextView emailTextView = (TextView) view.findViewById(R.id.textViewUserEmail);
        emailTextView.setTextColor(Color.BLACK);
        emailTextView.setText(user.getEmail());

        return view;
    }
}
