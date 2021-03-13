package com.example.sportstracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class LoadingDialog {

    Activity activity;
    AlertDialog dialog;

    LoadingDialog(Activity activity) {
        this.activity = activity;
        init();
    }

    private void init() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading_dialog, null));
        builder.setCancelable(false);
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

}
