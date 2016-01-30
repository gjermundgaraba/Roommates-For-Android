package com.gjermundbjaanes.apps.roommates2.expenses.viewexpense;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.gjermundbjaanes.apps.roommates2.R;
import com.gjermundbjaanes.apps.roommates2.helpers.Constants;
import com.gjermundbjaanes.apps.roommates2.helpers.ToastMaker;
import com.gjermundbjaanes.apps.roommates2.parsesubclasses.Expense;
import com.parse.DeleteCallback;
import com.parse.ParseException;

class DeleteExpenseOnClickListener implements DialogInterface.OnClickListener {
    private final Activity activity;
    private final Context context;
    private final Expense activeExpense;

    public DeleteExpenseOnClickListener(Activity activity, Expense activeExpense) {
        this.activity = activity;
        this.context = activity;
        this.activeExpense = activeExpense;
    }

    public void onClick(DialogInterface arg0, int arg1) {
        final ProgressDialog resetProgress =
                ProgressDialog.show(context, activity.getString(R.string.deleting_expense), activity.getString(R.string.please_wait), true);
        activeExpense.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                resetProgress.dismiss();
                ToastMaker.makeLongToast(R.string.expense_was_deleted, context);
                Intent intent = new Intent(Constants.EXPENSE_NEED_TO_REFRESH);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                activity.finish();
            }
        });
    }
}
