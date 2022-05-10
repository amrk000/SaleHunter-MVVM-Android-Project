package api.software.salehunter.viewmodel.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.Size;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;

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

import api.software.salehunter.R;
import api.software.salehunter.data.Repository;
import api.software.salehunter.data.remote.RetrofitInterface;
import api.software.salehunter.model.BarcodeMonsterResponseModel;
import api.software.salehunter.model.BaseResponseModel;
import api.software.salehunter.model.UpcItemDbResponseModel;
import api.software.salehunter.view.activity.MainActivity;
import io.reactivex.BackpressureStrategy;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;

public class ScannerViewModel extends AndroidViewModel {
    private Repository repository;
    private MutableLiveData<String> result;
    private LiveData<Response<UpcItemDbResponseModel>> barcodeLookupUpcItemDb;
    private LiveData<Response<BarcodeMonsterResponseModel>> barcodeLookupBarcodeMonster;

    private Preview preview;
    private ProcessCameraProvider cameraProvider;
    private CameraSelector cameraSelector;
    private ImageAnalysis imageAnalysis;
    private BarcodeScanner barcodeScanner;

    private Vibrator vibrator;
    private MediaPlayer sfx;

    public ScannerViewModel(Application application){
        super(application);
        repository = new Repository();
        result = new MutableLiveData<>();

        vibrator = (Vibrator) application.getSystemService(Context.VIBRATOR_SERVICE);
        sfx = MediaPlayer.create(application, R.raw.scanner_sound);
        sfx.setVolume(0.25f,0.25f);

        try {
            cameraProvider = ProcessCameraProvider.getInstance(application).get();
        }
        catch (Exception e){e.printStackTrace();}

        barcodeScanner = BarcodeScanning.getClient(new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_EAN_8,Barcode.FORMAT_EAN_13,Barcode.FORMAT_UPC_A,Barcode.FORMAT_UPC_E)
                .build()
        );

        imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(1920, 1080))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                .build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(application),new ImageAnalysis.Analyzer(){
            @androidx.camera.core.ExperimentalGetImage

            @Override
            public void analyze(@NonNull ImageProxy image) {
                Image mediaImage =  image.getImage();

                if (mediaImage != null) {
                    InputImage inputImage = InputImage.fromMediaImage(mediaImage, image.getImageInfo().getRotationDegrees());

                    barcodeScanner.process(inputImage)
                            .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                                @Override
                                public void onSuccess(List<Barcode> barcodes) {
                                    for (Barcode barcode : barcodes) {
                                        String rawValue = barcode.getRawValue();
                                        if(rawValue!=null) {
                                            vibrator.vibrate(100);
                                            sfx.start();
                                            result.setValue(rawValue);
                                            barcodeScanner.close();
                                        }
                                    }
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
                                    image.close();
                                }
                            });

                }

            }

        });

    }

    public MutableLiveData<String> getResult(){return result;};

    public void bindCameraProvider(LifecycleOwner lifecycleOwner, Preview.SurfaceProvider surfaceProvider){
        preview = new Preview.Builder().build();
        preview.setSurfaceProvider(surfaceProvider);

        cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, imageAnalysis, preview);
    }

    public LiveData<Response<UpcItemDbResponseModel>> barcodeLookupUpcItemDb(String barcode){
        barcodeLookupUpcItemDb = repository.barcodeLookupUpcItemDb(barcode);
        return barcodeLookupUpcItemDb;
    }

    public LiveData<Response<BarcodeMonsterResponseModel>> barcodeLookupBarcodeMonster(String barcode){
        barcodeLookupBarcodeMonster = repository.barcodeLookupBarcodeMonster(barcode);
        return barcodeLookupBarcodeMonster;
    }

    public void removeObserverUpcItemDb(LifecycleOwner lifecycleOwner){
        barcodeLookupUpcItemDb.removeObservers(lifecycleOwner);
    }

    public void removeObserverBarcodeMonster(LifecycleOwner lifecycleOwner){
        barcodeLookupBarcodeMonster.removeObservers(lifecycleOwner);
    }

}
