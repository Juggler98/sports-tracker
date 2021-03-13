package com.example.sportstracker.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.sportstracker.R;

public class LoadingDialog {

    private Activity activity;
    private AlertDialog dialog;
    private boolean cancelable;
    private TextView textView;

    public LoadingDialog(Activity activity, boolean cancelable) {
        this.activity = activity;
        this.cancelable = cancelable;
        init();
    }

    @SuppressLint("InflateParams")
    private void init() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.loading_dialog, null);
        builder.setView(view);
        builder.setCancelable(cancelable);
        textView = view.findViewById(R.id.loadingText);
        dialog = builder.create();
    }

    public void startLoadingDialog() {
        if (!dialog.isShowing())
            dialog.show();
    }

    public void dismissDialog() {
        if (dialog.isShowing())
            dialog.dismiss();
    }

    public void setText(String text) {
        textView.setText(text);
    }

}
