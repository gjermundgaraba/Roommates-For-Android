package com.realkode.roomates.Tasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.realkode.roomates.Helpers.Constants;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.ParseSubclassses.TaskList;
import com.realkode.roomates.ParseSubclassses.TaskListElement;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;
import com.realkode.roomates.Tasks.Adapters.TaskListElementsAdapter;

public class TaskListElementsActivity extends Activity {
    private TaskListElementsAdapter adapter;
    private TaskList taskList;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_elements_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem toggleItem = menu.findItem(R.id.action_toggle_done);
        MenuItem refreshItem = menu.findItem(R.id.action_refresh);
        MenuItem newItem = menu.findItem(R.id.action_new);
        MenuItem deleteItem = menu.findItem(R.id.action_delete_list);
        if (taskList != null) {
            if (!taskList.getDone()) {
                toggleItem.setTitle(R.string.options_menu_task_list_mark_as_finished);
            } else {
                toggleItem.setTitle(R.string.options_menu_task_list_mark_as_ufinished);
            }

            toggleItem.setEnabled(true);
            refreshItem.setEnabled(true);
            newItem.setEnabled(true);
            deleteItem.setEnabled(true);
        } else {
            toggleItem.setTitle(R.string.options_menu_task_list_loading);
            toggleItem.setEnabled(false);
            refreshItem.setEnabled(false);
            newItem.setEnabled(false);
            deleteItem.setEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            ToastMaker.makeShortToast(R.string.toast_task_list_elements_refresh, this);
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

        setTitle("Task List"); // Will be updated later
        String taskListID = getIntent().getStringExtra("taskListID");

        final ListView taskListElementsListView = (ListView) findViewById(R.id.taskListElementsListView);

        retrieveTaskListAndSetUpAdapter(taskListID, taskListElementsListView);

        setUpOnItemClickListeners(taskListElementsListView);

    }

    private void setUpOnItemClickListeners(ListView taskListElementsListView) {
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
                TaskListLongPressedDialog(taskListElement);
                return true;
            }
        });
    }

    private void retrieveTaskListAndSetUpAdapter(String taskListID, final ListView taskListElementsListView) {
        ParseQuery<TaskList> query = new ParseQuery<TaskList>(TaskList.class);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.getInBackground(taskListID, new GetCallback<TaskList>() {
            @Override
            public void done(TaskList taskList, ParseException exception) {
                if (exception != null) {
                    TaskListElementsActivity.this.finish();
                }

                TaskListElementsActivity.this.taskList = taskList;
                setTitle(taskList.getListName());
                invalidateOptionsMenu();

                adapter = new TaskListElementsAdapter(TaskListElementsActivity.this, taskList);
                taskListElementsListView.setAdapter(adapter);
            }
        });
    }

    public void TaskListLongPressedDialog(final TaskListElement taskListElement) {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);

        myAlertDialog.setPositiveButton(getString(R.string.alert_dialog_task_list_long_press_change_title),
                new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface arg0, int arg1) {
                changeTaskListElementTitle(taskListElement);
            }
        });

        myAlertDialog.setNegativeButton(getString(R.string.alert_dialog_task_list_long_press_delete),
                new DialogInterface.OnClickListener()
         {
            public void onClick(DialogInterface arg0, int arg1) {
                deleteElement(taskListElement);
            }
        });

        myAlertDialog.show();

    }

    private void deleteElement(final TaskListElement taskListElement) {
        final AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle(getString(R.string.alert_dialog_delete_task_title));
        myAlertDialog.setMessage(getString(R.string.alert_dialog_delete_task_message));

        myAlertDialog.setPositiveButton(getString(R.string.alert_dialog_delete_task_yes), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {
                final ProgressDialog resetProgress = ProgressDialog.show(TaskListElementsActivity.this, "Deleting task" , " Please wait ... ", true);
                taskListElement.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        resetProgress.dismiss();
                        ToastMaker.makeLongToast("Task was deleted", getApplicationContext());
                        adapter.loadObjects();
                    }
                });

            }
        });
        myAlertDialog.setNegativeButton(getString(R.string.alert_dialog_delete_task_cancel), null);
        myAlertDialog.show();

    }

    private void changeTaskListElementTitle(final TaskListElement taskListElement) {

        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.dialog_text_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);
        userInput.setText(taskListElement.getElementName());

        alertDialogBuilder.setTitle("Rename task")
                .setCancelable(false).setView(promptsView)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
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

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
    }

    private void startCreateNewTaskListElementDialog() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.dialog_new_task_list_element, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.elementNameEditText);

        alertDialogBuilder.setTitle("Create new task list element")
                .setCancelable(false).setView(promptsView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
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

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void startRenameTaskListDialog() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.dialog_rename_task_list, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.renameTaskListEditText);
        userInput.setText(taskList.getListName());

        alertDialogBuilder.setTitle("Rename Task List")
                .setCancelable(false).setView(promptsView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
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

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
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

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void toggleFinished() {
        taskList.setDone(!taskList.getDone());
        taskList.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Intent intent = new Intent(Constants.NEED_TO_REFRESH);
                LocalBroadcastManager.getInstance(TaskListElementsActivity.this).sendBroadcast(intent);
            }
        });
        invalidateOptionsMenu();
    }
}
