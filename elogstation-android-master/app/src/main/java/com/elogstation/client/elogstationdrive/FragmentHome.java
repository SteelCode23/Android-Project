package com.elogstation.client.elogstationdrive;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.elogstation.client.elogstationdrive.db.DatabaseHelper;
import com.elogstation.client.elogstationdrive.httpclient.CustomCallback;
import com.elogstation.client.elogstationdrive.httpclient.HttpPostAsyncTask;
import com.elogstation.client.elogstationdrive.httpclient.RequestType;
import com.elogstation.client.elogstationdrive.util.MainUtil;
import com.elogstation.client.elogstationdrive.util.StatusType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FragmentHome extends Fragment implements CustomCallback {
    static FragmentHome fragmentHome;
    public static FragmentHome newInstance() {
        if(fragmentHome == null){
            FragmentHome fragment = new FragmentHome();
            fragmentHome = fragment;
        }
        return fragmentHome;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        buttons[0] = (Button) view.findViewById(R.id.OFFDUTY);
        buttons[1] = (Button) view.findViewById(R.id.SLEEPING);
        buttons[2] = (Button) view.findViewById(R.id.DRIVING);
        buttons[3] = (Button) view.findViewById(R.id.NOTDRIVING);
        statusTypeTextView = (TextView) view.findViewById(R.id.statusType);
        sinceTimeTextView = (TextView) view.findViewById(R.id.sinceTime);

        Map<String, String> postData = new HashMap<>();
        HttpPostAsyncTask task = new HttpPostAsyncTask(postData, RequestType.STATUS, this);
        task.execute(Constants.host + Constants.getstatuslatest);

        task = new HttpPostAsyncTask(postData, RequestType.GETMYELDS, this);
        task.execute(Constants.host + Constants.getmyelds);

        allowClickAction();
        return view;
    }

    void allowClickAction(){
        final FragmentHome that = this;
        for(Button button: buttons){
            button.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Map<String, String> postData = new HashMap<>();
                    postData.put("deviceTime", MainUtil.convertDateToString(new Date()));

                    switch(v.getId()){
                        case R.id.OFFDUTY:
                            postData.put("statusType", "OFFDUTY");
                            break;
                        case R.id.SLEEPING:
                            postData.put("statusType", "SLEEPING");
                            break;
                        case R.id.NOTDRIVING:
                            postData.put("statusType", "NOTDRIVING");
                            break;
                        case R.id.DRIVING:
                            postData.put("statusType", "DRIVING");
                            break;

                    }
                    HttpPostAsyncTask task = new HttpPostAsyncTask(postData, RequestType.POSTSTATUS, that);
                    task.execute(Constants.host + Constants.poststatus);

                }
            });
        }
    }

    Button buttons[] = new Button[4];
    TextView statusTypeTextView, sinceTimeTextView;

    private DatabaseHelper db;

    @Override
    public void completionHandler(RequestType type, Boolean isSuccess, Object statusTypeOrResponseBody, Object date) {
        if(type == RequestType.STATUS || type == RequestType.POSTSTATUS){
            if(isSuccess == true){

                showSelectedButton((String)statusTypeOrResponseBody, (String)date);
            }

        }  else if(type == RequestType.GETMYELDS){
            try {
                JSONArray jsonArray = new JSONArray((String) statusTypeOrResponseBody);

                //save to db
                if (db == null) {
                    db = new DatabaseHelper(getContext());
                }
                db.deleteAllElds();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    db.insertEld(obj.getLong("id"),
                            obj.getString("eldId"),
                            obj.getString("orgId"));

                }
            } catch (Exception e){
                e.getMessage();
            }

        }
    }

    void allowClickable(){
        for(Button button: buttons){
            button.setClickable(true);
            button.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    void disallowClickable(Button button){
            button.setClickable(false);
            button.setTextColor(getResources().getColor(R.color.red));
    }


    private void setText(final TextView text,final String value){
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                // Any UI task, example
                text.setText(value);
            }
        };
        handler.sendEmptyMessage(1);
    }

    void showSelectedButton(String statusTypeString, String date){

        setText(statusTypeTextView, statusTypeString);
        if(date != null) {
            setText(sinceTimeTextView, "since " + MainUtil.getTimeAgo(date));
        } else {
            setText(sinceTimeTextView, "");
        }
//        statusTypeTextView.setText(statusTypeString);
//        sinceTimeTextView.setText(date);

        StatusType statusType = StatusType.valueOf(statusTypeString);
        allowClickable();
        if (statusType == StatusType.OFFDUTY) {
            disallowClickable(buttons[0]);
        } else if (statusType == StatusType.SLEEPING) {
            disallowClickable(buttons[1]);
        } else if (statusType == StatusType.DRIVING) {
            disallowClickable(buttons[2]);
        } else if (statusType == StatusType.NOTDRIVING) {
            disallowClickable(buttons[3]);
        }

    }

}