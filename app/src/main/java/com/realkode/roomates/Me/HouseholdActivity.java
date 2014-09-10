package com.realkode.roomates.Me;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RefreshCallback;
import com.realkode.roomates.Helpers.ToastMaker;
import com.realkode.roomates.ParseSubclassses.Invitation;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;

public class HouseholdActivity extends Activity {

    User user;
    Invitation selectedInvitation;
    Button acceptHouseholdButton;
    private int lastposition = 1000;

    // Called on activity created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        user = User.getCurrentUser();

        if (User.someoneIsLoggedInAndMemberOfAHousehold()) {
            setupForActiveHousehold();
        } else {
            setupForNoActiveHousehold();
        }
    }

    // when menu items are pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.actionNewHousehold:
                createNewHousehold();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // go here when user doesn't have a household
    // set up UI and listeners
    private void setupForNoActiveHousehold() {
        setContentView(R.layout.activity_household_inactive);

        setTitle("Invitations");


        acceptHouseholdButton = (Button) findViewById(R.id.acceptButton);
        acceptHouseholdButton.setEnabled(false);
        acceptHouseholdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HouseholdActivity.this.acceptInvitation();
            }
        });

        final ListView invitationsListView = (ListView) findViewById(R.id.invitationsListView);
        final ArrayList<Integer> clicked = new ArrayList<Integer>();
        InvitationAdapter invitationAdapter = new InvitationAdapter(this);
        invitationsListView.setAdapter(invitationAdapter);
        invitationsListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Invitation invitation = (Invitation) adapterView.getItemAtPosition(position);
                if (lastposition != position) {
                    try {
                        adapterView.getChildAt(lastposition).setBackgroundColor(Color.WHITE);
                    } catch (NullPointerException e) {
                        // no problem :)
                    }
                    adapterView.getChildAt(position).setBackgroundColor(Color.LTGRAY);
                    selectedInvitation = invitation;
                    lastposition = position;
                    acceptHouseholdButton.setEnabled(true);
                }
            }
        });
    }

    // go here when user has a household.
    // set up UI and listeners
    private void setupForActiveHousehold() {
        setContentView(R.layout.activity_household_active);
        ImageView logo = (ImageView) findViewById(R.id.imageViewLogoHousehold);

        final TextView textViewHouseholdName = (TextView) findViewById(R.id.textViewHouseholdName);
        Button leaveHouseholdBtn = (Button) findViewById(R.id.buttonLeaveHousehold);
        leaveHouseholdBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                leave_household_dialog();
            }
        });

        Button inviteUserBtn = (Button) findViewById(R.id.buttonInvite);
        inviteUserBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                inviteUserToHousehold();
            }
        });

        ListView membersListView = (ListView) findViewById(R.id.householdMembersListView);
        HouseholdMembersAdapter membersListViewAdapter = new HouseholdMembersAdapter(this);
        membersListView.setAdapter(membersListViewAdapter);


        ParseQuery query = new ParseQuery("Household");
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.getInBackground(User.getCurrentUser().getActiveHousehold().getObjectId(), new GetCallback() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                textViewHouseholdName.setText(parseObject.getString("householdName"));
            }
        });

    }

    // Dialog to leave household
    private void leave_household_dialog() {

        final AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle("Leave household");
        myAlertDialog.setMessage("Do you really want to leave the household?");

        myAlertDialog.setPositiveButton("Leave household", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                leaveHousehold();
            }
        });

        myAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.cancel();
            }
        });
        myAlertDialog.show();


    }
    // Accept invitation
    private void acceptInvitation() {
        if (selectedInvitation != null) {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("invitationId", selectedInvitation.getObjectId());
            final ProgressDialog acceptInvitationProgress = ProgressDialog.show(HouseholdActivity.this, "Accepting Invitation" , " Please wait ... ", true);
            ParseCloud.callFunctionInBackground("acceptInvitation", params,
                    new FunctionCallback<Object>() {
                        @Override
                        public void done(Object household, ParseException e) {
                            acceptInvitationProgress.dismiss();
                            if (e == null) {
                                user.refreshInBackground(new RefreshCallback() {

                                    @Override
                                    public void done(ParseObject object, ParseException e) {
                                        User.refreshChannels();
                                        HouseholdActivity.this.recreate();
                                    }
                                });
                            } else {
                                ToastMaker.makeShortToast("Could not accept invitation, please try again", HouseholdActivity.this);
                            }
                        }// end done
                    }
            ); // end cloud call
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        if (!User.someoneIsLoggedInAndMemberOfAHousehold()) {


            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.invitations_menu, menu);

        }
        return super.onCreateOptionsMenu(menu);
    }

    private void leaveHousehold() {
        HashMap<String, Object> params = new HashMap<String, Object>();
           ParseObject objectId = (ParseObject) user.get("activeHousehold");
        params.put("householdId", objectId.getObjectId().toString());
        final ProgressDialog leaveHouseholdProgress = ProgressDialog.show(HouseholdActivity.this, "Leaving Household" , " Please wait ... ", true);
        ParseCloud.callFunctionInBackground("leaveHousehold", params,
                new FunctionCallback<Object>() {
                    @Override
                    public void done(Object noReturnValue, ParseException e) {
                        leaveHouseholdProgress.dismiss();
                        if (e == null) {
                            // Success!
                            user.refreshInBackground(new RefreshCallback() {

                                @Override
                                public void done(ParseObject object, ParseException e) {
                                    User.refreshChannels();
                                    HouseholdActivity.this.recreate();
                                }
                            });
                        } else {
                            ToastMaker.makeShortToast("Could not leave household, please try again", HouseholdActivity.this);
                        }

                    }
                }
        );
    }

