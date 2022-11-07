package amrk000.salehunter.view.fragment.dialogs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import amrk000.salehunter.R;
import amrk000.salehunter.databinding.FragmentDisconnectedBinding;


public class DisconnectedDialog extends DialogFragment {
    private FragmentDisconnectedBinding vb;

    public DisconnectedDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().setWindowAnimations(R.style.DialogLayOnAnimation);

        vb = FragmentDisconnectedBinding.inflate(inflater, container, false);
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

        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setCancelable(false);

        vb.disconnectedDialogClose.setOnClickListener(view1 -> dismiss());

    }
}