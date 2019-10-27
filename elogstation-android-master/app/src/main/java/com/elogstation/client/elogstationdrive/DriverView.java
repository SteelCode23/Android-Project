package com.elogstation.client.elogstationdrive;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.elogstation.client.elogstationdrive.httpclient.CustomCallback;
import com.elogstation.client.elogstationdrive.httpclient.HttpPostAsyncTask;
import com.elogstation.client.elogstationdrive.httpclient.RequestType;
import com.elogstation.client.elogstationdrive.schedule.UploadTrackingJobService;
import com.elogstation.client.elogstationdrive.util.StatusType;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class DriverView extends AppCompatActivity {

//    private TextView mTextMessage;
    private GoogleSignInAccount account;
    String TAG = "DriverView";
    Fragment selectedFragment = null;

    boolean homeWasOpened = false;
    boolean dashboardWasOpened = false;
    boolean profileWasOpened = false;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentHome fragmentHome = FragmentHome.newInstance();
            FragmentDashboard fragmentDashboard = FragmentDashboard.newInstance();
            FragmentProfile fragmentProfile = FragmentProfile.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    transaction.hide(fragmentDashboard);
                    transaction.hide(fragmentProfile);
                    selectedFragment = FragmentHome.newInstance();
                    break;
                case R.id.navigation_dashboard:
                    transaction.hide(fragmentHome);
                    transaction.hide(fragmentProfile);
                    selectedFragment = FragmentDashboard.newInstance();
                    FragmentDashboard.enableSQLInserting = true;
                    break;
                case R.id.navigation_notifications:
                    transaction.hide(fragmentDashboard);
                    transaction.hide(fragmentHome);
                    selectedFragment = FragmentProfile.newInstance();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("account", account);
                    selectedFragment.setArguments(bundle);
                    break;
            }
            selectedFragment.setUserVisibleHint(true);
            transaction.show(selectedFragment);
            transaction.commit();
            return true;
        }
    };

    boolean fragmentDisplayHelper(Fragment fragment, Fragment selectedFragment, FragmentTransaction transaction){
        if(fragment != selectedFragment) {
            transaction.hide(fragment);
            return true;
        }
        else {
            transaction.show(fragment);
            return false;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_view);

//        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        account = (GoogleSignInAccount) getIntent().getParcelableExtra("account");

        Log.d(TAG, "account name=" + account.getDisplayName());

        FragmentProfile fragmentProfile = FragmentProfile.newInstance();
        Bundle bundle = new Bundle();
        bundle.putParcelable("account", account);
        fragmentProfile.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frame_layout, fragmentProfile);
        transaction.add(R.id.frame_layout, FragmentDashboard.newInstance());
        transaction.hide(FragmentDashboard.newInstance());
        transaction.hide(fragmentProfile);
        transaction.add(R.id.frame_layout, FragmentHome.newInstance());
        transaction.show(FragmentHome.newInstance());
        transaction.commit();

        homeWasOpened = true;

        Intent serviceIntent = new Intent(this, UploadTrackingJobService.class);
        startService(serviceIntent);

    }




}
