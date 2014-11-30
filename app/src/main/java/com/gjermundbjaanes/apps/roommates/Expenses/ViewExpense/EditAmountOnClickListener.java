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

import java.text.DecimalFormat;

class EditAmountOnClickListener implements DialogInterface.OnClickListener {
    private final Context context;
    private final EditText amountField;
    private final TextView expenseAmountView;
    private final Expense activeExpense;

    public EditAmountOnClickListener(Context context, EditText amountField, TextView expenseAmountView,
                                     Expense activeExpense) {
        this.context = context;
        this.amountField = amountField;
        this.expenseAmountView = expenseAmountView;
        this.activeExpense = activeExpense;
    }

    @Override
    public void onClick(DialogInterface dialog, int id) {
        DecimalFormat df = new DecimalFormat(".00");
        String totalAmount = "";

        try {
            totalAmount = df.format(Double.parseDouble(amountField.getText().toString()));
        } catch (Exception e) {
            ToastMaker.makeLongToast(e.getLocalizedMessage(), context);
        }

        final ProgressDialog resetProgress = ProgressDialog
                .show(context, context.getString(R.string.changing_amount), context.getString(R.string.please_wait),
                        true);
        activeExpense.setTotalAmount(Double.parseDouble(totalAmount));
        final String finalTotalAmount = totalAmount;
        activeExpense.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                resetProgress.dismiss();
                expenseAmountView.setText((finalTotalAmount));
                ToastMaker.makeLongToast(R.string.amount_was_changed, context);
                Intent intent = new Intent(Constants.EXPENSE_NEED_TO_REFRESH);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

            }
        });
    }
}
