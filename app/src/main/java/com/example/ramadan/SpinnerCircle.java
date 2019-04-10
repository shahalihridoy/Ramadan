package com.example.ramadan;

import android.app.ProgressDialog;
import android.content.Context;

public class SpinnerCircle {

    Context context;
    ProgressDialog progressDialog;

    SpinnerCircle(Context context) {
        this.context = context;
    }

    void spin(String contentTitle, String message) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(contentTitle);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }
}
