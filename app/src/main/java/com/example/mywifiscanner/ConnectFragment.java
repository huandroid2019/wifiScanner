package com.example.mywifiscanner;


import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectFragment extends Fragment {
    TextView tv_ssid, tv_bssid, tv_name;
    ScanResult scanResult;
    public ConnectFragment() {
        //
        setRetainInstance(true);
    }

    public void setNet(ScanResult scanResult){
        this.scanResult = scanResult;
        tv_ssid.setText(scanResult.SSID);
        tv_bssid.setText(scanResult.BSSID);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_connect, container, false);
        tv_bssid = v.findViewById(R.id.tv_bssid);
        tv_ssid = v.findViewById(R.id.tv_ssid);


        final EditText editText = v.findViewById(R.id.ed_psq);
        v.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scanResult!=null){
                    if(editText!=null && editText.getText().toString().matches("[0-9a-zA-Z]+")) {
                        connect(scanResult, editText.getText().toString());
                    } else {
                        Toast.makeText(getContext(),"wrong password",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return v;
    }
    private void connect(ScanResult scanResult, String password){
        WifiManager manager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = "\""+scanResult.SSID+"\"";
        wifiConfiguration.preSharedKey =  "\""+password+"\"";
        wifiConfiguration.status = WifiConfiguration.Status.ENABLED;
        wifiConfiguration.priority = 40;

        if(scanResult.capabilities.contains("WPA")){
            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        }
        int netId = -1;
        if (manager != null) {
            netId = manager.addNetwork(wifiConfiguration);
            Toast.makeText(getContext(),"net id:"+netId,Toast.LENGTH_LONG).show();
            if(netId == -1) {
                List<WifiConfiguration> list = manager.getConfiguredNetworks();
                for (WifiConfiguration configuration : list) {
                    if (configuration.SSID != null && configuration.SSID.equals("\"" + scanResult.SSID + "\"")) {
                        /*boolean isDisconnected = manager.disconnect();
                        Log.d("c", "disconnected" + isDisconnected);
                        boolean isEnabled = manager.enableNetwork(configuration.networkId, true);
                        Log.d("c", "enabled" + isEnabled);
                        boolean isReconnected = manager.reconnect();
                        Log.d("c", "reconnected" + isReconnected);*/
                        netId = configuration.networkId;
                        break;
                    }
                }
            }
        }

        try {
            assert manager != null;
            boolean isConnected = manager.enableNetwork(netId, true);
            Toast.makeText(getContext(),"connected:"+isConnected,Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
