package com.gjermundbjaanes.apps.roommates.tasks.fragment;

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

import com.gjermundbjaanes.apps.roommates.AddBehaviourFragment;
import com.gjermundbjaanes.apps.roommates.R;
import com.gjermundbjaanes.apps.roommates.RefreshableFragment;
import com.gjermundbjaanes.apps.roommates.helpers.Constants;
import com.gjermundbjaanes.apps.roommates.helpers.ToastMaker;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.TaskList;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.User;
import com.gjermundbjaanes.apps.roommates.tasks.viewtasklist.ViewTaskListActivity;
import com.parse.ParseException;
import com.parse.SaveCallback;

public class TaskListFragment extends Fragment implements RefreshableFragment, AddBehaviourFragment {
    private final BroadcastReceiver mMessageReceiver = new NeedToRefreshBroadcastReceiver();
    private TaskListsAdapter adapter;
    private TaskListSaver taskListSaver = new TaskListSaver(new TaskListSaveCallback());

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
            broadcastManager.registerReceiver(mMessageReceiver, new IntentFilter(Constants.NEED_TO_REFRESH));

            ListView taskListView = (ListView) rootView.findViewById(R.id.taskListsListView);
            taskListView.setAdapter(adapter);

            taskListView.setOnItemClickListener(new TaskListOnClickListener());
        }

        return rootView;
    }

    private void startCreateNewTaskListDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptsView = layoutInflater.inflate(R.layout.dialog_new_tasklist, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        final EditText taskListNameInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

        alertDialogBuilder
                .setTitle(getActivity().getString(R.string.dialog_create_new_task_list_title))
                .setView(promptsView)
                .setPositiveButton(getActivity().getString(R.string.dialog_OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        taskListSaver.saveTaskList(taskListNameInput.getText().toString());
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
    }

    @Override
    public void add() {
        startCreateNewTaskListDialog();
    }

    private class NeedToRefreshBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshFragment();
        }
    }

    private class TaskListSaveCallback extends SaveCallback {
        @Override
        public void done(ParseException e) {
            Intent intent = new Intent(Constants.NEED_TO_REFRESH);
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            ToastMaker.makeShortToast(R.string.toast_new_task_list_added, getActivity());
        }
    }

    private class TaskListOnClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TaskList taskList = (TaskList) parent.getItemAtPosition(position);

            String taskListObjectId = taskList.getObjectId();
            Intent intent = new Intent(getActivity(), ViewTaskListActivity.class);

            intent.putExtra("taskListID", taskListObjectId);

            getActivity().startActivity(intent);
        }
    }
}
