package api.software.salehunter.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import androidx.fragment.app.FragmentManager;

import api.software.salehunter.DisconnectedDialog;
import api.software.salehunter.accountSign.SignInFragment;

public class NetworkBroadcastReceiver extends BroadcastReceiver {

    DisconnectedDialog disconnectedDialog;
    FragmentManager supportFragmentManager;

    public NetworkBroadcastReceiver(){}

    public NetworkBroadcastReceiver(FragmentManager supportFragmentManager){
        this.supportFragmentManager = supportFragmentManager;
        disconnectedDialog = new DisconnectedDialog();
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean connected = connectivityManager.getActiveNetworkInfo()!=null && connectivityManager.getActiveNetworkInfo().isConnected();

        if(connected){
            if(disconnectedDialog.isVisible()) disconnectedDialog.dismiss();
        }
        else disconnectedDialog.show(supportFragmentManager,disconnectedDialog.getTag());

    }
}