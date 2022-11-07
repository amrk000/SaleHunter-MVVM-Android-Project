package amrk000.salehunter.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import androidx.appcompat.app.AppCompatActivity;

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