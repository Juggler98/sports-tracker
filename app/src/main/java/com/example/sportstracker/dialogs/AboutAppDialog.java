package com.example.sportstracker.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

/**
 * Creates information about app dialog.
 */
public class AboutAppDialog extends AppCompatDialogFragment {

    /**
     * Creates Dialog about app
     *
     * @param savedInstanceState savedInstanceState
     * @return AlertDialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("About App").setMessage("\n" + "Created by Adam Beliansky" + "\n\n" + "Version: 14.3.2021").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        return builder.create();
    }

}
