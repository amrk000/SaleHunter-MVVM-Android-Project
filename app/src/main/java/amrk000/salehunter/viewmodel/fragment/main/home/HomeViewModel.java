package amrk000.salehunter.viewmodel.fragment.main.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import amrk000.salehunter.data.Repository;

public class HomeViewModel extends AndroidViewModel {
    private Repository repository;

    public HomeViewModel(@NonNull Application application) {
        super(application);

        repository = new Repository();
    }

}
