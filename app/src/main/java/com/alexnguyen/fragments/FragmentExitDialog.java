package com.alexnguyen.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.alexnguyen.interfaces.CityClickListener;
import com.alexnguyen.radiofreeonline.R;

public class FragmentExitDialog extends AppCompatDialogFragment {
    private TextView txt_Exit_dialog, txtTitle_Exit_dialog;
    private Button btnCancel_Exit_dialog, btnexit_Exit_dialog;
    private CityClickListener listener;

    public FragmentExitDialog(CityClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).
                inflate(R.layout.layout_dialog_exit_app, null);

        SetUp(view);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setView(view);

        //dialog.setTitle("Exit App");
        dialog.setIcon(R.mipmap.app_icon);
        Dialog dialog_description = dialog.create();
        return dialog_description;
    }

//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.layout_dialog_exit_app, container);
//    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

    }

    private void SetUp(View view){
        txt_Exit_dialog = (TextView) view.findViewById(R.id.txt_Exit_dialog);
        txtTitle_Exit_dialog = (TextView) view.findViewById(R.id.txtTitle_Exit_dialog);
        btnCancel_Exit_dialog = (Button) view.findViewById(R.id.btnCancel_Exit_dialog);
        btnexit_Exit_dialog = (Button) view.findViewById(R.id.btnexit_Exit_dialog);

        txtTitle_Exit_dialog.setText("Confirm");
        txt_Exit_dialog.setText(getString(R.string.sure_quit));
        btnCancel_Exit_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });



        btnexit_Exit_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick();
            }
        });
    }
}
