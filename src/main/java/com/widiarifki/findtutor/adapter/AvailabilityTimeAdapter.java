package com.widiarifki.findtutor.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.helper.RunnableDialogMessage;
import com.widiarifki.findtutor.app.SessionManager;
import com.widiarifki.findtutor.model.AvailabilityPerDay;
import com.widiarifki.findtutor.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by widiarifki on 10/06/2017.
 */

public class AvailabilityTimeAdapter extends ArrayAdapter<AvailabilityPerDay> {

    private Context mContext;
    private SessionManager mSession;
    private User mUserLogin;
    private List<AvailabilityPerDay> mObjects;
    private ProgressDialog mProgressDialog;

    public AvailabilityTimeAdapter(@NonNull Context context, @NonNull List<AvailabilityPerDay> objects) {
        super(context, 0, objects);

        mContext = context;
        mObjects = objects;
        mSession = new SessionManager(mContext);
        mUserLogin = mSession.getUserDetail();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_layout_time_availability, null);
        }

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setCancelable(true);

        final AvailabilityPerDay time = mObjects.get(position);

        TextView tvStartTime = (TextView) convertView.findViewById(R.id.text_start_time);
        tvStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        TextView tvEndTime = (TextView) convertView.findViewById(R.id.text_end_time);
        ImageButton btnRemove = (ImageButton) convertView.findViewById(R.id.btn_remove);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
                dialogBuilder.setMessage("Apakah anda yakin akan menghapus?");
                dialogBuilder.setPositiveButton(mContext.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onRemoveItem(parent, time);
                    }
                });
                dialogBuilder.setNegativeButton(mContext.getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = dialogBuilder.create();
                if(!alertDialog.isShowing()) alertDialog.show();
            }
        });

        if(time != null){
            tvStartTime.setText(time.getStartHour());
            tvEndTime.setText(time.getEndHour());
        }

        return convertView;
    }

    private void onRemoveItem(final ViewGroup parent, final AvailabilityPerDay time) {
        // Pass validation
        mProgressDialog.setMessage("Menghapus data...");
        if(!mProgressDialog.isShowing()) mProgressDialog.show();

        RequestBody formBody = new FormBody.Builder()
                .add("id", time.getId()+"")
                .build();

        OkHttpClient httpClient = new OkHttpClient();

        Request httpRequest = new Request.Builder()
                .url(App.URL_RMV_AVAILABILITY)
                .post(formBody)
                .build();

        Call httpCall = httpClient.newCall(httpRequest);
        httpCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // alert user
                ((Activity) mContext).runOnUiThread(new RunnableDialogMessage(mContext, "Hapus data gagal", String.valueOf(e), mProgressDialog));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseStr = response.body().string();
                if (response.isSuccessful() && response.code() == 200) {
                    JSONObject responseObj = null;
                    try {
                        responseObj = new JSONObject(responseStr);
                        int status = responseObj.getInt("success");
                        if (status == 1) {
                            ((Activity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(mProgressDialog.isShowing()) mProgressDialog.dismiss();
                                    int idDay = time.getDay();
                                    mObjects.remove(time);
                                    notifyDataSetChanged();
                                    App.setListViewHeightBasedOnChildren((ListView) parent);
                                    HashMap<String, List<AvailabilityPerDay>> availabilities = mUserLogin.getAvailabilities();
                                    if(availabilities.get(idDay+"") != null){
                                        availabilities.remove(idDay+"");
                                        // regenerate hashmap
                                        availabilities.put(idDay+"", mObjects);
                                    }
                                    mSession.updateSession(mUserLogin);
                                }
                            });
                        } else {
                            String message = responseObj.getString("error_msg");
                            ((Activity) mContext).runOnUiThread(new RunnableDialogMessage(mContext, "Hapus data gagal", message, mProgressDialog));
                        }
                    } catch (JSONException e) {
                        ((Activity) mContext).runOnUiThread(new RunnableDialogMessage(mContext, "Hapus data gagal", e.getMessage(), mProgressDialog));
                    }
                } else {

                    ((Activity) mContext).runOnUiThread(new RunnableDialogMessage(mContext, "Hapus data gagal", response.message(), mProgressDialog));
                }
            }
        });
    }

    public void addToList(AvailabilityPerDay data){
        mObjects.add(data);
        notifyDataSetChanged();
    }
}
