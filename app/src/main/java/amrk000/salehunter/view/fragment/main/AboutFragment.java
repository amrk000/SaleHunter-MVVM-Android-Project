package amrk000.salehunter.view.fragment.main;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import amrk000.salehunter.R;
import amrk000.salehunter.databinding.FragmentAboutBinding;
import amrk000.salehunter.util.AppSettingsManager;
import amrk000.salehunter.view.activity.MainActivity;

public class AboutFragment extends Fragment {
    private FragmentAboutBinding vb;

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vb = FragmentAboutBinding.inflate(inflater,container,false);
        return vb.getRoot();
    }

    @Override
    public void onDestroy() {
        vb.aboutWebView.setWebViewClient(null);
        vb = null;
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setTitle(getString(R.string.About));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vb.aboutWebView.getSettings().setAllowContentAccess(true);
        vb.aboutWebView.getSettings().setJavaScriptEnabled(true);
        vb.aboutWebView.getSettings().setDomStorageEnabled(true);
        vb.aboutWebView.getSettings().setBuiltInZoomControls(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            switch (AppSettingsManager.getTheme(getContext())) {
                case AppSettingsManager.THEME_LIGHT:
                    vb.aboutWebView.getSettings().setForceDark(WebSettings.FORCE_DARK_OFF);
                    break;
                case AppSettingsManager.THEME_DARK:
                    vb.aboutWebView.getSettings().setForceDark(WebSettings.FORCE_DARK_ON);
                    break;
                default:
                    if (isDarkModeEnabled())
                        vb.aboutWebView.getSettings().setForceDark(WebSettings.FORCE_DARK_ON);
            }
        }

        vb.aboutWebView.loadUrl("https://sale-hunter.vercel.app/about-us");

        vb.aboutWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                vb.aboutLoading.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                vb.aboutWebView.reload();
            }
        });

        vb.aboutRefresh.setOnClickListener(button ->{
            vb.aboutWebView.reload();
            vb.aboutLoading.setVisibility(View.VISIBLE);
        });

        vb.moreapps.setOnClickListener((View v)->{
            Intent profilelink = new Intent(Intent.ACTION_VIEW);
            profilelink.setData(Uri.parse("https://play.google.com/store/apps/dev?id=5289896800613171020"));
            startActivity(profilelink);
        });

        vb.githubrepo.setOnClickListener((View v)->{
            Intent repolink = new Intent(Intent.ACTION_VIEW);
            repolink.setData(Uri.parse("https://github.com/amrk000/SaleHunter-MVVM-Android-Project"));
            startActivity(repolink);
        });

    }

    public boolean isDarkModeEnabled() {
        int currentMode = getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentMode == Configuration.UI_MODE_NIGHT_YES;
    }
}