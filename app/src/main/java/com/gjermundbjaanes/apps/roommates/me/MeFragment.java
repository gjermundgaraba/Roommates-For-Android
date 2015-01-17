package com.gjermundbjaanes.apps.roommates.me;

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
import android.widget.Button;
import android.widget.TextView;

import com.gjermundbjaanes.apps.roommates.R;
import com.gjermundbjaanes.apps.roommates.RefreshableFragment;
import com.gjermundbjaanes.apps.roommates.helpers.Constants;
import com.gjermundbjaanes.apps.roommates.me.householdsettings.MyHouseholdActivity;
import com.gjermundbjaanes.apps.roommates.me.nohousehold.WithoutHouseholdActivity;
import com.gjermundbjaanes.apps.roommates.me.profileinformation.EditProfileActivity;
import com.gjermundbjaanes.apps.roommates.notloggedin.login.LoginActivity;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.User;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class MeFragment extends Fragment implements RefreshableFragment {
    private ParseImageView profilePictureView;
    private User currentUser;
    private TextView nameHeader;
    private TextView nameTextView;
    private TextView emailTextView;
    private Button editProfileButton;

    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshFragment();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        setUpUI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_me, container, false);

        Button logoutButton = (Button) rootView.findViewById(R.id.buttonLogout);
        Button householdButton = (Button) rootView.findViewById(R.id.buttonHousehold);
        editProfileButton = (Button) rootView.findViewById(R.id.buttonEditProfile);
        nameHeader = (TextView) rootView.findViewById(R.id.nameHeader);
        nameTextView = (TextView) rootView.findViewById(R.id.textViewDisplayName);
        emailTextView = (TextView) rootView.findViewById(R.id.textViewEmail);
        profilePictureView = (ParseImageView) rootView.findViewById(R.id.profilePictureView);
        profilePictureView.setPlaceholder(getResources().getDrawable(R.drawable.placeholder));

        currentUser = User.getCurrentUser();

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        householdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent;
                if (User.loggedInAndMemberOfAHousehold()) {
                    intent = new Intent(context, MyHouseholdActivity.class);
                } else {
                    intent = new Intent(context, WithoutHouseholdActivity.class);
                }
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout_dialog();
            }
        });

        setUpBroadcastReceiver();

        return rootView;
    }

    private void setUpBroadcastReceiver() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        broadcastManager.registerReceiver(mMessageReceiver, new IntentFilter(Constants.NEED_TO_REFRESH));
    }

    void setUpUI() {
        currentUser = User.getCurrentUser();
        ParseFile profilePictureParseFile = currentUser.getProfilePicture();

        if (profilePictureParseFile != null) {
            profilePictureView.setParseFile(profilePictureParseFile);
            profilePictureView.loadInBackground();
        }

        if (ParseFacebookUtils.isLinked(currentUser)) {
            editProfileButton.setEnabled(false);
            editProfileButton.setTextColor(Color.LTGRAY);
        }

        String displayName = currentUser.getDisplayName();
        nameHeader.setText(displayName);
        nameTextView.setHint(displayName);
        emailTextView.setHint(currentUser.getEmail());
    }

    @Override
    public void refreshFragment() {
        setUpUI();
    }

    void logout_dialog() {
        final AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getActivity());
        myAlertDialog.setTitle(R.string.dialog_title_log_out);
        myAlertDialog.setMessage(R.string.dialog_message_log_out);

        myAlertDialog.setPositiveButton(R.string.button_log_out, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                performUserLogOut();
            }
        });

        myAlertDialog.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.cancel();
            }
        });
        myAlertDialog.show();

    }

    private void performUserLogOut() {
        clearFacebookSession();
        logOutParseUser();
        takeUserToLogInScreen();
    }

    private void clearFacebookSession() {
        if (com.facebook.Session.getActiveSession() != null) {
            com.facebook.Session.getActiveSession().closeAndClearTokenInformation();
        }
    }

    private void logOutParseUser() {
        ParseUser.logOut();
        ParseQuery.clearAllCachedResults();
        User.refreshChannels();
    }

    private void takeUserToLogInScreen() {
        Context context = getActivity();
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}