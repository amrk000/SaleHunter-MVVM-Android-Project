package api.software.salehunter.data.remote;

import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit mainClient;
    private static Retrofit upcItemDbClient;
    private static Retrofit barcodeMonsterClient;

    private static String mainClientUrl="https://sale-hunter.herokuapp.com/api/v1/";
    private static String upcItemDbClientUrl="https://api.upcitemdb.com/";
    private static String barcodeMonsterClientUrl="https://barcode.monster/";

    public static Retrofit getMainInstance(){
        if(mainClient == null){
            mainClient = new Retrofit.Builder()
                    .baseUrl(mainClientUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }

        return mainClient;
    }

    public static Retrofit getUpcItemDbInstance(){
        if(upcItemDbClient == null){
            upcItemDbClient = new Retrofit.Builder()
                    .baseUrl(upcItemDbClientUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }

        return upcItemDbClient;
    }

    public static Retrofit getBarcodeMonsterInstance(){
        if(barcodeMonsterClient == null){
            barcodeMonsterClient = new Retrofit.Builder()
                    .baseUrl(barcodeMonsterClientUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }

        return barcodeMonsterClient;
    }
}
