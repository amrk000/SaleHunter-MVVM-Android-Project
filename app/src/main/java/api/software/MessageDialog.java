package api.software;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import api.software.salehunter.R;

public class MessageDialog extends BottomSheetDialogFragment {
    TextView title, subTitle;
    Button close;

    String titleText, subTitleText;

    public MessageDialog() {}

    public MessageDialog(String titleText, String subTitleText) {
        this.titleText = titleText;
        this.subTitleText = subTitleText;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title = view.findViewById(R.id.messageDialogTitle);
        subTitle = view.findViewById(R.id.messageDialogSubtitle);
        close = view.findViewById(R.id.messageDialogClose);

        title.setText(titleText);

        subTitle.setText(subTitleText);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }

    public void setMessage(String titleText, String subTitleText) {
        this.titleText = titleText;
        this.subTitleText = subTitleText;
    }

}