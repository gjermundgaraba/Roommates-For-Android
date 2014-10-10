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
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;

public class HouseholdMembersAdapter extends ParseQueryAdapter<User>  {

    public HouseholdMembersAdapter(Context context) {

        super(context, new ParseQueryAdapter.QueryFactory<User>() {
            public ParseQuery<User> create() {
                ParseQuery<User> query = new ParseQuery<User>(User.class);
                query.whereEqualTo("activeHousehold", User.getCurrentUser().getActiveHousehold());
                query.orderByAscending("displayName");
                query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);

                return query;
            }
        });
    }

    @Override
    public View getItemView(User user, View view, ViewGroup parent) {
        if (view == null) {
            view = View.inflate(getContext(), R.layout.list_household_members_layout, null);
        }
        super.getItemView(user, view, parent);

        setProfilePicture(user, view);
        setUpTitle(user, view);

        return view;
    }

    private void setUpTitle(User user, View view) {
        TextView titleView = (TextView) view.findViewById(R.id.text1);
        titleView.setText(user.getDisplayName());
        titleView.setTextColor(Color.BLACK);
        TextView emailTextView = (TextView) view.findViewById(R.id.textViewUserEmail);
        emailTextView.setTextColor(Color.BLACK);
        emailTextView.setText(user.getEmail());
    }

    private void setProfilePicture(User user, View view) {
        ParseImageView profilePic = (ParseImageView) view.findViewById(R.id.icon);
        ParseFile imageFile = user.getProfilePicture();
        if (imageFile != null) {
            profilePic.setParseFile(imageFile);
            profilePic.loadInBackground();
        }
    }
}
