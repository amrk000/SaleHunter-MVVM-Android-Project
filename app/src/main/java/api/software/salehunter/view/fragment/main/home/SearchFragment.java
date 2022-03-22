package api.software.salehunter.view.fragment.main.home;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Handler;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import api.software.salehunter.adapter.HomeProductsAdapter;
import api.software.salehunter.databinding.FragmentSearchBinding;
import api.software.salehunter.model.ProductModel;
import api.software.salehunter.view.activity.Scanner;

public class SearchFragment extends Fragment {
    private FragmentSearchBinding vb;
    ArrayList<ProductModel> products = new ArrayList<>();

    ActivityResultLauncher<Intent> voiceResultLauncher;
    ActivityResultLauncher<Intent> barcodeResultLauncher;
    ActivityResultLauncher<String> cameraPermission;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        voiceResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // Here, no request code
                            Intent data = result.getData();
                            vb.searchSearchbar.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
                            searchRequest();
                        }
                    }
                });

        barcodeResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // Here, no request code
                            Intent data = result.getData();
                            vb.searchSearchbar.setText(data.getStringExtra("result"));
                            searchRequest();
                        }
                    }
                });

        cameraPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result) barcodeResultLauncher.launch(new Intent(getContext(),Scanner.class));
                else Toast.makeText(getContext(), "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vb = FragmentSearchBinding.inflate(inflater,container,false);
        return vb.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        vb = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vb.searchSearchbar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_SEARCH) searchRequest();
                return false;
            }
        });

        vb.searchVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Product Name ...");
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.forLanguageTag("EN").toLanguageTag());

                voiceResultLauncher.launch(intent);

            }
        });

        vb.searchBarcodeScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int checker = ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.CAMERA);

                if(checker == PackageManager.PERMISSION_GRANTED) barcodeResultLauncher.launch(new Intent(getContext(),Scanner.class));
                else cameraPermission.launch(Manifest.permission.CAMERA);

            }
        });

        HomeProductsAdapter recyclerAdapter = new HomeProductsAdapter(getContext(),products);
        vb.searchProductsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        vb.searchProductsRecyclerView.setAdapter(recyclerAdapter);

        new Handler().postDelayed(()->{
            for(int i=0; i<8; i++)
                products.add(new ProductModel().setName("pc purple led small speakers - usb cable").setImageUrl("").setBrand("Havit").setPrice("179.99LE").setStore("amazon").setRate(4.2f).setFavourite(false));
                recyclerAdapter.notifyItemRangeInserted(0,8);
        },500);
    }

    void searchRequest(){
        if(vb.searchSearchbar.getText().toString().isEmpty()) return;
        Toast.makeText(getContext(), "Search Request:\n"+vb.searchSearchbar.getText().toString(), Toast.LENGTH_SHORT).show();
    }

}