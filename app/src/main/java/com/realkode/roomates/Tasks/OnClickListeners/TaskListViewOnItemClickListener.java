package com.realkode.roomates.Tasks.OnClickListeners;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.realkode.roomates.ParseSubclassses.TaskList;
import com.realkode.roomates.Tasks.TaskListElementsActivity;

public class TaskListViewOnItemClickListener implements AdapterView.OnItemClickListener {
    Context context;

    public TaskListViewOnItemClickListener(Context context) {
        this.context = context;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TaskList taskList = (TaskList) parent.getItemAtPosition(position);

        String taskListObjectId = taskList.getObjectId();
        Intent intent = new Intent(context, TaskListElementsActivity.class);

        intent.putExtra("taskListID", taskListObjectId);

        context.startActivity(intent);

    }
}
