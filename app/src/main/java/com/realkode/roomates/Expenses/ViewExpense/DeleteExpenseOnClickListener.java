package com.realkode.roomates.Expenses.ViewExpense;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.realkode.roomates.Helpers.Constants;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.ParseSubclassses.Expense;

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
                ProgressDialog.show(context, "Deleting expense", " Please wait ... ", true);
        activeExpense.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                // Returns to the previous activity
                resetProgress.dismiss();
                ToastMaker.makeLongToast("Expense was deleted", context);
                Intent intent = new Intent(Constants.EXPENSE_NEED_TO_REFRESH);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                activity.finish();
            }
        });
    }
}
