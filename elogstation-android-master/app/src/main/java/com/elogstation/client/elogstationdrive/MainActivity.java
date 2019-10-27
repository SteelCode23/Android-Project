package com.elogstation.client.elogstationdrive;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.elogstation.client.elogstationdrive.httpclient.CustomCallback;
import com.elogstation.client.elogstationdrive.httpclient.HttpPostAsyncTask;
import com.elogstation.client.elogstationdrive.httpclient.RequestType;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements CustomCallback {

    GoogleApiClient mGoogleApiClient;
    GoogleSignInClient mGoogleSignInClient;
    SignInButton signInButton;
    TextView infoMessage;
    TextView loginMessage;
    int RC_SIGN_IN = 9048;
    String TAG = "MainActivity";

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        authenticationValid(account);
        updateUI(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Constants.clientId)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }

        });

        infoMessage = findViewById(R.id.info_message);

        loginMessage = findViewById(R.id.login_message);

        String toLogoutUser = getIntent().getStringExtra("logout");
        if(toLogoutUser != null && toLogoutUser.equals("true")){
            logoutUser();
        }
    }

    ProgressDialog dialog;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Context that = this;
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            dialog = ProgressDialog.show(that, "",
                    getString(R.string.loggingin), false);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            authenticationValid(account);
            // Signed in successfully, show authenticated UI.
//            updateUI(true);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d(TAG, "signInResult:failed code=" + e.getStatusCode());
            infoMessage.setText("signInResult:failed code=" + e.getStatusCode());
//            updateUI(false);
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            signInButton.setVisibility(View.GONE);
            loginMessage.setVisibility(View.GONE);
        } else {
            signInButton.setVisibility(View.VISIBLE);
            loginMessage.setVisibility(View.VISIBLE);
        }
    }

    GoogleSignInAccount account;

    private void authenticationValid(GoogleSignInAccount account){

    this.account = account;
//        openDriverView(account);

        if (account == null) {
            logoutUser();
            return;
        }


        String username = account.getId() + "-" + Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String password = account.getIdToken();

        try {

            Map<String, String> postData = new HashMap<>();
            postData.put("username", username);
            postData.put("sub", account.getId());

            postData.put("password", password);
            HttpPostAsyncTask task = new HttpPostAsyncTask(postData, RequestType.LOGIN, this);
            task.execute(Constants.host + Constants.login);

            infoMessage.setText("");
//
//        } catch (IOException e) {
//            Log.e(TAG, "IO Error sending ID token to backend.", e);
//            infoMessage.setText("IO Error sending ID token to backend.");
//            logoutUser();
        } catch (Exception e) {
            Log.e(TAG, "General Error sending ID token to backend.", e);
            infoMessage.setText("General Error sending ID token to backend.");
            logoutUser();
        }
    }

    @Override
    public void completionHandler(RequestType type, Boolean isSuccess, Object info, Object info2) {
        if(type == RequestType.LOGIN){
            if(isSuccess == true){
                openDriverView(this.account);
            } else {
                logoutUser();
            }

        }
    }

    private void openDriverView(GoogleSignInAccount account){

        if(dialog != null){
            dialog.cancel();
        }
        if (account != null) {
            Intent intent = new Intent(this, DriverView.class);// New activity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("account", account);
            startActivity(intent);
            FragmentDashboard.startedSearchingBluetoothDevices = false;
            FragmentDashboard.eldSelectedFromAPI = "";
            finish();
        }

    }

    private void logoutUser(){
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(false);
                    }
                });
    }
}
