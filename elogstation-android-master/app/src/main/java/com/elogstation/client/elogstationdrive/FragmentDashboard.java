package com.elogstation.client.elogstationdrive;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.elogstation.client.elogstationdrive.db.DatabaseHelper;
import com.elogstation.client.elogstationdrive.db.DefaultEldModel;
import com.elogstation.client.elogstationdrive.db.EldModel;
import com.elogstation.client.elogstationdrive.httpclient.CustomCallback;
import com.elogstation.client.elogstationdrive.httpclient.HttpPostAsyncTask;
import com.elogstation.client.elogstationdrive.httpclient.RequestType;
import com.elogstation.client.elogstationdrive.util.ListViewHelper;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.iosix.eldblelib.EldBleConnectionStateChangeCallback;
import com.iosix.eldblelib.EldBleDataCallback;
import com.iosix.eldblelib.EldBleError;
import com.iosix.eldblelib.EldBleScanCallback;
import com.iosix.eldblelib.EldBroadcast;
import com.iosix.eldblelib.EldBroadcastTypes;
import com.iosix.eldblelib.EldCachedDataRecord;
import com.iosix.eldblelib.EldCachedPeriodicRecord;
import com.iosix.eldblelib.EldDataRecord;
import com.iosix.eldblelib.EldManager;
import com.iosix.eldblelib.EldScanObject;


public class FragmentDashboard extends Fragment implements CustomCallback {
    static FragmentDashboard fragmentDashboard;
    public static FragmentDashboard newInstance() {
        if(fragmentDashboard == null){
            FragmentDashboard fragment = new FragmentDashboard();
            fragmentDashboard = fragment;
        }
        return fragmentDashboard;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    ListView androidListView;
    public static EldManager mEldManager;
    private ListViewHelper[] listViewMain;
    private View view;
    private Context context;

    @Override
    public void setUserVisibleHint(boolean visible){
        super.setUserVisibleHint(visible);
        if (visible && isResumed()){
            showUpdatedValues();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);


        ListViewHelper[] listViewHelpers = {
                new ListViewHelper(
                        getString(R.string.internet),
                        getString(R.string.internetDesc),
                        R.drawable.closecircle
                ),
                new ListViewHelper(
                        getString(R.string.api),
                        getString(R.string.apiDesc),
                        R.drawable.closecircle
                ),
                new ListViewHelper(
                        getString(R.string.eldSelected),
                        getString(R.string.eldSelectedDesc),
                        R.drawable.closecircle
                ),
                new ListViewHelper(
                        getString(R.string.bluetooth),
                        getString(R.string.bluetoothDesc),
                        R.drawable.closecircle
                ),
                new ListViewHelper(
                        getString(R.string.deviceConnected),
                        getString(R.string.deviceConnectedDesc),
                        R.drawable.closecircle
                ),
                new ListViewHelper(
                        getString(R.string.trackingProgress),
                        getString(R.string.trackingProgressDesc),
                        R.drawable.closecircle
                )
        };


        this.listViewMain = listViewHelpers;

        this.view = view;
        this.context = context;
        displayAndSetValues(listViewHelpers);


        return view;
    }
    private void displayAndSetValues(ListViewHelper[] listViewHelpers) {

        List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < listViewHelpers.length; i++) {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("listview_title", listViewHelpers[i].getTitle());
            hm.put("listview_discription", listViewHelpers[i].getDesc());
            hm.put("listview_image", Integer.toString(listViewHelpers[i].getImage()));
            aList.add(hm);
        }

        String[] from = {"listview_image", "listview_title", "listview_discription"};
        int[] to = {R.id.listview_image, R.id.listview_item_title, R.id.listview_item_short_description};

        final Context that = getContext();
        SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity().getBaseContext(), aList, R.layout.listview_activity, from, to);
        androidListView = (ListView) view.findViewById(R.id.list_view);
        androidListView.setAdapter(simpleAdapter);

        androidListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                switch (position) {
                    case 2:
                        final List<String> listOfElds = db.getAllEldsString();
                        final CharSequence[] items = listOfElds.toArray(new CharSequence[listOfElds.size()]);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(R.string.selectEld)
                                .setItems(items, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // The 'which' argument contains the index position
                                        // of the selected item
                                        db.insertDefaultEldAPI(listOfElds.get(which));
                                        checkEldSelected();

                                        if(FragmentDashboard.eldConnectedBluetooth == true) {
                                            FragmentDashboard.mEldManager.DisconnectEld();
                                            FragmentDashboard.eldConnectedBluetooth = false;
                                            checkBluetoothDeviceSelected();
                                            FragmentDashboard.startedSearchingBluetoothDevices = false;
                                        }
                                        showUpdatedValues();


                                    }
                                });
                        builder.create();
                        builder.show();
                        break;
//                    case 3:
//                        startActivity(new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS));
//                        break;
                    case 4:
                        if(startedSearchingBluetoothDevices == false) {
                            dialog = ProgressDialog.show(getContext(), "",
                                    getString(R.string.tryingToConnectToELD), false);
                            startedSearchingBluetoothDevices = true;
                            if (hasBlePermissions() && areLocationServicesEnabled(that)) {
                                if (mEldManager == null) {
                                    mEldManager = EldManager.GetEldManager(that, "123456789A");
                                }
                                if (mEldManager.ScanForElds(bleScanCallbackList) == EldBleError.BLUETOOTH_NOT_ENABLED)
                                    mEldManager.EnableBluetooth(REQUEST_BT_ENABLE);
                            }
                        }
                        break;
                }

            }

        });
    }

    public boolean hasBlePermissions() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean areLocationServicesEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        try {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static final int REQUEST_BASE = 100;
    private static final int REQUEST_BT_ENABLE = REQUEST_BASE + 1;


    private EldBleScanCallback bleScanCallbackList = new EldBleScanCallback() {

        @Override
        public void onScanResult(ArrayList deviceList) {


            if (deviceList != null) {

                final List<String> devices = new ArrayList<>();
                for (Object device : deviceList) {
                    EldScanObject so = (EldScanObject) deviceList.get(0);
                    devices.add(so.getDeviceId());
                    if (eldSelectedFromAPI.equals(so.getDeviceId())) {
                        mEldManager.ConnectToELd(bleDataCallback, EnumSet.of(EldBroadcastTypes.ELD_BUFFER_RECORD, EldBroadcastTypes.ELD_CACHED_RECORD, EldBroadcastTypes.ELD_DATA_RECORD),
                                bleConnectionStateChangeCallback, eldSelectedFromAPI);

                        return;

                    }
                }

            } else

            {
                Log.d("ELD", "No Eld found.");
            }


        }
    };


    private EldBleConnectionStateChangeCallback bleConnectionStateChangeCallback = new EldBleConnectionStateChangeCallback() {
        @Override
        public void onConnectionStateChange(final int newState) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
            Log.d("ELD", "New State of connection" + Integer.toString(newState, 10) + "\r\n");
//                }
//            });
        }
    };

    static boolean enableSQLInserting = false;

    private EldBleDataCallback bleDataCallback = new EldBleDataCallback() {
        @Override
        public void OnDataRecord(final EldBroadcast dataRec, final EldBroadcastTypes RecordType) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {

//                    mDataView.append(dataRec.getBroadcastString());

            if (RecordType == EldBroadcastTypes.ELD_DATA_RECORD) {
                ((EldDataRecord) (dataRec)).getRpm();

                if (eldConnectedBluetooth == false) {
                    eldConnectedBluetooth = true;
                    if(dialog != null) {
                        dialog.cancel();
                    }
                    eldSelectedFromAPIBluetooth = eldSelectedFromAPI;
                    checkBluetoothDeviceSelected();
                }
                if(FragmentDashboard.enableSQLInserting) {
                    db.insertDeviceTracking(((EldDataRecord) (dataRec)), eldSelectedFromAPIBluetooth);
                    Log.d("value", "RPM value: " + Double.toString(((EldDataRecord) (dataRec)).getRpm()));
//                } else {
//                    mEldManager.DisconnectEld();
                }

            } else if (RecordType == EldBroadcastTypes.ELD_CACHED_RECORD) {

                //Shows how to get to the specific record types created based on the broadcast info
                EldCachedDataRecord rec = (EldCachedDataRecord) dataRec;
                if (rec instanceof EldCachedPeriodicRecord) {
                    rec.getEngineHours();
                }

            }
//                    mScrollView.fullScroll(View.FOCUS_DOWN);
//                }
//            });
        }
    };


    @Override
    public void onViewCreated(View view,
                              Bundle savedInstanceState) {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {

                        checkConditions();
                    }
                },
                200);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

        }
    }


    int requestCode = 9012;
    void checkConditions(){
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, requestCode);
        checkInternetConnected();
        checkAPIConnected();
        checkEldSelected();
        checkBluetoothTurnedOn();
        checkBluetoothDeviceSelected();
        checkTrackingProgess();
    }


    void checkTrackingProgess(){
        int total = db.countDeviceTrackingTotal();
        if(total < 1){
            return;
        }
        int uploaded = db.countDeviceTrackingUploaded();

        if((total - uploaded) < Constants.uploadThreshold){
            setListViewValueOk(5);
        }else{
            setListViewValueNotOk(5);
        }
        setListViewDesc(5, (total - uploaded) + " pending");

    }


    static boolean eldConnectedBluetooth = false;
    static String eldSelectedFromAPI="";
    String eldSelectedFromAPIBluetooth="";
    static boolean startedSearchingBluetoothDevices = false;
    ProgressDialog dialog;
    void checkBluetoothDeviceSelected(){
        if(eldSelectedFromAPI.isEmpty()){
            return;
        }
        if(eldConnectedBluetooth == true){
            setListViewValueOk(4);
            setListViewDesc(4, eldSelectedFromAPIBluetooth);
        } else {
            setListViewValueNotOk(4);
            setListViewDesc(4, "");

        }

        if(startedSearchingBluetoothDevices == false) {
            startedSearchingBluetoothDevices = true;
            if (hasBlePermissions() && areLocationServicesEnabled(getContext())) {
                if (mEldManager == null) {
                    mEldManager = EldManager.GetEldManager(getContext(), "123456789A");
                    if (mEldManager.ScanForElds(bleScanCallbackList) == EldBleError.BLUETOOTH_NOT_ENABLED)
                        mEldManager.EnableBluetooth(REQUEST_BT_ENABLE);
                }
            }
        }
    }

    void checkBluetoothTurnedOn(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
            }
            setListViewValueOk(3);
        }
    }

    DatabaseHelper db;

    void checkEldSelected(){
        if (db == null) {
            db = new DatabaseHelper(getContext());
        }
        DefaultEldModel defaultEldModel = db.getDefaultEldAPI();
        if(defaultEldModel == null){

        } else {
            setListViewValueOk(2);
            setListViewDesc(2, defaultEldModel.getEldId());
            eldSelectedFromAPI = defaultEldModel.getEldId();
        }
    }

    void checkAPIConnected(){
        Map<String, String> postData = new HashMap<>();
        HttpPostAsyncTask task = new HttpPostAsyncTask(postData, RequestType.HEALTH, this);
        task.execute(Constants.host + Constants.health);
    }

    @Override
    public void completionHandler(RequestType type, Boolean isSuccess, Object info, Object info2) {
        if(type == RequestType.HEALTH){
            if(isSuccess == true){
                setListViewValueOk(1);
            } else {
                setListViewValueNotOk(1);
            }
        }
    }

    void checkInternetConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getActiveNetworkInfo() != null){
            setListViewValueOk(0);
        } else {
            setListViewValueNotOk(0);
        }
    }

    void showUpdatedValues(){
        checkInternetConnected();
        checkAPIConnected();
        checkTrackingProgess();
        displayAndSetValues(listViewMain);

//        View v =androidListView .getChildAt(index - androidListView.getFirstVisiblePosition() );
//
//        if(v == null)
//            return;
//
//        ImageView image = (ImageView) v.findViewById(R.id.listview_image);
//        image.setImageDrawable(getResources().getDrawable(R.drawable.checkcircle));

//        View v =androidListView .getChildAt(index - androidListView.getFirstVisiblePosition() );
//
//        if(v == null)
//            return;
//
//        TextView textView = (TextView) v.findViewById(R.id.listview_item_short_description);
//        textView.setText(desc);

    }

    void setListViewValueOk(int index){
        listViewMain[index].setImage(R.drawable.checkcircle);
    }

    void setListViewValueNotOk(int index){
        listViewMain[index].setImage(R.drawable.closecircle);
    }

    void setListViewDesc(int index, String desc) {
        listViewMain[index].setDesc(desc);
    }

}