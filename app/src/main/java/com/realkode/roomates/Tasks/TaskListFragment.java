package com.realkode.roomates.Tasks;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;

import com.realkode.roomates.Helpers.Constants;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;
import com.realkode.roomates.RefreshableFragment;
import com.realkode.roomates.Tasks.Adapters.TaskListsAdapter;
import com.realkode.roomates.Tasks.OnClickListeners.CreateNewTaskListOnClickListener;
import com.realkode.roomates.Tasks.OnClickListeners.TaskListViewOnItemClickListener;

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
            broadcastManager.registerReceiver(mMessageReceiver, new IntentFilter(Constants.NEED_TO_REFRESH));

            ListView taskListView = (ListView) rootView.findViewById(R.id.taskListsListView);
            taskListView.setAdapter(adapter);

            taskListView.setOnItemClickListener(new TaskListViewOnItemClickListener(getActivity()));
        }

        return rootView;
    }

    public void startCreateNewTaskListDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptsView = layoutInflater.inflate(R.layout.dialog_new_tasklist, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        final EditText taskListNameInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

        alertDialogBuilder.setTitle(getActivity().getString(R.string.dialog_create_new_task_list_title))
                .setView(promptsView).setPositiveButton(getActivity().getString(R.string.dialog_OK),
                new CreateNewTaskListOnClickListener(getActivity(), taskListNameInput));

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
