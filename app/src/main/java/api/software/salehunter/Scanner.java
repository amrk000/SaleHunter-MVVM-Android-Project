package api.software.salehunter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.ViewPort;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Rational;
import android.util.Size;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;

public class Scanner extends AppCompatActivity {
    PreviewView previewView;
    ProcessCameraProvider cameraProvider;
    ImageView lazer;
    Vibrator vibrator;
    MediaPlayer sfx;
    boolean scanned = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        setTheme(R.style.SaleHunter);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        previewView = findViewById(R.id.cameraPreview);
        lazer = findViewById(R.id.lazer);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        sfx = MediaPlayer.create(this, R.raw.scanner_sound);
        sfx.setVolume(0.25f,0.25f);

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        // Camera provider is now guaranteed to be available
        try { cameraProvider = cameraProviderFuture.get(); } catch (Exception e){e.printStackTrace();}

        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        BarcodeScannerOptions barcodeScannerOptions = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_EAN_8,Barcode.FORMAT_EAN_13,Barcode.FORMAT_UPC_A,Barcode.FORMAT_UPC_E)
                .build();

        BarcodeScanner barcodeScanner = BarcodeScanning.getClient(barcodeScannerOptions);

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(1920, 1080))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                .build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this),new ImageAnalysis.Analyzer(){
            @Override
            public void analyze(@NonNull ImageProxy image) {

                Image mediaImage = image.getImage();

                if (mediaImage != null) {
                    InputImage inputImage = InputImage.fromMediaImage(mediaImage, image.getImageInfo().getRotationDegrees());

                    //process image from buffer
                    barcodeScanner.process(inputImage)
                            .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                                @Override
                                public void onSuccess(List<Barcode> barcodes) {
                                    //on process start successfully
                                }

                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //on process start error
                                }

                            })
                            .addOnCompleteListener(new OnCompleteListener<List<Barcode>>() {
                                @Override
                                public void onComplete(@NonNull Task<List<Barcode>> task) {

                                    //list of all detected barcodes/qr codes
                                    for (Barcode barcode : task.getResult()) {

                                        String rawValue = barcode.getRawValue();

                                        Intent returnIntent = new Intent();
                                        returnIntent.putExtra("result", rawValue);
                                        setResult(Activity.RESULT_OK, returnIntent);

                                        scanned = true;
                                        sfx.start();
                                        finish();

                                    }
                                    image.close();
                                }
                            });

                }

            }

        });

        cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis,preview);

        lazer.startAnimation(AnimationUtils.loadAnimation(this,R.anim.scanner_lazer_effect));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(scanned) vibrator.vibrate(80);
    }
}