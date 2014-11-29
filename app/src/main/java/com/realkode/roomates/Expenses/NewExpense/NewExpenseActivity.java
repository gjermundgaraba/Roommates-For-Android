package com.realkode.roomates.Expenses.NewExpense;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.SaveCallback;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.Me.HouseholdMembersAdapter;
import com.realkode.roomates.ParseSubclassses.Expense;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class NewExpenseActivity extends Activity {
    private EditText expenseNameText;
    private EditText totalAmountText;
    private EditText descriptionText;
    private ArrayList<User> notPaidList;
    private ArrayList<User> paidList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_expense);
        notPaidList = new ArrayList<User>();
        paidList = new ArrayList<User>();
        final ListView membersListView = (ListView) findViewById(R.id.listViewMemberList);

        expenseNameText = (EditText) findViewById(R.id.edit_text_expense_name);
        totalAmountText = (EditText) findViewById(R.id.edit_text_total_amount);
        descriptionText = (EditText) findViewById(R.id.edit_text_description);

        HouseholdMembersAdapter membersListViewAdapter = new HouseholdMembersAdapter(this);
        membersListView.setAdapter(membersListViewAdapter);

        membersListView.setOnItemClickListener(new MemberListOnClickListener(notPaidList, paidList, membersListView));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_expense_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == findViewById(R.id.action_save).getId()) {
            saveExpense();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveExpense() {
        String expenseName = expenseNameText.getText().toString();
        Double totalAmount = getTotalAmount();
        String description = descriptionText.getText().toString();

        if ((notPaidList.isEmpty() && paidList.isEmpty()) || expenseName.isEmpty() ||
                totalAmount.isNaN() || description.isEmpty() || totalAmount <= 0) {
            ToastMaker.makeLongToast(R.string.fill_out_all_fields_new_expense, this);
        } else {
            Expense expense = new Expense();

            expense.setNotPaidUp(notPaidList);
            expense.setPaidUp(paidList);
            expense.setHousehold(User.getCurrentUser().getActiveHousehold());
            expense.setDetails(description);
            expense.setTotalAmount(totalAmount);
            expense.setOwed(User.getCurrentUser());
            expense.setName(expenseName);
            final ProgressDialog resetProgress = ProgressDialog
                    .show(NewExpenseActivity.this, getString(R.string.saving), getString(R.string.please_wait), true);
            expense.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    resetProgress.dismiss();
                    ToastMaker.makeLongToast(R.string.expense_saved, getApplicationContext());
                    finish();
                }
            });
        }
    }

    private Double getTotalAmount() {
        DecimalFormat df = new DecimalFormat(".00");

        try {
            return Double.parseDouble(df.format(Double.parseDouble(totalAmountText.getText().toString())));
        } catch (NumberFormatException e) {
            ToastMaker.makeLongToast(R.string.could_not_parse_amount, this);
            return 0.0;
        }
    }
}
