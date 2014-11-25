package com.realkode.roomates.Me.NoHousehold;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.FunctionCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.RefreshCallback;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.ParseSubclassses.Invitation;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;

public class WithoutHouseholdActivity extends Activity {
    private Invitation selectedInvitation;
    private Button acceptHouseholdButton;
    private int lastPosition = 1000;
    private InvitationAdapter invitationAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_household_inactive);

        acceptHouseholdButton = (Button) findViewById(R.id.acceptButton);
        acceptHouseholdButton.setEnabled(false);
        acceptHouseholdButton.setOnClickListener(new AcceptInvitationOnClickListener());

        Button createHouseholdButton = (Button) findViewById(R.id.createHouseholdButton);
        createHouseholdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewHousehold();
            }
        });

        final ListView invitationsListView = (ListView) findViewById(R.id.invitationsListView);
        invitationAdapter = new InvitationAdapter(this);
        invitationsListView.setAdapter(invitationAdapter);
        invitationsListView.setOnItemClickListener(new InvitationsOnItemClickListener());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh_invitations:
                invitationAdapter.loadObjects();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.invitations_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void createNewHousehold() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptsView = layoutInflater.inflate(R.layout.dialog_new_household, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        final EditText householdNameInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

        alertDialogBuilder
                .setTitle(getString(R.string.dialog_title_create_new_household))
                .setView(promptsView)
                .setPositiveButton(getString(R.string.ok), new CreateHouseholdOnClickListener(householdNameInput));

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();

    }

    private class CreateHouseholdOnClickListener implements DialogInterface.OnClickListener {
        private EditText householdNameInput;

        private CreateHouseholdOnClickListener(EditText householdNameInput) {
            this.householdNameInput = householdNameInput;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            final Context context = WithoutHouseholdActivity.this;
            String householdName = householdNameInput.getText().toString();
            HouseholdCreator householdCreator = new HouseholdCreator(householdName);

            final ProgressDialog createHouseholdProgress = ProgressDialog
                    .show(context, context.getString(R.string.progress_dialog_title_creating_household),
                            context.getString(R.string.progress_dialog_message_creating_household), true);
            householdCreator.create(new createHouseholdCallback(createHouseholdProgress, context));
        }

        private class createHouseholdCallback extends FunctionCallback<Object> {
            private final ProgressDialog createHouseholdProgress;
            private final Context context;

            public createHouseholdCallback(ProgressDialog createHouseholdProgress, Context context) {
                this.createHouseholdProgress = createHouseholdProgress;
                this.context = context;
            }

            @Override
            public void done(Object o, ParseException e) {
                createHouseholdProgress.dismiss();
                if (e == null) {
                    ToastMaker.makeShortToast(R.string.toast_household_created_successfully, context);
                    final ProgressDialog refreshUserProgress = ProgressDialog.show(context, context.getString(R.string.refreshing_user), context.getString(R.string.please_wait));
                    User.getCurrentUser().refreshInBackground(new RefreshCallback() {

                        @Override
                        public void done(ParseObject object, ParseException e) {
                            refreshUserProgress.dismiss();
                            User.refreshChannels();
                            WithoutHouseholdActivity.this.finish();
                        }
                    });
                } else {
                    ToastMaker.makeShortToast(R.string.toast_created_household_unsuccesfully, context);
                }
            }
        }
    }

    private class InvitationsOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Invitation invitation = (Invitation) adapterView.getItemAtPosition(position);
            if (lastPosition != position) {
                try {
                    adapterView.getChildAt(lastPosition).setBackgroundColor(Color.WHITE);
                } catch (NullPointerException ignored) {}

                adapterView.getChildAt(position).setBackgroundColor(getResources().getColor(R.color.DarkBlue));
                adapterView.getChildAt(position).getBackground().setAlpha(100);
                selectedInvitation = invitation;
                lastPosition = position;
                acceptHouseholdButton.setEnabled(true);
            }
        }
    }

    private class AcceptInvitationOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final Context context = WithoutHouseholdActivity.this;

            final ProgressDialog acceptInvitationProgress = ProgressDialog
                    .show(context, context.getString(R.string.progressdialog_title_accepting_invitation),
                            context.getString(R.string.progressdialog_message_accepting_invitation), true);

            selectedInvitation.accept(new FunctionCallback<Object>() {
                @Override
                public void done(Object o, ParseException e) {
                    acceptInvitationProgress.dismiss();
                    if (e == null) {
                        final ProgressDialog refreshUserProgress = ProgressDialog.show(context, context.getString(R.string.refreshing_user), context.getString(R.string.please_wait));
                        User.getCurrentUser().refreshInBackground(new RefreshCallback() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                refreshUserProgress.dismiss();
                                User.refreshChannels();
                                WithoutHouseholdActivity.this.finish();
                            }
                        });
                    } else {
                        ToastMaker.makeShortToast(R.string.toast_could_not_accept_invitation, context);
                    }
                }
            });
        }
    }
}
