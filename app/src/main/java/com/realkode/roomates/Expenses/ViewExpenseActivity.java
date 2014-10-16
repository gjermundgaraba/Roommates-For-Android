package com.realkode.roomates.Expenses;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.realkode.roomates.Helpers.Constants;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.ParseSubclassses.Expense;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

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
            // Get extra data included in the Intent

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_expense_menu, menu);
        //optionsMenu = menu;

        return true;
    }

    // Called when item on action bar is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        if (!activeExpense.getOwed().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
            ToastMaker.makeShortToast("This is not your expense", this);
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
                edit_amount_expense();
                return true;
            case R.id.action_edit_details:
                edit_details();
                return true;
            case R.id.action_add_people:
                edit_people();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Edit the people associated with the expense. Starts a new activity
    private void edit_people() {
        Context context = getApplicationContext();
        Intent intent = new Intent(context, EditPeopleExpenseActivity.class);
        intent.putExtra("objectID",(String) getIntent().getExtras().get("expenseID"));
        startActivity(intent);
    }

    // Edit the details field in the expense
    private void edit_details() {
        // get dialog_text_prompt.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.dialog_text_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);
        userInput.setText(activeExpense.getDetails());

        // set dialog message
        alertDialogBuilder.setTitle("Edit description")
                .setCancelable(false).setView(promptsView)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // get user input and set it to result
                        // edit text
                        final String description = userInput.getText().toString();
                        final ProgressDialog resetProgress = ProgressDialog.show(ViewExpenseActivity.this, "Changing description" , " Please wait ... ", true);
                        activeExpense.setDetails(description);
                        activeExpense.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                resetProgress.dismiss();
                                expenseDetailsView.setText(description);
                                ToastMaker.makeLongToast("Description was changed",getApplicationContext());

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
    // Edit the total amount field in the expense
    private void edit_amount_expense() {
        // get dialog_text_prompt.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.dialog_text_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);
        userInput.setText(activeExpense.getTotalAmount() + "");
        userInput.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);


        // set dialog message
        alertDialogBuilder.setTitle("Total expense amount")
                .setCancelable(false).setView(promptsView)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // get user input and set it to result
                        // edit text

                        DecimalFormat df = new DecimalFormat(".00");
                        String totalAmount = "";
                        try {
                            totalAmount = df.format(Double.parseDouble(userInput.getText().toString()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        final ProgressDialog resetProgress = ProgressDialog.show(ViewExpenseActivity.this, "Changing amount", " Please wait ... ", true);
                        activeExpense.setTotalAmount(Double.parseDouble(totalAmount));
                        final String finalTotalAmount = totalAmount;
                        activeExpense.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                resetProgress.dismiss();
                                expenseAmountView.setText((finalTotalAmount));
                                ToastMaker.makeLongToast("Amount was changed", getApplicationContext());
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
    // Edit the name field in the expense
    private void renameExpense() {
        // get dialog_text_prompt.xml view
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
        String objectId = (String) getIntent().getExtras().get("expenseID");
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
    // Private class for the list adapter
    private static class ViewExpenseAdapter extends BaseAdapter {
        private Context context;

        private ArrayList<Item> items = new ArrayList<Item>();
        public Expense expense;

        // Called with the expense as constructor
        ViewExpenseAdapter(Context context, Expense expense) {
            this.context = context;
            this.expense = expense;
            loadElements();
        }


        // Loading the elements into the different sections.
        public void loadElements() {
            // Clearing the list before generating the new
            items.clear();


            ArrayList<User> notPaidUp = expense.getNotPaidUp();
            ArrayList<User> paidUp = expense.getPaidUp();


            int numUsers = notPaidUp.size() + paidUp.size();
            DecimalFormat df = new DecimalFormat(".00");
            String amountOwed = df.format(expense.getTotalAmount().doubleValue() / numUsers);
            items.add(new SectionItem("Not paid up"));

            for (User user : notPaidUp) {
                items.add(new EntryItem(user.getDisplayName(), "Owes " + amountOwed, user));
            }

            items.add(new SectionItem("Paid up"));

            for (User user : paidUp) {
                items.add(new EntryItem(user.getDisplayName(), "Has paid up", user));
            }


            notifyDataSetChanged();


        }


        public void swapElement(User user) {
            ArrayList<User> notPaid = expense.getNotPaidUp();
            ArrayList<User> paid = expense.getPaidUp();


            if (notPaid.contains(user)) {
                notPaid.remove(user);
                paid.add(user);

                expense.setNotPaidUp(notPaid);
                expense.setPaidUp(paid);
            } else {
                paid.remove(user);
                notPaid.add(user);

                expense.setNotPaidUp(notPaid);
                expense.setPaidUp(paid);
            }
            loadElements();
            expense.saveEventually();

            if (notPaid.size() == 0) {
                Intent intent = new Intent(Constants.EXPENSE_NEED_TO_REFRESH);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        }

        @Override
        public int getCount() {
            if (items != null) {
                return items.size();
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int i) {
            if (items != null) {
                Item item = items.get(i);
                if (!item.isSection()) {
                    EntryItem entryItem = (EntryItem) item;

                    return entryItem.user;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            final Item item = items.get(position);

            if (item != null) {
                if (item.isSection()) {
                    SectionItem sectionItem = (SectionItem) item;
                    view = View.inflate(context, R.layout.list_element_section, null);
                    view.setBackgroundColor(Color.LTGRAY);
                    view.setOnClickListener(null);
                    view.setOnLongClickListener(null);
                    view.setLongClickable(false);
                    TextView title = (TextView) view.findViewById(R.id.sectionTitleTextView);

                    title.setText(sectionItem.getTitle());
                } else {
                    EntryItem entryItem = (EntryItem) item;
                    view = View.inflate(context, R.layout.list_task_element_layout, null);
                    TextView title = (TextView) view.findViewById(R.id.textViewList);
                    TextView subTitle = (TextView) view.findViewById(R.id.textViewCreatedBy);

                    title.setText(entryItem.title);
                    subTitle.setText(entryItem.subtitle);
                }
            }

            return view;
        }

        private interface Item {
            public boolean isSection();
        }

        private class SectionItem implements Item {

            private final String title;

            public SectionItem(String title) {
                this.title = title;
            }

            public String getTitle() {
                return title;
            }

            @Override
            public boolean isSection() {
                return true;
            }
        }

        public class EntryItem implements Item {

            public final String title;
            public final String subtitle;
            public final User user;

            public EntryItem(String title, String subtitle, User element) {
                this.title = title;
                this.subtitle = subtitle;
                this.user = element;
            }

            @Override
            public boolean isSection() {
                return false;
            }

        }
    }
}
