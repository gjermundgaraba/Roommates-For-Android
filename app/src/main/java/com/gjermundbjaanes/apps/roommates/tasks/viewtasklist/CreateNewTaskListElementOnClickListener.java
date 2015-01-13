package com.gjermundbjaanes.apps.roommates.tasks.viewtasklist;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

import com.gjermundbjaanes.apps.roommates.R;
import com.gjermundbjaanes.apps.roommates.helpers.ToastMaker;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.TaskListElement;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.User;
import com.parse.ParseException;
import com.parse.SaveCallback;

public class CreateNewTaskListElementOnClickListener implements DialogInterface.OnClickListener {
    private final Context context;
    private final EditText taskListElementNameField;
    private final TaskListElementsAdapter taskListElementsAdapter;

    public CreateNewTaskListElementOnClickListener(Context context, EditText taskListElementNameField,
                                                   TaskListElementsAdapter taskListElementsAdapter) {
        this.context = context;
        this.taskListElementNameField = taskListElementNameField;
        this.taskListElementsAdapter = taskListElementsAdapter;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        String taskListName = taskListElementNameField.getText().toString();
        if (taskListName.isEmpty()) {
            ToastMaker.makeLongToast("Tasklist element name cannot be empty", context);
        } else {
            TaskListElement taskListElement = new TaskListElement();
            taskListElement.setElementName(taskListName);
            taskListElement.setCreatedBy(User.getCurrentUser());
            taskListElement.setTaskList(taskListElementsAdapter.getTaskList());
            taskListElement.setUpdatedBy(User.getCurrentUser());

            taskListElement.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    taskListElementsAdapter.loadObjects();
                    ToastMaker.makeShortToast(R.string.new_task_list_element_created, context);
                }
            });
        }

    }
}
