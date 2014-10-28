package com.realkode.roomates.Expenses.EditPeople;

import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.realkode.roomates.ParseSubclassses.User;

import java.util.ArrayList;

class PersonItemOnClickListener implements AdapterView.OnItemClickListener {
    private final ListView listView;
    private final ArrayList<User> paidList;
    private final ArrayList<User> notPaidList;

    public PersonItemOnClickListener(ListView listView, ArrayList<User> paidList, ArrayList<User> notPaidList) {
        this.listView = listView;
        this.paidList = paidList;
        this.notPaidList = notPaidList;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        User clickedUser = (User) listView.getItemAtPosition(position);

        for (User user : notPaidList) {
            if (user.getObjectId().equals(clickedUser.getObjectId())) {
                // part of, lets remove
                notPaidList.remove(user);
                adapterView.getChildAt(position).setBackgroundColor(Color.WHITE);
                return;
            }
        }

        for (User user : paidList) {
            if (user.getObjectId().equals(clickedUser.getObjectId())) {
                // part of, lets remove
                paidList.remove(user);
                adapterView.getChildAt(position).setBackgroundColor(Color.WHITE);
                return;
            }
        }

        // not part of the expense, lets add him
        notPaidList.add(clickedUser);
        adapterView.getChildAt(position).setBackgroundColor(Color.LTGRAY);
    }
}
