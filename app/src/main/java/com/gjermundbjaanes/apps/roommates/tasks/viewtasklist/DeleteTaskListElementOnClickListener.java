package com.gjermundbjaanes.apps.roommates.tasks.viewtasklist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.gjermundbjaanes.apps.roommates.R;
import com.gjermundbjaanes.apps.roommates.helpers.ToastMaker;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.TaskListElement;
import com.parse.DeleteCallback;
import com.parse.ParseException;

public class DeleteTaskListElementOnClickListener implements DialogInterface.OnClickListener {
    private final Context context;
    private final TaskListElementsAdapter taskListElementsAdapter;
    private final TaskListElement taskListElement;

    public DeleteTaskListElementOnClickListener(Context context, TaskListElementsAdapter taskListElementsAdapter,
                                                TaskListElement taskListElement) {
        this.context = context;
        this.taskListElementsAdapter = taskListElementsAdapter;
        this.taskListElement = taskListElement;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        final ProgressDialog deleteProgress = ProgressDialog
                .show(context, context.getString(R.string.deleting_task), context.getString(R.string.please_wait),
                        true);
        taskListElement.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                deleteProgress.dismiss();
                ToastMaker.makeLongToast(R.string.task_was_deleted, context);
                taskListElementsAdapter.loadObjects();
            }
        });
    }
}
