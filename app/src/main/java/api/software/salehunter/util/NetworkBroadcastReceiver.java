package api.software.salehunter.util;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import api.software.salehunter.view.fragment.dialogs.DisconnectedDialog;

public class NetworkBroadcastReceiver extends BroadcastReceiver {
    AppCompatActivity activity;

    public NetworkBroadcastReceiver(){};
    public NetworkBroadcastReceiver(AppCompatActivity activity){
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean connected = connectivityManager.getActiveNetworkInfo()!=null && connectivityManager.getActiveNetworkInfo().isConnected();

        DialogsProvider.get(activity).setDisconnected(!connected);
    }

}