package com.realkode.roomates.Expenses;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.Me.HouseholdMembersAdapter;
import com.realkode.roomates.ParseSubclassses.Expense;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;

import java.nio.DoubleBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity for creating a new expense.
 */
public class NewExpenseActivity extends Activity {

    EditText expenseNameText;
    EditText totalAmountText;
    EditText descriptionText;
    ArrayList<User> notPaidList;
    ArrayList<User> paidList;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_expense_menu, menu);
        return true;
    }


    // Method called when item on actionbar is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == findViewById(R.id.action_save).getId()) {
            saveExpense();
        }
        return true;
    }


    // Saving the new expense.
    private void saveExpense() {
        String expenseName = expenseNameText.getText().toString();
        Double totalAmount = -1.0;

        // Decimalformat with to digits after the comma.
        DecimalFormat df = new DecimalFormat(".00");

        // Parsing the double from the text field and formatting it.
        try {

            totalAmount = Double.parseDouble(df.format(Double.parseDouble(totalAmountText.getText().toString())));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


        String description = descriptionText.getText().toString();


        if ((notPaidList.isEmpty() && paidList.isEmpty()) || expenseName.isEmpty() || totalAmount.isNaN()
                || description.isEmpty() || totalAmount <= 0) {
            ToastMaker.makeLongToast("Fill out all the fields. There must be at least one person paying", getApplicationContext());
        } else {


            Expense expense = new Expense();

            expense.setNotPaidUp(notPaidList);
            expense.setPaidUp(paidList);
            expense.setHousehold(User.getCurrentUser().getActiveHousehold());
            expense.setDetails(description);
            expense.setTotalAmount(totalAmount);
            expense.setOwed(User.getCurrentUser());
            expense.setName(expenseName);
            final ProgressDialog resetProgress = ProgressDialog.show(NewExpenseActivity.this, "Saving", " Please wait ... ", true);
            expense.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    // Finishing the activity and returns to the first activity on the backstack.
                    ToastMaker.makeLongToast("Expense was saved", getApplicationContext());
                    resetProgress.dismiss();
                    finish();
                }
            });
        }
    }

    // called when activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_expense);
        notPaidList = new ArrayList<User>();
        paidList = new ArrayList<User>();
        final ListView membersListView = (ListView) findViewById(R.id.listViewMemberList);

        expenseNameText = (EditText) findViewById(R.id.editText_expense_name);
        totalAmountText = (EditText) findViewById(R.id.editText_total_amount);
        descriptionText = (EditText) findViewById(R.id.editText_description);

        HouseholdMembersAdapter membersListViewAdapter = new HouseholdMembersAdapter(this);
        membersListView.setAdapter(membersListViewAdapter);

        membersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User clickedUser = (User) membersListView.getItemAtPosition(i);


                // Checking if clicked user should be put in or removed from paidlist or notpaidlist. If the user is currentuser, it
                // should be associated with paidlist, else notpaidlist
                if (clickedUser.getObjectId().equals(ParseUser.getCurrentUser().getObjectId()) && paidList.contains(clickedUser)) {
                    adapterView.getChildAt(i).setBackgroundColor(Color.WHITE);
                    paidList.remove(clickedUser);

                } else if (clickedUser.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                    adapterView.getChildAt(i).setBackgroundColor(Color.LTGRAY);
                    paidList.add(clickedUser);

                } else if (notPaidList.contains(clickedUser)) {
                    adapterView.getChildAt(i).setBackgroundColor(Color.WHITE);
                    notPaidList.remove(clickedUser);
                } else {
                    notPaidList.add(clickedUser);
                    adapterView.getChildAt(i).setBackgroundColor(Color.LTGRAY);
                }


            }
        });
    }
}
