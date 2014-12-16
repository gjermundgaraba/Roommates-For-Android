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
        final Double totalAmount = getTotalAmount();

        if (totalAmount.isNaN() || totalAmount <= 0) {
            ToastMaker.makeLongToast(R.string.invalid_amount, context);
        } else {
            final ProgressDialog resetProgress = ProgressDialog
                    .show(context, context.getString(R.string.changing_amount), context.getString(R.string.please_wait),
                            true);
            activeExpense.setTotalAmount(totalAmount);
            activeExpense.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    resetProgress.dismiss();
                    expenseAmountView.setText(("" + totalAmount));
                    ToastMaker.makeLongToast(R.string.amount_was_changed, context);
                    Intent intent = new Intent(Constants.EXPENSE_NEED_TO_REFRESH);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                }
            });
        }
    }

    private Double getTotalAmount() {
        DecimalFormat df = new DecimalFormat(".00");

        try {
            return Double.parseDouble(df.format(Double.parseDouble(amountField.getText().toString())));
        } catch (NumberFormatException e) {
            ToastMaker.makeLongToast(R.string.invalid_amount, context);
            return 0.0;
        }
    }
}
