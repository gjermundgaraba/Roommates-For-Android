package com.realkode.roomates.Tasks.OnClickListeners;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.ParseSubclassses.TaskListElement;
import com.realkode.roomates.R;
import com.realkode.roomates.Tasks.Adapters.TaskListElementsAdapter;

public class DeleteTaskListElementOnClickListener implements DialogInterface.OnClickListener {
    private Context context;
    private TaskListElementsAdapter taskListElementsAdapter;
    private TaskListElement taskListElement;

    public DeleteTaskListElementOnClickListener(Context context, TaskListElementsAdapter taskListElementsAdapter, TaskListElement taskListElement) {
        this.context = context;
        this.taskListElementsAdapter = taskListElementsAdapter;
        this.taskListElement = taskListElement;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        final ProgressDialog deleteProgress = ProgressDialog.show(context, context.getString(R.string.deleting_task), context.getString(R.string.please_wait), true);
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
