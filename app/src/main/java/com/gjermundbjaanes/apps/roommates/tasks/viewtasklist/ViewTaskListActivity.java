package com.gjermundbjaanes.apps.roommates.tasks.viewtasklist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
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

import com.gjermundbjaanes.apps.roommates.R;
import com.gjermundbjaanes.apps.roommates.helpers.Constants;
import com.gjermundbjaanes.apps.roommates.helpers.ToastMaker;
import com.gjermundbjaanes.apps.roommates.helpers.Utils;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.TaskList;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.TaskListElement;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class ViewTaskListActivity extends Activity {
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

        final ListView taskListElementsListView = (ListView) findViewById(R.id.taskListElementsListView);

        String taskListID = getIntent().getStringExtra("taskListID");
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
        Utils.setSafeQueryCaching(query);
        query.getInBackground(taskListID, new TaskListGetCallback(taskListElementsListView));
    }

    void TaskListLongPressedDialog(final TaskListElement taskListElement) {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);

        myAlertDialog.setPositiveButton(getString(R.string.alert_dialog_task_list_long_press_change_title),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        changeTaskListElementTitle(taskListElement);
                    }
                });

        myAlertDialog.setNegativeButton(getString(R.string.alert_dialog_task_list_long_press_delete),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        deleteElement(taskListElement);
                    }
                });

        myAlertDialog.show();

    }

    private void deleteElement(final TaskListElement taskListElement) {
        final AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle(getString(R.string.alert_dialog_delete_task_title))
                .setMessage(getString(R.string.alert_dialog_delete_task_message))
                .setPositiveButton(getString(R.string.alert_dialog_delete_task_yes),
                        new DeleteTaskListElementOnClickListener(this, adapter, taskListElement))
                .setNegativeButton(getString(R.string.alert_dialog_delete_task_cancel), null);

        myAlertDialog.show();

    }

    private void changeTaskListElementTitle(final TaskListElement taskListElement) {

        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.dialog_text_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
        userInput.setText(taskListElement.getElementName());

        alertDialogBuilder.setTitle(getString(R.string.rename_task)).setCancelable(false).setView(promptsView)
                .setPositiveButton(getString(R.string.save),
                        new ChangeTaskListElementTitleOnClickListener(this, userInput, taskListElement, adapter))
                .setNegativeButton(getString(R.string.cancel), null);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
    }

    private void startCreateNewTaskListElementDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptsView = layoutInflater.inflate(R.layout.dialog_new_task_list_element, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.elementNameEditText);

        alertDialogBuilder.setTitle(getString(R.string.alert_dialog_create_new_task_list_element_title))
                .setView(promptsView)
                .setPositiveButton(getString(R.string.alert_dialog_create_new_task_list_element_message),
                        new CreateNewTaskListElementOnClickListener(this, userInput, adapter))
                .setNegativeButton(getString(R.string.cancel), null);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void startRenameTaskListDialog() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.dialog_rename_task_list, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.renameTaskListEditText);
        userInput.setText(taskList.getListName());

        alertDialogBuilder.setTitle(getString(R.string.rename_task_list)).setCancelable(false).setView(promptsView)
                .setPositiveButton(getString(R.string.ok), new RenameTaskListOnClickListener(this, taskList, userInput))
                .setNegativeButton(getString(R.string.cancel), null);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
    }

    private void startDeleteTaskListDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getString(R.string.delete_task_list_confirmation_title))
                .setPositiveButton(getString(R.string.yes), new DeleteTaskListOnClickListener())
                        .setNegativeButton(getString(R.string.no), null);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void toggleFinished() {
        taskList.setDone(!taskList.getDone());
        taskList.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Intent intent = new Intent(Constants.NEED_TO_REFRESH);
                LocalBroadcastManager.getInstance(ViewTaskListActivity.this).sendBroadcast(intent);
            }
        });
        invalidateOptionsMenu();
    }

    private class TaskListGetCallback extends GetCallback<TaskList> {
        private final ListView taskListElementsListView;

        public TaskListGetCallback(ListView taskListElementsListView) {
            this.taskListElementsListView = taskListElementsListView;
        }

        @Override
        public void done(TaskList taskList, ParseException exception) {
            if (exception != null) {
                ViewTaskListActivity.this.finish();
                return;
            }

            ViewTaskListActivity.this.taskList = taskList;
            setTitle(taskList.getListName());
            invalidateOptionsMenu();

            adapter = new TaskListElementsAdapter(ViewTaskListActivity.this, taskList);
            taskListElementsListView.setAdapter(adapter);
        }
    }

    private class DeleteTaskListOnClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            final Context context = ViewTaskListActivity.this;
            final ProgressDialog progressDialog = ProgressDialog
                    .show(context, context.getString(R.string.deleting_list), context.getString(R.string.please_wait),
                            true);

            taskList.deleteInBackground(new DeleteCallback() {
                @Override
                public void done(ParseException e) {
                    progressDialog.dismiss();
                    Intent intent = new Intent(Constants.NEED_TO_REFRESH);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    ViewTaskListActivity.this.finish();
                }
            });
        }
    }
}
