package com.realkode.roomates.Tasks;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.ParseSubclassses.TaskList;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;
import com.realkode.roomates.RefreshableFragment;

import java.util.ArrayList;
import java.util.List;

public class TaskListFragment extends Fragment implements RefreshableFragment {

    private TaskListsAdapter adapter;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            refreshFragment();
        }
    };

    public void refreshFragment() {
        if (User.loggedInAndMemberOfAHousehold()) {
            adapter.loadObjects();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_task_list, container, false);

        if (User.loggedInAndMemberOfAHousehold()) {
            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
            broadcastManager.registerReceiver(mMessageReceiver, new IntentFilter("need-to-refresh"));

            ListView taskListView = (ListView) rootView.findViewById(R.id.taskListsListView);
            adapter = new TaskListsAdapter(getActivity());
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


    private static class TaskListsAdapter extends BaseAdapter {
        private Context context;

        private ArrayList<Item> items = new ArrayList<Item>();
        private ArrayList<TaskList> taskLists = new ArrayList<TaskList>();


        TaskListsAdapter(Context context) {
            this.context = context;
            loadObjects();
        }

        public void reloadElements() {
            if (taskLists != null) {
                items.clear();

                ArrayList<TaskList> unfinishedElements = new ArrayList<TaskList>(taskLists);
                ArrayList<TaskList> finishedElements = new ArrayList<TaskList>();

                for (TaskList taskList : unfinishedElements) {
                    if (taskList.getDone()) {
                        finishedElements.add(taskList);
                    }
                }
                unfinishedElements.removeAll(finishedElements);


                items.add(new SectionItem("Todo"));
                for (TaskList taskList : unfinishedElements) {
                    items.add(new EntryItem(taskList.getListName(), "Created by " + taskList.getCreatedBy().getDisplayName(), taskList));
                }

                items.add(new SectionItem("Finished"));
                for (TaskList taskList : finishedElements) {
                    items.add(new EntryItem(taskList.getListName(), "Created by " + taskList.getCreatedBy().getDisplayName(), taskList));
                }


                notifyDataSetChanged();
            }

        }

        public void loadObjects() {
            ParseQuery<TaskList> taskListParseQuery = ParseQuery.getQuery(TaskList.class);
            taskListParseQuery.include("createdBy");
            taskListParseQuery.orderByAscending("createdAt");
            taskListParseQuery.whereEqualTo("household", User.getCurrentUser().getActiveHousehold());

            if (taskLists.size() == 0) {
                taskListParseQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
            }
            else {
                taskListParseQuery.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
            }


            taskListParseQuery.findInBackground(new FindCallback<TaskList>() {
                @Override
                public void done(List<TaskList> incomingTaskLists, ParseException e) {
                    if (e == null) {
                        taskLists = new ArrayList<TaskList>(incomingTaskLists);
                        reloadElements();
                    }
                }
            });


        }

        @Override
        public int getCount() {
            if (items != null) {
                return items.size();
            }
            else {
                return 0;
            }
        }

        @Override
        public Object getItem(int i) {
            if (items != null) {
                Item item = items.get(i);
                if (!item.isSection()) {
                    EntryItem entryItem = (EntryItem)item;
                    System.out.println(entryItem.taskList.getListName());
                    return entryItem.taskList;
                }
                else {
                    return null;
                }
            }
            else {
                return null;
            }
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            final Item item = items.get(position);

            if (item != null) {
                if (item.isSection()) {
                    SectionItem sectionItem = (SectionItem)item;
                    view = View.inflate(context, R.layout.list_element_section, null);
                    view.setBackgroundColor(Color.LTGRAY);
                    view.setOnClickListener(null);
                    view.setOnLongClickListener(null);
                    view.setLongClickable(false);
                    TextView title = (TextView)view.findViewById(R.id.sectionTitleTextView);

                    title.setText(sectionItem.getTitle());
                }
                else {
                    EntryItem entryItem = (EntryItem)item;
                    view = View.inflate(context, R.layout.list_task_element_layout, null);
                    TextView title = (TextView)view.findViewById(R.id.textViewList);
                    TextView subTitle = (TextView)view.findViewById(R.id.textViewCreatedBy);

                    title.setText(entryItem.title);
                    subTitle.setText(entryItem.subtitle);
                }
            }

            return view;
        }

        private interface Item {
            public boolean isSection();
        }

        private class SectionItem implements Item{

            private final String title;

            public SectionItem(String title) {
                this.title = title;
            }

            public String getTitle(){
                return title;
            }

            @Override
            public boolean isSection() {
                return true;
            }
        }

        public class EntryItem implements Item{

            public final String title;
            public final String subtitle;
            public final TaskList taskList;

            public EntryItem(String title, String subtitle, TaskList element) {
                this.title = title;
                this.subtitle = subtitle;
                this.taskList = element;
            }

            @Override
            public boolean isSection() {
                return false;
            }

        }
    }


    public void startCreateNewTaskListDialog() {

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.dialog_new_tasklist, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder.setTitle("Create new task list")
                .setCancelable(false).setView(promptsView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // get user input and set it to result
                        // edit text
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

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        // show it
        alertDialog.show();
    }

}
