package api.software.salehunter.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;

import api.software.salehunter.R;
import api.software.salehunter.data.Repository;
import api.software.salehunter.databinding.ActivityAccountSignBinding;
import api.software.salehunter.model.EmailVerificationModel;
import api.software.salehunter.model.ResetPasswordModel;
import api.software.salehunter.util.DialogsProvider;
import api.software.salehunter.util.NetworkBroadcastReceiver;
import api.software.salehunter.util.UserAccountManager;
import api.software.salehunter.viewmodel.activity.AccountSignViewModel;

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
        if(forcedSignOut) DialogsProvider.get(this).messageDialog("Session Expired !","You are signed out for account security\nPlease sign in again.");
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