// Make household invite
    private void inviteUserToHousehold() {
        LayoutInflater inflater = LayoutInflater.from(HouseholdActivity.this);
        View inviteRoommateDialog = inflater.inflate(R.layout.dialog_invite_roommate, null);
        final EditText inviteeEditText = (EditText) inviteRoommateDialog
                .findViewById(R.id.inviteUsernameEditText);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HouseholdActivity.this);
        alertDialogBuilder.setTitle("Invite user to Household").setCancelable(false)
                .setPositiveButton("Invite", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inviteeUsername = inviteeEditText.getText().toString().toLowerCase(Locale.ENGLISH);
                        String householdObjectId = user.getParseObject("activeHousehold")
                                .getObjectId();

                        HashMap<String, Object> params = new HashMap<String, Object>();
                        params.put("username", inviteeUsername);
                        params.put("householdId", householdObjectId);
                        final ProgressDialog inviteProgress = ProgressDialog.show(HouseholdActivity.this, "Inviting User to Household" , " Please wait ... ", true);
                        // Calling cloudcode
                        ParseCloud.callFunctionInBackground("inviteUserToHousehold", params,
                                new FunctionCallback<Object>() {
                                    @Override
                                    public void done(Object noReturnValue, ParseException e) {
                                        inviteProgress.dismiss();
                                        System.out.println("Done inviting");
                                        if (e == null) {
                                            ToastMaker.makeShortToast("User was invited to household", HouseholdActivity.this);

                                        } else {
                                            CharSequence text = e.getMessage();
                                            Toast.makeText(HouseholdActivity.this, text, Toast.LENGTH_SHORT)
                                                    .show();
                                        }
                                    }
                                }
                        );

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).setView(inviteRoommateDialog);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
    }
// Creating a new household
    private void createNewHousehold() {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.dialog_new_household, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder.setTitle("Create new Household")
                .setCancelable(false).setView(promptsView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // get user input and set it to result
                        // edit text
                        String householdName = userInput.getText().toString();

                        HashMap<String, Object> params = new HashMap<String, Object>();
                        params.put("householdName", householdName);
                        final ProgressDialog createHouseholdProgress = ProgressDialog.show(HouseholdActivity.this, "Creating Household" , " Please wait ... ", true);
                        ParseCloud.callFunctionInBackground("createNewHousehold", params,
                                new FunctionCallback<Object>() {
                                    @Override
                                    public void done(Object obj, ParseException e) {
                                        createHouseholdProgress.dismiss();
                                        if (e == null) {
                                            CharSequence charSeq = "New Household was Created.";
                                            Toast.makeText(getApplicationContext(), charSeq,
                                                    Toast.LENGTH_SHORT).show();
                                            user.refreshInBackground(new RefreshCallback() {

                                                @Override
                                                public void done(ParseObject object, ParseException e) {
                                                    User.refreshChannels();
                                                    HouseholdActivity.this.recreate();
                                                }
                                            });
                                        } else {
                                            ToastMaker.makeShortToast("Could not create new household, please try again", HouseholdActivity.this);
                                        }
                                    }
                                }
                        );
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
