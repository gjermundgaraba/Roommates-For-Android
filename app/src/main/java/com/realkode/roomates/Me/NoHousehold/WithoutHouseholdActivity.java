package com.realkode.roomates.Me.NoHousehold;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.realkode.roomates.ParseSubclassses.Invitation;
import com.realkode.roomates.R;

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
                    } catch (NullPointerException ignored) {
                    }

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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.invitations_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void createNewHousehold() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptsView = layoutInflater.inflate(R.layout.dialog_new_household, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        EditText householdNameInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

        alertDialogBuilder.setTitle(getString(R.string.dialog_title_create_new_household)).setView(promptsView)
                .setPositiveButton(getString(R.string.dialog_positive_button_create_new_household),
                        new CreateHouseholdOnClickListener(householdNameInput, this));

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();

    }
}
