package amrk000.salehunter.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;

import amrk000.salehunter.R;
import amrk000.salehunter.data.local.myDataBase;
import amrk000.salehunter.databinding.ActivityScannerBinding;
import amrk000.salehunter.viewmodel.activity.ScannerViewModel;

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
                viewModel.removeObserverBarcodeMonster(this);

                if(responseAlt.body().getProductName() != null) {
                    String productName = responseAlt.body().getProductName();
                    submitData(productName);
                } else productNotDetected(getString(R.string.Product_Not_Detected));

            } else productNotDetected(getString(R.string.You_are_Disconnected));
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}