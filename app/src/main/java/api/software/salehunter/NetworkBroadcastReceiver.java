package api.software.salehunter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

public class NetworkBroadcastReceiver extends BroadcastReceiver {

    FragmentManager supportFragmentManager;
    FrameLayout frameLayout;

    public NetworkBroadcastReceiver(){}

    public NetworkBroadcastReceiver(FragmentManager supportFragmentManager, FrameLayout frameLayout){
        this.supportFragmentManager = supportFragmentManager;
        this.frameLayout = frameLayout;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager.getActiveNetworkInfo()!=null && connectivityManager.getActiveNetworkInfo().isConnected()){
            supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(frameLayout.getId(),new SignInFragment())
                    .commit();

        }
        else {
            //remove all backstack
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            supportFragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(frameLayout.getId(),new DisconnectedFragment())
                    .commit();

        }
    }
}