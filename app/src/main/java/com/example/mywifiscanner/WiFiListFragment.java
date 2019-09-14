package com.example.mywifiscanner;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class WiFiListFragment extends Fragment {
    private WifiManager wifiManager;
    private List<ScanResult> nets;
    private ArrayAdapter<String> adapter;
    private ProgressBar progressBar;
    private Handler handler = new Handler();
    private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(context,"onReceive",Toast.LENGTH_SHORT).show();
            if(progressBar!=null){
                progressBar.setVisibility(View.GONE);
            }
            nets = wifiManager.getScanResults();
           // unregisterReceiver(this);
            //Toast.makeText(context,"onReceive "+nets.size(),Toast.LENGTH_SHORT).show();
            adapter.clear();
            for (ScanResult scanResult : nets) {
                adapter.add(scanResult.SSID+" - "+scanResult.capabilities+" "+scanResult.frequency);
            }
            adapter.notifyDataSetChanged();
        };
    };

    public WiFiListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        wifiManager = (WifiManager)getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
        context.registerReceiver(wifiReceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_blank, container, false);
        adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1);
        ListView listView = v.findViewById(R.id.listVew);
        listView.setAdapter(adapter);
        progressBar = v.findViewById(R.id.progressBar);
        findWiFis();
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(nets!=null&&nets.size()>position) {
                    ScanResult result = nets.get(position);
                    Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment2);
                    if(fragment instanceof ConnectFragment){
                        ((ConnectFragment) fragment).setNet(result);
                    }
                }
            }
        });
        return v;
    }
    private void findWiFis(){
        if(wifiManager!=null){
            wifiManager.startScan();
            if(progressBar!=null){
                progressBar.setVisibility(View.VISIBLE);
            }
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    findWiFis();
                }
            },5000);
        }

    }

}
