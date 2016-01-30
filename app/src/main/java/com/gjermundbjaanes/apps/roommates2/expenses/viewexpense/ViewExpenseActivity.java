package com.gjermundbjaanes.apps.roommates2.expenses.viewexpense;

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

import com.gjermundbjaanes.apps.roommates2.R;
import com.gjermundbjaanes.apps.roommates2.expenses.editpeople.EditPeopleExpenseActivity;
import com.gjermundbjaanes.apps.roommates2.helpers.Constants;
import com.gjermundbjaanes.apps.roommates2.helpers.ToastMaker;
import com.gjermundbjaanes.apps.roommates2.helpers.Utils;
import com.gjermundbjaanes.apps.roommates2.parsesubclasses.Expense;
import com.gjermundbjaanes.apps.roommates2.parsesubclasses.User;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

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
            queryForExpense();
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!isFinishing()){
                    final ProgressDialog progress = ProgressDialog.show(ViewExpenseActivity.this, getString(R.string.loading_expense),
                            getString(R.string.please_wait), true);

                    String expenseObjectId = (String) getIntent().getExtras().get(Constants.EXTRA_NAME_EXPENSE_ID);

                    ParseQuery<Expense> query = new ParseQuery<Expense>(Expense.class);
                    query.include("owed");
                    query.include("notPaidUp");
                    query.include("paidUp");
                    Utils.setSafeQueryCaching(query);

                    query.getInBackground(expenseObjectId, new GetExpenseCallback(progress));
                }
            }
        });

    }

    private void setExpense(Expense expense) {
        activeExpense = expense;
        expenseNameView.setText(expense.getName());
        expenseOwedView.setText(expense.getOwed().getDisplayName());
        expenseAmountView.setText("" + expense.getTotalAmount());
        expenseDetailsView.setText(expense.getDetails());
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
        if (!activeExpense.currentUserIsOwed()) {
            ToastMaker.makeShortToast(R.string.edit_expense_not_allowed, this);
            return true;
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
        descriptionField.setSelection(descriptionField.getText().length());

        alertDialogBuilder.setTitle(getString(R.string.edit_description)).setView(promptsView)
                .setPositiveButton(getString(R.string.save), new EditDescriptionOnClickListener(descriptionField))
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
        amountField.setSelection(amountField.getText().length());


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
        expenseNameField.setSelection(expenseNameField.getText().length());

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

    private class GetExpenseCallback extends GetCallback<Expense> {
        private final ProgressDialog progress;

        public GetExpenseCallback(ProgressDialog progress) {
            this.progress = progress;
        }

        @Override
        public void done(final Expense expense, ParseException e) {
            if (e == null) {
                progress.dismiss();
                setExpense(expense);
                viewExpenseAdapter = new ViewExpenseAdapter(ViewExpenseActivity.this, expense);
                listView.setAdapter(viewExpenseAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                        if (activeExpense.currentUserIsOwed()) {
                            User theUser = (User) parent.getItemAtPosition(position);
                            viewExpenseAdapter.toggleElement(theUser);
                        } else {
                            ToastMaker.makeShortToast(R.string.edit_expense_not_allowed, getApplicationContext());
                        }

                    }
                });
            } else {
                ToastMaker.makeLongToast(R.string.could_not_get_expense, ViewExpenseActivity.this);
                ViewExpenseActivity.this.finish();
            }
        }
    }

    private class EditDescriptionOnClickListener implements DialogInterface.OnClickListener {
        private final EditText descriptionField;

        public EditDescriptionOnClickListener(EditText descriptionField) {
            this.descriptionField = descriptionField;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            final Context context = ViewExpenseActivity.this;
            final String description = descriptionField.getText().toString();
            final ProgressDialog resetProgress = ProgressDialog.show(context,
                    context.getString(R.string.changing_description), context.getString(R.string.please_wait), true);

            activeExpense.setDetails(description);
            activeExpense.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    resetProgress.dismiss();
                    if (e == null) {
                        expenseDetailsView.setText(description);
                        ToastMaker.makeLongToast(R.string.description_was_changed, context);
                    } else {
                        ToastMaker.makeLongToast(R.string.description_could_not_be_changed, context);
                    }
                }
            });
        }
    }
}
