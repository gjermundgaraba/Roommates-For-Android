package com.realkode.roomates.Me;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.RefreshCallback;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.ParseSubclassses.Invitation;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;

import java.util.HashMap;

public class WithoutHouseholdActivity extends Activity {
    private Invitation selectedInvitation;
    private Button acceptHouseholdButton;
    private int lastPosition = 1000;

    public Invitation getSelectedInvitation() {
        return selectedInvitation;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_household_inactive);

        acceptHouseholdButton = (Button) findViewById(R.id.acceptButton);
        acceptHouseholdButton.setEnabled(false);
        acceptHouseholdButton.setOnClickListener(new AcceptInvitationOnClickListener(this));

        final ListView invitationsListView = (ListView) findViewById(R.id.invitationsListView);
        InvitationAdapter invitationAdapter = new InvitationAdapter(this);
        invitationsListView.setAdapter(invitationAdapter);
        invitationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Invitation invitation = (Invitation) adapterView.getItemAtPosition(position);
                if (lastPosition != position) {
                    try {
                        adapterView.getChildAt(lastPosition).setBackgroundColor(Color.WHITE);
                    } catch (NullPointerException ignored) { }

                    adapterView.getChildAt(position).setBackgroundColor(Color.LTGRAY);
                    selectedInvitation = invitation;
                    lastPosition = position;
                    acceptHouseholdButton.setEnabled(true);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionNewHousehold:
                createNewHousehold();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!User.loggedInAndMemberOfAHousehold()) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.invitations_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void createNewHousehold() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptsView = layoutInflater.inflate(R.layout.dialog_new_household, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

        alertDialogBuilder.setTitle("Create new Household")
                .setCancelable(false).setView(promptsView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String householdName = userInput.getText().toString();

                        HashMap<String, Object> params = new HashMap<String, Object>();
                        params.put("householdName", householdName);
                        final ProgressDialog createHouseholdProgress = ProgressDialog.show(WithoutHouseholdActivity.this, "Creating Household" , " Please wait ... ", true);
                        ParseCloud.callFunctionInBackground("createNewHousehold", params,
                                new FunctionCallback<Object>() {
                                    @Override
                                    public void done(Object obj, ParseException e) {
                                        createHouseholdProgress.dismiss();
                                        if (e == null) {
                                            CharSequence charSeq = "New Household was Created.";
                                            Toast.makeText(getApplicationContext(), charSeq,
                                                    Toast.LENGTH_SHORT).show();
                                            User.getCurrentUser().refreshInBackground(new RefreshCallback() {

                                                @Override
                                                public void done(ParseObject object, ParseException e) {
                                                    User.refreshChannels();
                                                    WithoutHouseholdActivity.this.finish();
                                                }
                                            });
                                        } else {
                                            ToastMaker.makeShortToast("Could not create new household, please try again", WithoutHouseholdActivity.this);
                                        }
                                    }
                                }
                        );
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();

    }
}
