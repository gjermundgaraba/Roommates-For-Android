package com.realkode.roomates.Expenses;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.realkode.roomates.Helpers.Constants;
import com.realkode.roomates.ParseSubclassses.Expense;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;

import java.util.ArrayList;


/**
 * Activity to edit people in an expense
 */
public class EditPeopleExpenseActivity extends Activity{
    Expense activeExpense;
    ArrayList<User> paidList;
    ArrayList<User> notPaidList;



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_expense_menu, menu);
        //optionsMenu = menu;

        return true;
    }


    // The method called when a button on the actionbar is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == findViewById(R.id.action_save).getId())
        {
            saveExpense();
        }
        return true;
    }


    // Saving the edited expense
    private void saveExpense() {
        activeExpense.setPaidUp(paidList);
        activeExpense.setNotPaidUp(notPaidList);
        final ProgressDialog resetProgress = ProgressDialog.show(EditPeopleExpenseActivity.this, "Saving" , " Please wait ... ", true);
        activeExpense.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                // When expense is saved, activity is finished and the first activity on the backstack is resumed.
                resetProgress.dismiss();
                Intent intent = new Intent(Constants.EXPENSE_NEED_TO_REFRESH);
                LocalBroadcastManager.getInstance(EditPeopleExpenseActivity.this).sendBroadcast(intent);
                finish();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ProgressDialog resetProgress = ProgressDialog.show(EditPeopleExpenseActivity.this, "Loading" , " Please wait ... ", true);
        final String objectid = getIntent().getExtras().getString("objectID");


        // Making the query
        ParseQuery<Expense> query = new ParseQuery<Expense>("Expense");
        query.include("owed");
        query.include("notPaidUp");
        query.include("paidUp");

        query.getInBackground(objectid,new GetCallback<Expense>() {
            @Override
            public void done(Expense expense, ParseException e) {
                activeExpense = expense;
                System.out.println("Expense:   " + expense.getOwed().getDisplayName());

                setContentView(R.layout.activity_edit_people_expense);
                final ListView list = (ListView) findViewById(R.id.edit_people_listview);
                ArrayList<User> userList = expense.getNotPaidUp();
                userList.addAll(expense.getPaidUp());
                ArrayList<String> objectIDs = new ArrayList<String>();

                for (User users : userList){
                    objectIDs.add(users.getObjectId());
                }
                HouseholdMembersAdapterEditExpense membersListViewAdapter = new HouseholdMembersAdapterEditExpense(getApplicationContext(),objectIDs);
                paidList = expense.getPaidUp();
                notPaidList = expense.getNotPaidUp();
                list.setAdapter(membersListViewAdapter);

                resetProgress.dismiss();
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        User clickedUser = (User) list.getItemAtPosition(i);

                        // Check if user is part of the expense
                        for (User user : notPaidList) {
                            if (user.getObjectId().equals(clickedUser.getObjectId())) {
                                System.out.println("notPaid contains clicked user");
                                // part of, lets remove
                                notPaidList.remove(user);
                                adapterView.getChildAt(i).setBackgroundColor(Color.WHITE);
                                return;
                            }
                        }

                        for (User user : paidList) {
                            if (user.getObjectId().equals(clickedUser.getObjectId())) {
                                System.out.println("paid contains clicked user");
                                // part of, lets remove
                                paidList.remove(user);
                                adapterView.getChildAt(i).setBackgroundColor(Color.WHITE);
                                return;
                            }
                        }

                        // Else
                        System.out.println("else");
                        // not part of the expense, lets add him
                        notPaidList.add(clickedUser);
                        adapterView.getChildAt(i).setBackgroundColor(Color.LTGRAY);

                    }
                });
            }
        });




    }
}
