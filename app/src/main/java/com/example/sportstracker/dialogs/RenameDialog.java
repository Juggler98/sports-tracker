package com.example.sportstracker.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.sportstracker.R;

/**
 * Dialog for rename Activity name.
 */
public class RenameDialog extends AppCompatDialogFragment {

    private EditText editTextRename;
    private RenameDialogListener renameDialogListener;

    /**
     * Dialog for Renaming activity.
     *
     * @param savedInstanceState savedInstanceState
     * @return AlertDialog
     */
    @NonNull
    @Override
    @SuppressLint("InflateParams")
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Activity activity = requireActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.rename_dialog, null);

        builder.setView(view).setTitle("Rename Activity").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = editTextRename.getText().toString();
                renameDialogListener.applyText(name);
            }
        });

        editTextRename = view.findViewById(R.id.rename);
        SharedPreferences sharedPreferences = activity.getSharedPreferences(getString(R.string.sharedPreferences), Context.MODE_PRIVATE);
        String routeName = sharedPreferences.getString(getString(R.string.renamePref), "");
        editTextRename.setText(routeName);

        return builder.create();
    }

    /**
     * Attach renameDialogListener as RenameDialogListener.
     *
     * @param context context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            renameDialogListener = (RenameDialogListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates interface with method applyText, which can cause rename of activity.
     */
    public interface RenameDialogListener {
        void applyText(String name);
    }
}
