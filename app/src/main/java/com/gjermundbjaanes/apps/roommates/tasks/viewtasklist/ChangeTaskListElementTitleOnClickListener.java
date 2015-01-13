package com.gjermundbjaanes.apps.roommates.tasks.viewtasklist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

import com.gjermundbjaanes.apps.roommates.helpers.ToastMaker;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.TaskListElement;
import com.parse.ParseException;
import com.parse.SaveCallback;

public class ChangeTaskListElementTitleOnClickListener implements DialogInterface.OnClickListener {
    private final Context context;
    private final EditText titleField;
    private final TaskListElement taskListElement;
    private final TaskListElementsAdapter taskListElementsAdapter;

    public ChangeTaskListElementTitleOnClickListener(Context context, EditText titleField,
                                                     TaskListElement taskListElement,
                                                     TaskListElementsAdapter taskListElementsAdapter) {
        this.context = context;
        this.titleField = titleField;
        this.taskListElement = taskListElement;
        this.taskListElementsAdapter = taskListElementsAdapter;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        final String name = titleField.getText().toString();
        if (name.isEmpty()) {
            ToastMaker.makeLongToast("Tasklist element name cannot be empty", context);
        } else {
            final ProgressDialog resetProgress = ProgressDialog.show(context, "Changing name", " Please wait ... ", true);
            taskListElement.setElementName(name);
            taskListElement.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    resetProgress.dismiss();

                    ToastMaker.makeLongToast("Name was changed", context);
                    taskListElementsAdapter.loadObjects();
                }
            });
        }
    }
}
