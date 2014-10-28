package com.realkode.roomates.Expenses.ViewExpense;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.SaveCallback;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.ParseSubclassses.Expense;
import com.realkode.roomates.R;

class EditDescriptionOnClickListener implements DialogInterface.OnClickListener {
    private final Context context;
    private final Expense activeExpense;
    private final EditText descriptionField;
    private final TextView expenseDetailsView;

    public EditDescriptionOnClickListener(Context context, Expense activeExpense, EditText descriptionField,
                                          TextView expenseDetailsView) {
        this.context = context;
        this.activeExpense = activeExpense;
        this.descriptionField = descriptionField;
        this.expenseDetailsView = expenseDetailsView;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        final String description = descriptionField.getText().toString();
        final ProgressDialog resetProgress = ProgressDialog
                .show(context, context.getString(R.string.changing_description),
                        context.getString(R.string.please_wait), true);
        activeExpense.setDetails(description);
        activeExpense.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                resetProgress.dismiss();
                expenseDetailsView.setText(description);
                ToastMaker.makeLongToast(R.string.description_was_changed, context);

            }
        });
    }
}
