package com.elogstation.client.elogstationdrive.httpclient;

import android.os.AsyncTask;
import android.util.Log;

import com.elogstation.client.elogstationdrive.Constants;
import com.elogstation.client.elogstationdrive.db.DatabaseHelper;
import com.elogstation.client.elogstationdrive.db.DeviceModel;
import com.elogstation.client.elogstationdrive.db.EldModel;
import com.elogstation.client.elogstationdrive.schedule.UploadTrackingJobService;
import com.elogstation.client.elogstationdrive.util.StatusType;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

public class HttpPostAsyncTask extends AsyncTask<String, Void, Void> {

    // This is the JSON body of the post
    JSONObject postData;
    List<DeviceModel> deviceModels;
    RequestType type;
    CustomCallback callback;
    public static String statusType = "";

    // This is a constructor that allows you to pass in the JSON body
    public HttpPostAsyncTask(Map<String, String> postData, RequestType type, CustomCallback callback) {
        if (postData != null) {
            this.postData = new JSONObject(postData);
        }
        this.type = type;
        this.callback = callback;
    }

    static HttpClient httpClient;

    public HttpPostAsyncTask(List<DeviceModel> deviceModels, RequestType type, CustomCallback callback) {
        if (deviceModels != null) {
            this.deviceModels = deviceModels;
        }

        this.type = type;
        this.callback = callback;
    }

    // This is a function that we are overriding from AsyncTask. It takes Strings as parameters because that is what we defined for the parameters of our async task
    @Override
    protected Void doInBackground(String... params) {

        try {
            String url = params[0];
            if(httpClient == null) {
                httpClient = new DefaultHttpClient();
            }
            HttpPost httpPost = new HttpPost(url);
            String sub = "";
            if(type == RequestType.LOGIN) {
                String username = postData.getString("username");
                sub = postData.getString("sub");
                String password = postData.getString("password");
                httpPost.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(username,
                        password), "UTF-8", false));

            }

            if(type == RequestType.POSTSTATUS){
                JSONObject object = new JSONObject();
                object.put("statusType", postData.getString("statusType"));
                object.put("deviceTime", postData.getString("deviceTime"));
                String message = object.toString();
                httpPost.setEntity(new StringEntity(message, "UTF8"));
            }


            if(type == RequestType.POSTTRACKING){

//                JSONArray mJSONArray = new JSONArray(Arrays.asList(deviceModels.toString()));
                httpPost.setEntity(new StringEntity(deviceModels.toString(), "UTF8"));
            }

            httpPost.setHeader("Content-Type", "application/json");

            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            final String responseBody = EntityUtils.toString(response.getEntity());
//            Log.i(TAG, "Signed in as: " + responseBody);
            if (statusCode != HttpStatus.SC_OK) {
                callback.completionHandler(type, false, null, null);
                return null;
            }

//            String deviceId = jsonObj.getString("deviceId");
            if(type == RequestType.LOGIN) {
                JSONObject jsonObj = new JSONObject(responseBody);
                String subNew = jsonObj.getString("sub");
                if (sub.contains(subNew)) {
                    callback.completionHandler(type, true, null, null);
                    return null;
                }
            } else if(type == RequestType.STATUS || type == RequestType.POSTSTATUS) {

                try{

                    JSONObject jsonObj = new JSONObject(responseBody);
                    String statusType = jsonObj.getString("statusType");
                    String deviceTime = jsonObj.getString("deviceTime");

                    this.statusType = statusType;

                    callback.completionHandler(type, true, statusType, deviceTime);
                    return null;
                } catch(Exception e){
                    callback.completionHandler(type, true, null, null);
                }
            } else if(type == RequestType.HEALTH){
                if (statusCode == HttpStatus.SC_OK) {
                    callback.completionHandler(type, true, null, null);
                }
            } else if(type == RequestType.GETMYELDS){

                if (statusCode == HttpStatus.SC_OK) {

                    callback.completionHandler(type, true, responseBody, null);
                }
            } else if(type == RequestType.POSTTRACKING) {

                ArrayList<String> stringArray = new ArrayList<String>();

                JSONArray jsonArray = new JSONArray(responseBody);

                for (int i = 0; i < jsonArray.length(); i++) {
                    stringArray.add(jsonArray.getString(i));
                }
                String[] ids = stringArray.toArray(new String[0]);

                callback.completionHandler(type, true, ids, null);
            }
//            infoMessage.setText("Signed in as: " + responseBody);


        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
            callback.completionHandler(type, false, null, null);
        }
        return null;
    }
}
