package com.elogstation.client.elogstationdrive;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.elogstation.client.elogstationdrive.db.DatabaseHelper;
import com.elogstation.client.elogstationdrive.schedule.UploadTrackingJobService;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class FragmentProfile extends Fragment {

    GoogleSignInAccount account;
    TextView profileName;
    TextView profileEmail;
    ImageView profilePic;
    Button signOutButton;

    static FragmentProfile fragmentProfile;
    public static FragmentProfile newInstance() {
        if(fragmentProfile == null){
            FragmentProfile fragment = new FragmentProfile();
            fragmentProfile = fragment;
        }
        return fragmentProfile;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        account = getArguments().getParcelable("account");

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profileName = (TextView) view.findViewById(R.id.profileName);
        profileEmail = (TextView) view.findViewById(R.id.profileEmail);
        profilePic = (ImageView) view.findViewById(R.id.profilePic);

        showProfileInfo();

        signOutButton = (Button) view.findViewById(R.id.signOutButton);
//        Context that = getContext();

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);// New activity
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("logout", "true");

                //disconnect eld device
                FragmentDashboard.mEldManager.DisconnectEld();
                FragmentDashboard.eldConnectedBluetooth = false;

                //stop the timer service to upload tracking info
                UploadTrackingJobService.timer.cancel();

                //stop inserting to local database
                FragmentDashboard.enableSQLInserting = false;

                //delete everything from local database
                if (db == null) {
                    db = new DatabaseHelper(getContext());
                }
                db.deleteAll();

                startActivity(intent);
                getActivity().finish();
            }

        });

        return view;

    }
    DatabaseHelper db;

    private void showProfileInfo(){

        if(profileName != null)
            profileName.setText(account.getDisplayName());
        if(profileEmail != null)
            profileEmail.setText(account.getEmail());

        if(profilePic != null && account.getPhotoUrl() != null)
//            Glide.with(this).load(account.getPhotoUrl()).into(profilePic);

            Glide.with(this)
                    .load(account.getPhotoUrl().toString())
                    .listener(new RequestListener() {
                        public boolean onException(Exception e,Object o,Target t,boolean b)
                        {return false;}

                        public boolean onLoadFailed(Exception e, Object model, Target target, boolean isFirstResource) {
                            // log exception
                            Log.e("TAG", "Error loading image", e);
                            return false; // important to return false so the error placeholder can be placed
                        }
                        public boolean onResourceReady(Object o,Object p,Target t,boolean b,boolean c)
                        {return false;}})
                    .fitCenter()
                    .into(profilePic);

    }
}
