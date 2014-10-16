package com.realkode.roomates.Tasks.OnClickListeners;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.SaveCallback;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.ParseSubclassses.TaskListElement;
import com.realkode.roomates.Tasks.Adapters.TaskListElementsAdapter;

public class ChangeTaskListElementTitleOnClickListener implements DialogInterface.OnClickListener {
    private Context context;
    private EditText titleField;
    private TaskListElement taskListElement;
    private TaskListElementsAdapter taskListElementsAdapter;

    public ChangeTaskListElementTitleOnClickListener(Context context, EditText titleField, TaskListElement taskListElement, TaskListElementsAdapter taskListElementsAdapter) {
        this.context = context;
        this.titleField = titleField;
        this.taskListElement = taskListElement;
        this.taskListElementsAdapter = taskListElementsAdapter;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        final String name = titleField.getText().toString();
        final ProgressDialog resetProgress = ProgressDialog.show(context, "Changing name" , " Please wait ... ", true);
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
