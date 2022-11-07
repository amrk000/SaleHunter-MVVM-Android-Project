package amrk000.salehunter.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;

import amrk000.salehunter.R;
import amrk000.salehunter.databinding.ActivityAccountSignBinding;
import amrk000.salehunter.util.DialogsProvider;
import amrk000.salehunter.util.NetworkBroadcastReceiver;
import amrk000.salehunter.util.UserAccountManager;
import amrk000.salehunter.viewmodel.activity.AccountSignViewModel;

public class AccountSign extends AppCompatActivity {
    private ActivityAccountSignBinding vb;
    private NavController navController;
    private AccountSignViewModel viewModel;

    NetworkBroadcastReceiver networkBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.SaleHunter);
        vb = ActivityAccountSignBinding.inflate(getLayoutInflater());
        View view = vb.getRoot();
        setContentView(view);

        overridePendingTransition(R.anim.lay_on,R.anim.null_anim);

        navController = Navigation.findNavController(this, R.id.account_sign_fragmentsContainer);
        viewModel = new ViewModelProvider(this).get(AccountSignViewModel.class);
        vb.accountSignBack.setOnClickListener(button -> {
            onBackPressed();
        });

        networkBroadcastReceiver = new NetworkBroadcastReceiver(this);
        registerReceiver(networkBroadcastReceiver , new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        boolean forcedSignOut = getIntent().getBooleanExtra(UserAccountManager.FORCED_SIGN_OUT,false);
        if(forcedSignOut) DialogsProvider.get(this).messageDialog(getString(R.string.Session_Expired),getString(R.string.You_are_signed_out_for_account_security));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkBroadcastReceiver);
    }

    public void setTitle(String title) {
        vb.accountSignTitle.setText(title);
    }

    public boolean isBackButtonVisible(){return vb.accountSignBack.getVisibility() == View.VISIBLE;}

    public void setBackButton(boolean backButton){

        if(backButton){
            vb.accountSignBack.setVisibility(View.VISIBLE);
            vb.accountSignBack.startAnimation(AnimationUtils.loadAnimation(this, R.anim.back_button_in));
        }
        else {
            vb.accountSignBack.setVisibility(View.GONE);
            vb.accountSignBack.startAnimation(AnimationUtils.loadAnimation(this, R.anim.back_button_out));
        }

    }

}