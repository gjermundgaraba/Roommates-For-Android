package com.realkode.roomates.Me;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.realkode.roomates.NotLoggedIn.LoginActivity;
import com.realkode.roomates.ParseSubclassses.User;
import com.realkode.roomates.R;
import com.realkode.roomates.RefreshableFragment;

/**
 * Fragment for the me-view
 */
public class MeFragment extends Fragment implements RefreshableFragment {
    ParseImageView profilePictureView;
    User currentUser;
    TextView nameTextView;
    TextView nameTextView2;
    TextView emailTextView;
    Button editProfileButton;

    @Override
    public void onResume() {
        super.onResume();
        setUpUI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_me, container, false);
        
        // Get UI-elements
        Button logoutButton = (Button) rootView.findViewById(R.id.buttonLogout);
        editProfileButton = (Button) rootView.findViewById(R.id.buttonEditProfile);
        Button householdButton = (Button) rootView.findViewById(R.id.buttonHousehold);
        nameTextView = (TextView) rootView.findViewById(R.id.textName);
        nameTextView2 = (TextView) rootView.findViewById(R.id.textViewDisplayName);
        emailTextView = (TextView) rootView.findViewById(R.id.textViewEmail);
        profilePictureView = (ParseImageView) rootView.findViewById(R.id.profilePictureView);

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
            // Household
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, HouseholdActivity.class);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            // Log out
            @Override
            public void onClick(View view) {
                // logging out

               logout_dialog();
            }
        });
        return rootView;
    }
    public void setUpUI(){
        ParseFile profilePictureParseFile = currentUser.getProfilePicture();

        if (profilePictureParseFile != null){
            profilePictureView.setParseFile(profilePictureParseFile);
            profilePictureView.loadInBackground();
        }

        if (ParseFacebookUtils.isLinked(currentUser)) {
            editProfileButton.setEnabled(false);
            editProfileButton.setTextColor(Color.LTGRAY);
        }

        String displayName = currentUser.getDisplayName();
        nameTextView.setText(displayName);
        nameTextView2.setHint(displayName);
        emailTextView.setHint(currentUser.getEmail());
    }

    // Refreshing fragment
    @Override
    public void refreshFragment() {
        setUpUI();
    }

    // Dialog to log out
    public void logout_dialog()
    {
        final AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getActivity());
        myAlertDialog.setTitle("Log out");
        myAlertDialog.setMessage("Do you really want to log out?");

        myAlertDialog.setPositiveButton("Log out", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface arg0, int arg1)
            {
                logout();
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

    // Log out the user and delete caches.
    private void logout() {
        if (com.facebook.Session.getActiveSession() != null)
        {
            com.facebook.Session.getActiveSession().closeAndClearTokenInformation();
        }
        ParseUser.logOut();
        ParseQuery.clearAllCachedResults();
        User.refreshChannels();
        Context context = getActivity();
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}