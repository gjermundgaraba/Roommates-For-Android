package com.gjermundbjaanes.apps.roommates.expenses.viewexpense;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.EditText;
import android.widget.TextView;

import com.gjermundbjaanes.apps.roommates.R;
import com.gjermundbjaanes.apps.roommates.helpers.Constants;
import com.gjermundbjaanes.apps.roommates.helpers.ToastMaker;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.Expense;
import com.parse.ParseException;
import com.parse.SaveCallback;

class RenameExpenseOnClickListener implements DialogInterface.OnClickListener {
    private final Context context;
    private final EditText expenseNameField;
    private final Expense activeExpense;
    private final TextView expenseNameView;

    public RenameExpenseOnClickListener(Context context, EditText expenseNameField, Expense activeExpense,
                                        TextView expenseNameView) {
        this.context = context;
        this.expenseNameField = expenseNameField;
        this.activeExpense = activeExpense;
        this.expenseNameView = expenseNameView;
    }

    @Override
    public void onClick(DialogInterface dialog, int id) {
        final String name = expenseNameField.getText().toString();

        if(name.isEmpty()) {
            ToastMaker.makeShortToast(R.string.expense_name_cannot_be_empty, context);
        } else {
            final ProgressDialog resetProgress = ProgressDialog
                    .show(context, context.getString(R.string.changing_name), context.getString(R.string.please_wait),
                            true);
            activeExpense.setName(name);
            activeExpense.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    resetProgress.dismiss();

                    expenseNameView.setText(name);
                    ToastMaker.makeLongToast(R.string.name_was_changed, context);
                    Intent intent = new Intent(Constants.EXPENSE_NEED_TO_REFRESH);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            });
        }

    }
}
