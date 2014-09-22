package com.realkode.roomates.Tasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.ParseSubclassses.TaskList;
import com.realkode.roomates.ParseSubclassses.TaskListElement;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;

import java.util.ArrayList;
import java.util.List;


public class TaskListElementsActivity extends Activity {

    private TaskListElementsAdapter adapter;
    TaskList taskList;
    //Menu optionsMenu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.task_elements_menu, menu);
        //optionsMenu = menu;

        return true;
    }

    private void updateTitle() {
        if (taskList != null) {
            if (taskList.getDone()) {
                setTitle(taskList.getListName() + " (done)");
            } else {
                setTitle(taskList.getListName() + " (todo)");
            }

        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem toggleItem = menu.findItem(R.id.action_toggle_done);
        MenuItem refreshItem = menu.findItem(R.id.action_refresh);
        MenuItem newItem = menu.findItem(R.id.action_new);
        MenuItem deleteItem = menu.findItem(R.id.action_delete_list);
        if (taskList != null) {
            if (!taskList.getDone()) {
                toggleItem.setTitle("Mark as Finished");
            } else {
                toggleItem.setTitle("Mark as Unfinished");
            }

            toggleItem.setEnabled(true);
            refreshItem.setEnabled(true);
            newItem.setEnabled(true);
            deleteItem.setEnabled(true);
        } else {
            toggleItem.setTitle("Loading...");
            toggleItem.setEnabled(false);
            refreshItem.setEnabled(false);
            newItem.setEnabled(false);
            deleteItem.setEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            ToastMaker.makeShortToast("Refreshing...", this);
            adapter.loadObjects();
            return true;
        } else if (id == R.id.action_new) {
            startCreateNewTaskListElementDialog();
            return true;
        } else if (id == R.id.action_rename_list) {
            startRenameTaskListDialog();
            return true;
        } else if (id == R.id.action_delete_list) {
            startDeleteTaskListDialog();
            return true;
        } else if (id == R.id.action_toggle_done) {
            toggleFinished();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list_elements);

        setTitle("");

        String taskListID = getIntent().getStringExtra("taskListID");

        ParseQuery query = new ParseQuery("TaskList");
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        final ListView taskListElementsListView = (ListView) findViewById(R.id.taskListElementsListView);


        query.getInBackground(taskListID, new GetCallback() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e != null) TaskListElementsActivity.this.finish();
                taskList = (TaskList) object;

                invalidateOptionsMenu();

                TaskListElementsActivity.this.updateTitle();
                adapter = new TaskListElementsAdapter(TaskListElementsActivity.this, taskList);
                taskListElementsListView.setAdapter(adapter);
            }
        });
        taskListElementsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TaskListElement taskListElement = (TaskListElement) adapterView.getItemAtPosition(i);
                taskListElement.setDone(!taskListElement.getDone());
                taskListElement.saveEventually();
                adapter.reloadElements();
            }
        });
        taskListElementsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                TaskListElement taskListElement = (TaskListElement) adapterView.getItemAtPosition(i);
                Task_List_Long_Pressed_Dialog(taskListElement);
                return true;
            }
        });

    }

    public void Task_List_Long_Pressed_Dialog(final TaskListElement taskListElement) {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);



        myAlertDialog.setPositiveButton("Change title", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                changeTaskListElementTitle(taskListElement);
            }
        });

        myAlertDialog.setNegativeButton("Delete task", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                deleteElement(taskListElement);
            }


        });
        myAlertDialog.show();

    }

    private void deleteElement(final TaskListElement taskListElement) {
        final AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle("Delete task?");
        myAlertDialog.setMessage("Are you sure you want to delete this task?");

        myAlertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface arg0, int arg1)
            {
                final ProgressDialog resetProgress = ProgressDialog.show(TaskListElementsActivity.this, "Deleting task" , " Please wait ... ", true);
                taskListElement.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        // Returns to the previous activity
                        resetProgress.dismiss();
                        ToastMaker.makeLongToast("Task was deleted",getApplicationContext());
                        adapter.loadObjects();
                    }
                });

            }
        });

        myAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int arg1)
            {
                dialog.cancel();
            }
        });
        myAlertDialog.show();

    }

    private void changeTaskListElementTitle(final TaskListElement taskListElement) {

// get dialog_text_prompt.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.dialog_text_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);
        userInput.setText(taskListElement.getElementName());

        // set dialog message
        alertDialogBuilder.setTitle("Rename task")
                .setCancelable(false).setView(promptsView)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // get user input and set it to result
                        // edit text
                        final String name = userInput.getText().toString();
                        final ProgressDialog resetProgress = ProgressDialog.show(TaskListElementsActivity.this, "Changing name" , " Please wait ... ", true);
                        taskListElement.setElementName(name);
                        taskListElement.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                resetProgress.dismiss();

                                ToastMaker.makeLongToast("Name was changed",getApplicationContext());
                                adapter.loadObjects();


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

    private static class TaskListElementsAdapter extends BaseAdapter {
        private Context context;
        private TaskList taskList;

        public TaskList getTaskList() {
            return taskList;
        }

        private ArrayList<Item> items = new ArrayList<Item>();
        private ArrayList<TaskListElement> elements = new ArrayList<TaskListElement>();

//        public ArrayList<TaskListElement> getElements() {
//            return new ArrayList<TaskListElement>(elements);
//        }
//        public void setElements(ArrayList<TaskListElement> elements) {
//            this.elements = elements;
//            reloadElements();
//        }


        TaskListElementsAdapter(Context context, TaskList taskList) {
            this.context = context;
            this.taskList = taskList;
            loadObjects();
        }

        public void reloadElements() {
            if (elements != null) {
                items.clear();

                ArrayList<TaskListElement> unfinishedElements = new ArrayList<TaskListElement>(elements);
                ArrayList<TaskListElement> finishedElements = new ArrayList<TaskListElement>();

                for (TaskListElement element : unfinishedElements) {
                    if (element.getDone()) {
                        finishedElements.add(element);
                    }
                }
                unfinishedElements.removeAll(finishedElements);


                items.add(new SectionItem("Todo"));
                for (TaskListElement element : unfinishedElements) {
                    items.add(new EntryItem(element.getElementName(), "Created by " + element.getCreatedBy().getDisplayName(), element));
                }

                items.add(new SectionItem("Finished"));
                for (TaskListElement element : finishedElements) {
                    items.add(new EntryItem(element.getElementName(), "Finished by " + element.getFinishedBy().getDisplayName(), element));
                }


                notifyDataSetChanged();
            }

        }

        public void loadObjects() {
            ParseQuery<TaskListElement> taskListElementParseQuery = ParseQuery.getQuery(TaskListElement.class);
            taskListElementParseQuery.include("createdBy");
            taskListElementParseQuery.include("finishedBy");
            taskListElementParseQuery.orderByAscending("createdAt");
            taskListElementParseQuery.whereEqualTo("taskList", this.taskList);
            taskListElementParseQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);

            taskListElementParseQuery.findInBackground(new FindCallback<TaskListElement>() {
                @Override
                public void done(List<TaskListElement> taskListElements, ParseException e) {
                    if (e == null) {
                        elements = new ArrayList<TaskListElement>(taskListElements);
                        reloadElements();
                    }
                }
            });


        }

        @Override
        public int getCount() {
            if (items != null) {
                return items.size();
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int i) {
            if (items != null) {
                Item item = items.get(i);
                if (!item.isSection()) {
                    EntryItem entryItem = (EntryItem) item;
                    System.out.println(entryItem.element.getElementName());
                    return entryItem.element;
                } else {
                    return null;
                }
            } else {
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
                    SectionItem sectionItem = (SectionItem) item;
                    view = View.inflate(context, R.layout.list_element_section, null);
                    view.setBackgroundColor(Color.LTGRAY);
                    view.setOnClickListener(null);
                    view.setOnLongClickListener(null);
                    view.setLongClickable(false);
                    TextView title = (TextView) view.findViewById(R.id.sectionTitleTextView);

                    title.setText(sectionItem.getTitle());
                } else {
                    final EntryItem entryItem = (EntryItem) item;
                    view = View.inflate(context, R.layout.list_task_element_layout, null);
                    TextView title = (TextView) view.findViewById(R.id.textViewList);
                    TextView subTitle = (TextView) view.findViewById(R.id.textViewCreatedBy);
//                    ImageButton infoButton = (ImageButton) view.findViewById(R.id.buttonElementInfo);
                    title.setText(entryItem.title);
                    subTitle.setText(entryItem.subtitle);

//                    infoButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            System.out.println(entryItem.element.getElementName());
//                        }
//                    });
                }
            }

            return view;
        }

        private interface Item {
            public boolean isSection();
        }

        private class SectionItem implements Item {

            private final String title;

            public SectionItem(String title) {
                this.title = title;
            }

            public String getTitle() {
                return title;
            }

            @Override
            public boolean isSection() {
                return true;
            }
        }

        public class EntryItem implements Item {

            public final String title;
            public final String subtitle;
            public final TaskListElement element;

            public EntryItem(String title, String subtitle, TaskListElement element) {
                this.title = title;
                this.subtitle = subtitle;
                this.element = element;
            }

            @Override
            public boolean isSection() {
                return false;
            }

        }
    }

    private void startCreateNewTaskListElementDialog() {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.dialog_new_task_list_element, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.elementNameEditText);

        // set dialog message
        alertDialogBuilder.setTitle("Create new task list element")
                .setCancelable(false).setView(promptsView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // get user input and set it to result
                        // edit text
                        TaskListElement taskListElement = new TaskListElement();
                        String taskListName = userInput.getText().toString();
                        taskListElement.setElementName(taskListName);
                        taskListElement.setCreatedBy(User.getCurrentUser());
                        taskListElement.setTaskList(TaskListElementsActivity.this.adapter.getTaskList());
                        taskListElement.setUpdatedBy(User.getCurrentUser());

                        taskListElement.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                TaskListElementsActivity.this.adapter.loadObjects();
                                ToastMaker.makeShortToast("New Task List Element Created", TaskListElementsActivity.this);
                            }
                        });


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void startRenameTaskListDialog() {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.dialog_rename_task_list, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.renameTaskListEditText);
        userInput.setText(taskList.getListName());

        // set dialog message
        alertDialogBuilder.setTitle("Rename Task List")
                .setCancelable(false).setView(promptsView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // get user input and set it to result
                        // edit text
                        taskList.setListName(userInput.getText().toString());

                        taskList.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                TaskListElementsActivity.this.setTitle(taskList.getListName());
                                ToastMaker.makeShortToast("Task List Name Changed", TaskListElementsActivity.this);

                                Intent intent = new Intent("expense-need-to-refresh");
                                LocalBroadcastManager.getInstance(TaskListElementsActivity.this).sendBroadcast(intent);
                            }
                        });


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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

    private void startDeleteTaskListDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle("Are You Sure You Want To Delete This Task List?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        final ProgressDialog progressDialog = ProgressDialog.show(TaskListElementsActivity.this,
                                "Deleting List", " Please wait ... ", true);
                        taskList.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                progressDialog.dismiss();
                                Intent intent = new Intent("need-to-refresh");
                                LocalBroadcastManager.getInstance(TaskListElementsActivity.this).sendBroadcast(intent);
                                finish();
                            }
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void toggleFinished() {
        taskList.setDone(!taskList.getDone());
        updateTitle();
        taskList.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Intent intent = new Intent("need-to-refresh");
                LocalBroadcastManager.getInstance(TaskListElementsActivity.this).sendBroadcast(intent);
            }
        });
        invalidateOptionsMenu();
    }
}