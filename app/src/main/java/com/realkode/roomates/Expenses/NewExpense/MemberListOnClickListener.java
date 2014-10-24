package com.realkode.roomates.Expenses.NewExpense;

import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.ParseUser;
import com.realkode.roomates.ParseSubclassses.User;

import java.util.ArrayList;

public class MemberListOnClickListener implements AdapterView.OnItemClickListener {
    ArrayList<User> notPaidList;
    ArrayList<User> paidList;
    ListView membersListView;

    public MemberListOnClickListener(ArrayList<User> notPaidList, ArrayList<User> paidList, ListView membersListView) {
        this.notPaidList = notPaidList;
        this.paidList = paidList;
        this.membersListView = membersListView;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        User clickedUser = (User) membersListView.getItemAtPosition(position);

        // Checking if clicked user should be put in or removed from paidlist or notpaidlist. If the user is currentuser, it
        // should be associated with paidlist, else notpaidlist
        if (clickedUser.getObjectId().equals(ParseUser.getCurrentUser().getObjectId()) && paidList.contains(clickedUser)) {
            adapterView.getChildAt(position).setBackgroundColor(Color.WHITE);
            paidList.remove(clickedUser);
        } else if (clickedUser.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
            adapterView.getChildAt(position).setBackgroundColor(Color.LTGRAY);
            paidList.add(clickedUser);
        } else if (notPaidList.contains(clickedUser)) {
            adapterView.getChildAt(position).setBackgroundColor(Color.WHITE);
            notPaidList.remove(clickedUser);
        } else {
            notPaidList.add(clickedUser);
            adapterView.getChildAt(position).setBackgroundColor(Color.LTGRAY);
        }
    }

}
