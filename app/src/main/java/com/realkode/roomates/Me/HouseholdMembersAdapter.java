package com.realkode.roomates.Me;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;

/**
 * Adapter for listing household members
 */
public class HouseholdMembersAdapter extends ParseQueryAdapter<User>  {

    public HouseholdMembersAdapter(Context context) {

        super(context, new ParseQueryAdapter.QueryFactory<User>() {
            public ParseQuery create() {
                ParseQuery query = new ParseQuery("_User");
                query.whereEqualTo("activeHousehold", ParseUser.getCurrentUser().getParseObject("activeHousehold"));
                query.orderByAscending("displayName");
                query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
                return query;
            }
        });
    }

    // Customize the layout by overriding getItemView
    @Override
    public View getItemView(User user, View view, ViewGroup parent) {
        if (view == null) {
            view = View.inflate(getContext(), R.layout.list_household_members_layout, null);
        }

        super.getItemView(user, view, parent);

        // Add and download the image
        ParseImageView profilePic = (ParseImageView) view.findViewById(R.id.icon);
        ParseFile imageFile = user.getProfilePicture();

        if (imageFile != null) {
            profilePic.setParseFile(imageFile);
            profilePic.loadInBackground();

        }


        // Add the title view
        TextView titleTextView = (TextView) view.findViewById(R.id.text1);
        titleTextView.setText(user.getDisplayName());
        titleTextView.setTextColor(Color.BLACK);
        TextView emailTextView = (TextView) view.findViewById(R.id.textViewUserEmail);
        emailTextView.setTextColor(Color.BLACK);
        emailTextView.setText(user.getEmail());
        return view;
    }
}
