package com.realkode.roomates.Expenses.ViewExpense;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.realkode.roomates.Expenses.EditPeopleExpenseActivity;
import com.realkode.roomates.Helpers.Constants;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.ParseSubclassses.Expense;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;

/**
 * This activity shows details of an expense
 */
public class ViewExpenseActivity extends Activity {
    private Expense activeExpense;
    private TextView expenseNameView;
    private TextView expenseOwedView;
    private TextView expenseAmountView;
    private TextView expenseDetailsView;
    private ListView listView;
    private ViewExpenseAdapter viewExpenseAdapter;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ParseQuery<Expense> query = new ParseQuery("Expense");
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

    // Called when item on action bar is pressed
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

        final EditText descriptionField = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);
        descriptionField.setText(activeExpense.getDetails());

        alertDialogBuilder.setTitle(getString(R.string.edit_description))
                .setView(promptsView)
                .setPositiveButton(getString(R.string.save), new EditDescriptionOnClickListener(this, activeExpense, descriptionField, expenseDetailsView))
                .setNegativeButton(getString(R.string.cancel), null);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();

    }

    private void editExpenseAmount() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptsView = layoutInflater.inflate(R.layout.dialog_text_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        final EditText amountField = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);
        amountField.setText(activeExpense.getTotalAmount() + "");
        amountField.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);


        alertDialogBuilder.setTitle(getString(R.string.total_expense_amount))
                .setView(promptsView)
                .setPositiveButton(getString(R.string.save), new EditAmountOnClickListener(this, amountField, expenseAmountView, activeExpense))
                .setNegativeButton(getString(R.string.cancel), null);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
    }

    private void renameExpense() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.dialog_text_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);
        userInput.setText(activeExpense.getName());

        // set dialog message
        alertDialogBuilder.setTitle("Rename expense")
                .setCancelable(false).setView(promptsView)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // get user input and set it to result
                        // edit text
                        final String name = userInput.getText().toString();
                        final ProgressDialog resetProgress = ProgressDialog.show(ViewExpenseActivity.this, "Changing name" , " Please wait ... ", true);
                        activeExpense.setName(name);
                        activeExpense.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                resetProgress.dismiss();
                                expenseNameView.setText(name);
                                ToastMaker.makeLongToast("Name was changed",getApplicationContext());
                                Intent intent = new Intent(Constants.EXPENSE_NEED_TO_REFRESH);
                                LocalBroadcastManager.getInstance(ViewExpenseActivity.this).sendBroadcast(intent);

                            }
                        });
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        // show it
        alertDialog.show();
    }
    // Prompt to delete the expense
    private void deleteExpense() {
        final AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle("Delete expense?");
        myAlertDialog.setMessage("Are you sure you want to delete this expense?");

        myAlertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface arg0, int arg1)
            {
                final ProgressDialog resetProgress = ProgressDialog.show(ViewExpenseActivity.this, "Deleting expense" , " Please wait ... ", true);
                activeExpense.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        // Returns to the previous activity
                        resetProgress.dismiss();
                        ToastMaker.makeLongToast("Expense was deleted",getApplicationContext());
                        Intent intent = new Intent(Constants.EXPENSE_NEED_TO_REFRESH);
                        LocalBroadcastManager.getInstance(ViewExpenseActivity.this).sendBroadcast(intent);
                        finish();
                    }
                });

            }
        });

        myAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int arg1)
            {
                dialog.cancel();
            }
        });
        myAlertDialog.show();
    }

    // Called when activity is started
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_expense);

        expenseNameView = (TextView) findViewById(R.id.expenseTextViewName);
        expenseOwedView = (TextView) findViewById(R.id.expenseTextViewOwed);
        expenseAmountView = (TextView) findViewById(R.id.expenseTextViewAmount);
        expenseDetailsView = (TextView) findViewById(R.id.expenseTextViewDetails);
        listView = (ListView) findViewById(R.id.listViewExpenseUsers);
        final ProgressDialog progress = ProgressDialog.show(ViewExpenseActivity.this, "Loading expense" , " Please wait ... ", true);
        final Context context = this;
        String objectId = (String) getIntent().getExtras().get(Constants.EXTRA_NAME_EXPENSE_ID);
        ParseQuery<Expense> query = new ParseQuery<Expense>("Expense");
        query.include("owed");
        query.include("notPaidUp");
        query.include("paidUp");
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.registerReceiver(mMessageReceiver, new IntentFilter(Constants.EXPENSE_NEED_TO_REFRESH));


        query.getInBackground(objectId, new GetCallback<Expense>() {
            @Override
            public void done(final Expense expense, ParseException e) {
                if (e == null) {
                    // Set up the UI when the query is finished
                    progress.dismiss();
                    activeExpense = expense;
                    expenseNameView.setText(expense.getName());
                    expenseOwedView.setText(expense.getOwed().getDisplayName());
                    expenseAmountView.setText("" + expense.getTotalAmount());
                    expenseDetailsView.setText(expense.getDetails());
                    final ViewExpenseAdapter adapter = new ViewExpenseAdapter(context, expense);
                    ViewExpenseActivity.this.viewExpenseAdapter = adapter;
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                            User theUser = (User) parent.getItemAtPosition(position);

                            System.out.println(theUser.getDisplayName() + " was clicked");
                            adapter.swapElement(theUser);


                        }
                    });
                }
                else {
                    System.out.println("DAFUQ SOMETHING HAPPEND!");
                }

            }
        });
    }

}
