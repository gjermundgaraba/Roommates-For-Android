package com.realkode.roomates.Expenses.ViewExpense;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputType;
import android.view.*;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.realkode.roomates.Expenses.EditPeople.EditPeopleExpenseActivity;
import com.realkode.roomates.Helpers.Constants;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.ParseSubclassses.Expense;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;

public class ViewExpenseActivity extends Activity {
    private Expense activeExpense;
    private TextView expenseNameView;
    private TextView expenseOwedView;
    private TextView expenseAmountView;
    private TextView expenseDetailsView;
    private ListView listView;
    private ViewExpenseAdapter viewExpenseAdapter;

    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ParseQuery<Expense> query = new ParseQuery<Expense>(Expense.class);
            query.include("owed");
            query.include("notPaidUp");
            query.include("paidUp");

            query.getInBackground(activeExpense.getObjectId(), new GetCallback<Expense>() {
                @Override
                public void done(Expense expense, ParseException e) {
                    ViewExpenseActivity.this.viewExpenseAdapter.expense = expense;
                    ViewExpenseActivity.this.viewExpenseAdapter.loadElements();
                }
            });
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_expense_menu, menu);
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_expense);

        setUpBroadcastReceiver();
        setUpViews();
        queryForExpense();
    }

    private void queryForExpense() {
        final ProgressDialog progress = ProgressDialog
                .show(ViewExpenseActivity.this, getString(R.string.loading_expense), getString(R.string.please_wait),
                        true);

        String expenseObjectId = (String) getIntent().getExtras().get(Constants.EXTRA_NAME_EXPENSE_ID);

        ParseQuery<Expense> query = new ParseQuery<Expense>("Expense");
        query.include("owed");
        query.include("notPaidUp");
        query.include("paidUp");
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);

        query.getInBackground(expenseObjectId, new GetCallback<Expense>() {
            @Override
            public void done(final Expense expense, ParseException e) {
                if (e == null) {
                    progress.dismiss();
                    activeExpense = expense;
                    expenseNameView.setText(expense.getName());
                    expenseOwedView.setText(expense.getOwed().getDisplayName());
                    expenseAmountView.setText("" + expense.getTotalAmount());
                    expenseDetailsView.setText(expense.getDetails());
                    final ViewExpenseAdapter adapter = new ViewExpenseAdapter(ViewExpenseActivity.this, expense);
                    ViewExpenseActivity.this.viewExpenseAdapter = adapter;
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                            User theUser = (User) parent.getItemAtPosition(position);
                            adapter.swapElement(theUser);
                        }
                    });
                } else {
                    ToastMaker.makeLongToast(R.string.could_not_get_expense, ViewExpenseActivity.this);
                    ViewExpenseActivity.this.finish();
                }

            }
        });
    }

    private void setUpBroadcastReceiver() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.registerReceiver(mMessageReceiver, new IntentFilter(Constants.EXPENSE_NEED_TO_REFRESH));
    }

    private void setUpViews() {
        expenseNameView = (TextView) findViewById(R.id.expenseTextViewName);
        expenseOwedView = (TextView) findViewById(R.id.expenseTextViewOwed);
        expenseAmountView = (TextView) findViewById(R.id.expenseTextViewAmount);
        expenseDetailsView = (TextView) findViewById(R.id.expenseTextViewDetails);
        listView = (ListView) findViewById(R.id.listViewExpenseUsers);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        if (!activeExpense.getOwed().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
            ToastMaker.makeShortToast(getString(R.string.edit_expense_not_allowed), this);
            return super.onOptionsItemSelected(item);
        }

        switch (item.getItemId()) {
            case R.id.action_delete_expense:
                deleteExpense();
                return true;
            case R.id.action_rename_expense:
                renameExpense();
                return true;
            case R.id.action_edit_amount:
                editExpenseAmount();
                return true;
            case R.id.action_edit_details:
                editDetails();
                return true;
            case R.id.action_add_people:
                editPeople();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void editPeople() {
        Context context = getApplicationContext();
        Intent intent = new Intent(context, EditPeopleExpenseActivity.class);
        intent.putExtra(Constants.EXTRA_NAME_EXPENSE_ID, activeExpense.getObjectId());
        startActivity(intent);
    }

    private void editDetails() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptsView = layoutInflater.inflate(R.layout.dialog_text_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        final EditText descriptionField = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
        descriptionField.setText(activeExpense.getDetails());

        alertDialogBuilder.setTitle(getString(R.string.edit_description)).setView(promptsView)
                .setPositiveButton(getString(R.string.save),
                        new EditDescriptionOnClickListener(this, activeExpense, descriptionField, expenseDetailsView))
                .setNegativeButton(getString(R.string.cancel), null);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();

    }

    private void editExpenseAmount() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptsView = layoutInflater.inflate(R.layout.dialog_text_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        final EditText amountField = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
        amountField.setText(activeExpense.getTotalAmount() + "");
        amountField.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);


        alertDialogBuilder.setTitle(getString(R.string.total_expense_amount)).setView(promptsView)
                .setPositiveButton(getString(R.string.save),
                        new EditAmountOnClickListener(this, amountField, expenseAmountView, activeExpense))
                .setNegativeButton(getString(R.string.cancel), null);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
    }

    private void renameExpense() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptsView = layoutInflater.inflate(R.layout.dialog_text_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        final EditText expenseNameField = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
        expenseNameField.setText(activeExpense.getName());

        alertDialogBuilder.setTitle(getString(R.string.rename_expense)).setView(promptsView)
                .setPositiveButton(getString(R.string.save),
                        new RenameExpenseOnClickListener(this, expenseNameField, activeExpense, expenseNameView))
                .setNegativeButton(getString(R.string.cancel), null);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
    }

    private void deleteExpense() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getString(R.string.delete_expense_question_mark))
                .setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_expense))
                .setPositiveButton(getString(R.string.yes), new DeleteExpenseOnClickListener(this, activeExpense))
                .setNegativeButton(getString(R.string.cancel), null);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
    }

}
