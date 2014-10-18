package com.realkode.roomates.Tasks.OnClickListeners;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.SaveCallback;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.ParseSubclassses.TaskListElement;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;
import com.realkode.roomates.Tasks.Adapters.TaskListElementsAdapter;

public class CreateNewTaskListElementOnClickListener implements DialogInterface.OnClickListener {
    Context context;
    EditText taskListElementNameField;
    TaskListElementsAdapter taskListElementsAdapter;

    public CreateNewTaskListElementOnClickListener(Context context, EditText taskListElementNameField, TaskListElementsAdapter taskListElementsAdapter) {
        this.context = context;
        this.taskListElementNameField = taskListElementNameField;
        this.taskListElementsAdapter = taskListElementsAdapter;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        TaskListElement taskListElement = new TaskListElement();
        String taskListName = taskListElementNameField.getText().toString();
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