package api.software.salehunter.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.constraintlayout.motion.widget.MotionScene;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Size;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.material.shape.RoundedCornerTreatment;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.android.material.transition.platform.MaterialContainerTransform;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;

import api.software.salehunter.R;
import api.software.salehunter.data.local.myDataBase;
import api.software.salehunter.databinding.ActivityScannerBinding;
import api.software.salehunter.viewmodel.activity.ScannerViewModel;

public class Scanner extends AppCompatActivity {
    private ActivityScannerBinding vb;
    private ScannerViewModel viewModel;

    public static final int RESULT_TRY_AGAIN = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        vb = ActivityScannerBinding.inflate(getLayoutInflater());
        View view = vb.getRoot();
        setContentView(view);
        setTheme(R.style.SaleHunter);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        vb.lazer.startAnimation(AnimationUtils.loadAnimation(this,R.anim.scanner_lazer_effect));

        viewModel = new ViewModelProvider(this).get(ScannerViewModel.class);

        viewModel.bindCameraProvider(this, vb.cameraPreview.getSurfaceProvider());

        viewModel.getResult().observe(this, result -> {
            showDetectingProductLayout(result);
            lookUpBarcode(result);
        });

        vb.scannerDetectingTryAgain.setOnClickListener(button ->{
            Intent returnIntent = new Intent();
            setResult(Scanner.RESULT_TRY_AGAIN, returnIntent);
            onBackPressed();
        });

    }

    void submitData(String productName){
        String[] arrayOfWords = productName.split(" ");

        StringBuilder shortenedName = new StringBuilder();
        for(int i=0; i< arrayOfWords.length; i++){
            shortenedName.append(arrayOfWords[i]);
            if(i<5) shortenedName.append(" ");
            else break;
        }

        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", shortenedName.toString());
        setResult(Scanner.RESULT_OK, returnIntent);
        onBackPressed();
    }

    void showDetectingProductLayout(String barcode){
        vb.scannerScanningLayout.setVisibility(View.INVISIBLE);
        vb.scannerDetectingLayout.setVisibility(View.VISIBLE);
        vb.scannerDetectingBarcode.setText(barcode);
        vb.scannerDetectingTryAgain.setVisibility(View.GONE);
    }

    void productNotDetected(String message){
        vb.scannerDetectingBarcode.clearAnimation();
        vb.scannerDetectingImage.clearAnimation();
        vb.scannerDetectingLoading.setVisibility(View.GONE);
        vb.scannerDetectingText.setText(message);
        vb.scannerDetectingText.setTextColor(getResources().getColor(R.color.accent));
        vb.scannerDetectingTryAgain.setVisibility(View.VISIBLE);
        vb.scannerDetectingTryAgain.startAnimation(AnimationUtils.loadAnimation(this,R.anim.lay_on));
    }

    void lookUpBarcode(String barcode){
       String product =  myDataBase.get(this).daoAccess().lookUpProduct(barcode);
       if(product!=null) submitData(product);
       else lookUpBarcodeAlt(barcode);
    }

    void lookUpBarcodeAlt(String barcode){
        viewModel.barcodeLookupUpcItemDb(barcode).observe(this, response ->{
            if(response.code() == 200) {
                Toast.makeText(this, "1st Lookup", Toast.LENGTH_SHORT).show();
                viewModel.removeObserverUpcItemDb(this);

                if(response.body().getItems().size()>0) {
                    String productName = response.body().getItems().get(0).getProductName();
                    submitData(productName);
                } else lookUpBarcodeAlt2(barcode);

            } else lookUpBarcodeAlt2(barcode);

        });
    }

    void lookUpBarcodeAlt2(String barcode){
        viewModel.barcodeLookupBarcodeMonster(barcode).observe(this, responseAlt ->{
            if(responseAlt.code() == 200) {
                Toast.makeText(this, "2nd Lookup", Toast.LENGTH_SHORT).show();
                viewModel.removeObserverBarcodeMonster(this);

                if(responseAlt.body().getProductName() != null) {
                    String productName = responseAlt.body().getProductName();
                    submitData(productName);
                } else productNotDetected("Product Not Detected");

            } else productNotDetected("You're Disconnected");
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}