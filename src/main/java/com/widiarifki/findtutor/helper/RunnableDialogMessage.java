package com.widiarifki.findtutor.helper;

import android.app.ProgressDialog;
import android.content.Context;

import com.widiarifki.findtutor.app.App;

/**
 * Created by widiarifki on 10/06/2017.
 */

public class RunnableDialogMessage implements Runnable {
    Context mContext;
    String mTitle;
    String mMessage;
    ProgressDialog mProgressDialog;

    public RunnableDialogMessage(Context context, String title, String message) {
        mContext = context;
        mTitle = title;
        mMessage = message;
    }

    public RunnableDialogMessage(Context context, String title, String message, ProgressDialog progressDialog) {
        mContext = context;
        mTitle = title;
        mMessage = message;
        mProgressDialog = progressDialog;
    }

    public void run() {
        if(mProgressDialog != null){
            if(mProgressDialog.isShowing()) mProgressDialog.dismiss();
        }
        App.showSimpleDialog(mContext, mTitle, mMessage);
    }
}
