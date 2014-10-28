package com.realkode.roomates.Me;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.realkode.roomates.ParseSubclassses.Household;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;


public class MyHouseholdActivity extends Activity {
    private TextView textViewHouseholdName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_household_active);

        textViewHouseholdName = (TextView) findViewById(R.id.textViewHouseholdName);

        Button leaveHouseholdBtn = (Button) findViewById(R.id.buttonLeaveHousehold);
        leaveHouseholdBtn.setOnClickListener(new LeaveHouseholdOnClickListener(this));

        Button inviteUserBtn = (Button) findViewById(R.id.buttonInvite);
        inviteUserBtn.setOnClickListener(new InviteUserOnClickListener(this));

        ListView membersListView = (ListView) findViewById(R.id.householdMembersListView);
        HouseholdMembersAdapter membersListViewAdapter = new HouseholdMembersAdapter(this);
        membersListView.setAdapter(membersListViewAdapter);

        setHouseholdNameTitle();
    }

    private void setHouseholdNameTitle() {
        ParseQuery<Household> query = new ParseQuery<Household>(Household.class);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        String householdObjectId = User.getCurrentUser().getActiveHousehold().getObjectId();
        query.getInBackground(householdObjectId, new GetCallback<Household>() {
            @Override
            public void done(Household household, ParseException e) {
                textViewHouseholdName.setText(household.getHouseholdName());
            }
        });
    }
}
