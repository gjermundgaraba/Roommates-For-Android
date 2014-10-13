package com.realkode.roomates.Tasks;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.SaveCallback;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.ParseSubclassses.TaskList;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;
import com.realkode.roomates.RefreshableFragment;

public class TaskListFragment extends Fragment implements RefreshableFragment {

    private final BroadcastReceiver mMessageReceiver = new NeedToRefreshBroadcastReceiver();
    private TaskListsAdapter adapter;

    public void refreshFragment() {
        if (User.loggedInAndMemberOfAHousehold()) {
            adapter.loadObjects();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_task_list, container, false);
        adapter = new TaskListsAdapter(getActivity());

        if (User.loggedInAndMemberOfAHousehold()) {
            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
            broadcastManager.registerReceiver(mMessageReceiver, new IntentFilter(TaskListElementsActivity.NEED_TO_REFRESH));

            ListView taskListView = (ListView) rootView.findViewById(R.id.taskListsListView);
            taskListView.setAdapter(adapter);

            taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    TaskList taskList = (TaskList) parent.getItemAtPosition(position);

                    // The objectID to identify which taskListElements to get.
                    String objectID = taskList.getObjectId();
                    Context context = getActivity().getApplicationContext();
                    Intent intent = new Intent(context, TaskListElementsActivity.class);

                    intent.putExtra("taskListID", objectID);

                    startActivity(intent);

                }
            });
        }

        return rootView;
    }

    public void startCreateNewTaskListDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptsView = layoutInflater.inflate(R.layout.dialog_new_tasklist, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

        alertDialogBuilder.setTitle("Create new task list")
                .setCancelable(false).setView(promptsView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        TaskList taskList = new TaskList();
                        String taskListName = userInput.getText().toString();
                        taskList.setListName(taskListName);
                        taskList.setDone(false);
                        taskList.setCreatedBy(User.getCurrentUser());
                        taskList.setHousehold(User.getCurrentUser().getActiveHousehold());
                        taskList.setUpdatedBy(User.getCurrentUser());

                        taskList.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                TaskListFragment.this.adapter.loadObjects();
                                ToastMaker.makeShortToast("Task list added", getActivity());
                            }
                        });


                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
    }

    private class NeedToRefreshBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshFragment();
        }
    }
}
