package api.software.salehunter.mainFragments.home;

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
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import api.software.salehunter.adapter.HomeProductsAdapter;
import api.software.salehunter.model.ProductModel;
import api.software.salehunter.R;
import api.software.salehunter.Scanner;

public class SearchFragment extends Fragment {
    EditText searchBox;
    ImageButton voice,barcode;

    ActivityResultLauncher<Intent> voiceResultLauncher;
    ActivityResultLauncher<Intent> barcodeResultLauncher;
    ActivityResultLauncher<String> cameraPermission;

    RecyclerView recyclerView;
    ArrayList<ProductModel> products = new ArrayList<>();

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
                            searchBox.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
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
                            searchBox.setText(data.getStringExtra("result"));
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchBox = view.findViewById(R.id.search_searchbar);
        voice = view.findViewById(R.id.search_voice);
        barcode = view.findViewById(R.id.search_barcodeScan);
        recyclerView = view.findViewById(R.id.search_products_recyclerView);

        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_SEARCH) searchRequest();
                return false;
            }
        });

        voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Product Name ...");
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.forLanguageTag("EN").toLanguageTag());

                voiceResultLauncher.launch(intent);

            }
        });

        barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int checker = ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.CAMERA);

                if(checker == PackageManager.PERMISSION_GRANTED) barcodeResultLauncher.launch(new Intent(getContext(),Scanner.class));
                else cameraPermission.launch(Manifest.permission.CAMERA);

            }
        });

        HomeProductsAdapter recyclerAdapter = new HomeProductsAdapter(getContext(),products);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerView.setAdapter(recyclerAdapter);

        new Handler().postDelayed(()->{
            for(int i=0; i<8; i++)
                products.add(new ProductModel().setName("pc purple led small speakers - usb cable").setImageUrl("").setBrand("Havit").setPrice("179.99LE").setStore("amazon").setRate(4.2f).setFavourite(false));
                recyclerAdapter.notifyItemRangeInserted(0,8);
        },500);
    }

    void searchRequest(){
        if(searchBox.getText().toString().isEmpty()) return;
        Toast.makeText(getContext(), "Search Request:\n"+searchBox.getText().toString(), Toast.LENGTH_SHORT).show();
    }

}