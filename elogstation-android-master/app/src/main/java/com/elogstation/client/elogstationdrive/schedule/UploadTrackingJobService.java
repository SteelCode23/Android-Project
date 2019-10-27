package com.elogstation.client.elogstationdrive.schedule;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.elogstation.client.elogstationdrive.Constants;
import com.elogstation.client.elogstationdrive.db.DatabaseHelper;
import com.elogstation.client.elogstationdrive.db.DeviceModel;
import com.elogstation.client.elogstationdrive.httpclient.CustomCallback;
import com.elogstation.client.elogstationdrive.httpclient.HttpPostAsyncTask;
import com.elogstation.client.elogstationdrive.httpclient.RequestType;

import org.json.JSONArray;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class UploadTrackingJobService extends Service implements CustomCallback {

    DatabaseHelper db;

    @Override
    public void onCreate() {
        final Context that = this;
        final UploadTrackingJobService thatService = this;
        super.onCreate();

        // Timer task makes your service will repeat after every 20 Sec.
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                if (db == null) {
                    db = new DatabaseHelper(that);
                }
                List<DeviceModel> deviceModels = db.selectDeviceTrackingNotUploaded();
//                JSONArray mJSONArray = new JSONArray(Arrays.asList(deviceModels.toString()));
//
//                Map<String, String> postData = new HashMap<>();
//                postData.put("deviceModels", mJSONArray.toString());

                HttpPostAsyncTask task = new HttpPostAsyncTask(deviceModels, RequestType.POSTTRACKING, thatService);
                task.execute(Constants.host + Constants.posttracking);

            }
        };
        timer = new Timer(true);
        timer.scheduleAtFixedRate(doAsynchronousTask, 0, Constants.uploadTimeInterval);
    }

    public static Timer timer;

    @Override
    public void completionHandler(RequestType type, Boolean isSuccess, Object ids, Object info2) {
        if(type == RequestType.POSTTRACKING){
            if(isSuccess == true){

                String[] idsNew =(String[]) ids;

                int result = db.updateDeviceTrackingUploaded(idsNew);

                Log.d("result", "result");
//                setListViewValueOk(1);
//                update database;
            }
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO do something useful

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }
}
