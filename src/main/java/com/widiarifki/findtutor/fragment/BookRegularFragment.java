package com.widiarifki.findtutor.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rits.cloning.Cloner;
import com.widiarifki.findtutor.MainActivity;
import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.adapter.SelectHourAdapter;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.app.Constants;
import com.widiarifki.findtutor.app.SessionManager;
import com.widiarifki.findtutor.model.SavedSubject;
import com.widiarifki.findtutor.model.SubjectTopic;
import com.widiarifki.findtutor.model.User;

import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
 * Created by widiarifki on 04/08/2017.
 */

public class BookRegularFragment extends Fragment {
    Context mContext;
    private MainActivity mParentActivity;

    TextView mTvFreq;
    TextView mTvDay;
    TextView mTvDate;
    TextView mTvDateEnd;
    private TextView mTvTime;
    Button mBtnBook;

    private int mSelectedFreq = 2;
    List<Integer> mSelectedDay = new ArrayList<>();
    private LocalDate mStartDate;
    int mSelectedD = 0;
    int mSelectedM = 0;
    int mSelectedY = 0;
    LocalDate mEndDate;
    int mSelectedDEnd = 0;
    int mSelectedMEnd = 0;
    int mSelectedYEnd = 0;
    private LinearLayout selectDay;
    private SessionManager mSession;
    private User mUserLogin;
    private int mIdTutor;
    private Activity mContextActivity;
    private int mStartTime;
    private int mEndTime;
    private String mStudentLatitude;
    private String mStudentLongitude;
    private String mStudentLocAddress;
    double mDistance;
    private HashMap<String, SavedSubject> mSubjects;
    int mIdSubject;
    private ArrayList<Integer> mSelectedTimes;
    private ArrayList<Integer> mSelectedTimesTemp;
    private Fragment mThisFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mContextActivity = (Activity)mContext;
        mParentActivity = (MainActivity) getActivity();
        mThisFragment = this;

        Bundle givenParams = getArguments();
        mIdTutor = givenParams.getInt(Constants.PARAM_KEY_ID_USER, 0);
        mDistance = givenParams.getDouble(Constants.PARAM_KEY_DISTANCE, 0);

        mEndDate = mStartDate = new LocalDate();
        mStudentLatitude = "";
        mStudentLongitude = "";
        if(mParentActivity.mSearchTutorLocation != null){
            Location studentLocation = mParentActivity.mSearchTutorLocation;
            mStudentLatitude = String.valueOf(studentLocation.getLatitude());
            mStudentLongitude = String.valueOf(studentLocation.getLongitude());
            mStudentLocAddress = mParentActivity.mSearchTutorLocationTxt;
        }

        mSubjects = new HashMap<String, SavedSubject>();
        if(mParentActivity.getSearchTutorSubject() != null){
            mSubjects = mParentActivity.getSearchTutorSubject();
        }
        mSelectedTimes = new ArrayList<Integer>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mSession = new SessionManager(mContext);
        mUserLogin = mSession.getUserDetail();

        View view = inflater.inflate(R.layout.fragment_book_reguler, container, false);

        mTvFreq = (TextView) view.findViewById(R.id.tvFreq);
        mTvDay = (TextView) view.findViewById(R.id.tvDay);
        mTvDate = (TextView) view.findViewById(R.id.tvDate);
        mTvDateEnd = (TextView) view.findViewById(R.id.tvDateEnd);
        mTvTime = (TextView) view.findViewById(R.id.tvTime);

        LinearLayout selectFreq = (LinearLayout) view.findViewById(R.id.selectFreq);
        mTvFreq.setText("Mingguan");
        selectFreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_layout_select_freq, null);
                final RadioGroup rgrupFreq = (RadioGroup) dialogView.findViewById(R.id.rgrup_gender);
                if(mSelectedFreq == 1) rgrupFreq.check(R.id.radio_opt_daily);
                if(mSelectedFreq == 2) rgrupFreq.check(R.id.radio_opt_weekly);

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Pilih Frekuensi");
                builder.setView(dialogView);
                builder.setNegativeButton(getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String frequencyTxt = "";
                        int selectedGender = rgrupFreq.getCheckedRadioButtonId();
                        if(selectedGender == R.id.radio_opt_daily){
                            frequencyTxt = getString(R.string.label_opt_daily);
                            mSelectedFreq = 1;
                        }
                        else if(selectedGender == R.id.radio_opt_weekly){
                            frequencyTxt = getString(R.string.label_opt_weekly);
                            mSelectedFreq = 2;
                        }
                        mTvFreq.setText(frequencyTxt);
                        setSelectDayVisibility();
                    }
                });

                AlertDialog alertDialog = builder.create();
                if(!alertDialog.isShowing()) alertDialog.show();
            }
        });

        selectDay = (LinearLayout) view.findViewById(R.id.selectDay);
        selectDay.setOnClickListener(showDialogDay());
        setSelectDayVisibility();

        final LinearLayout selectDate = (LinearLayout) view.findViewById(R.id.selectDate);
        selectDate.setOnClickListener(showDialogDate("start"));

        final LinearLayout selectDateEnd = (LinearLayout) view.findViewById(R.id.selectDateEnd);
        selectDateEnd.setOnClickListener(showDialogDate("end"));

        final LinearLayout selectTime = (LinearLayout) view.findViewById(R.id.selectTime);
        selectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_layout_select_time, null);
                RecyclerView rv = (RecyclerView) dialogView.findViewById(R.id.recyclerView);

                mSelectedTimesTemp = new ArrayList<Integer>();
                rv.setAdapter(new SelectHourAdapter(mContext, mThisFragment, mSelectedTimes));
                rv.setLayoutManager(new GridLayoutManager(mContext, 3));

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Pilih Waktu Pertemuan");
                builder.setView(dialogView);
                builder.setNegativeButton(getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Cloner cloner = new Cloner();
                        mSelectedTimes = cloner.deepClone(mSelectedTimesTemp);
                        if(mSelectedTimes != null && mSelectedTimes.size() > 0){
                            mStartTime = mSelectedTimes.get(0);
                            mEndTime = mStartTime + mSelectedTimes.size();
                            String startHour = String.format("%02d", mStartTime) + ":00";
                            String endHour = String.format("%02d", mEndTime) + ":00";
                            mTvTime.setText(startHour + " - " + endHour);
                        }else{
                            mTvTime.setText("Jam");
                        }
                    }
                });

                AlertDialog alertDialog = builder.create();
                if(!alertDialog.isShowing()) alertDialog.show();
            }
        });

        mBtnBook = (Button) view.findViewById(R.id.btnSendBook);
        mBtnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doBooking();
            }
        });

        return view;
    }

    private void doBooking() {
        List<Integer> subjectId = new ArrayList<Integer>();
        if (mSubjects != null) {
            for (SavedSubject group : mSubjects.values()) {
                int idParent = group.getCategoryId();
                HashMap<String, SubjectTopic> topic = group.getTopicList();
                if (topic.containsKey(idParent + "") && !subjectId.contains(idParent)) {
                    subjectId.add(idParent);
                } else {
                    for (SubjectTopic topicItem : topic.values())
                        subjectId.add(topicItem.getId());
                }
            }
        }

        int timeLn = mEndTime + 1 - mStartTime;
        String jsonSubjectList = new Gson().toJson(subjectId);
        String jsonDays = new Gson().toJson(mSelectedDay);
        RequestBody formBody = new FormBody.Builder()
                .add("id_student", mUserLogin.getId() + "")
                .add("id_tutor", mIdTutor + "")
                .add("frequence_type", mSelectedFreq + "")
                .add("start_date", mStartDate.toString("yyy-MM-dd"))
                .add("end_date", mEndDate.toString("yyy-MM-dd"))
                .add("days", jsonDays)
                .add(Constants.PARAM_KEY_START_TIME_ID, mStartTime + "")
                .add(Constants.PARAM_KEY_TIME_LENGTH, timeLn + "")
                .add("id_subject", String.valueOf(subjectId.get(0)))
                .add(Constants.PARAM_KEY_LATITUDE, mStudentLatitude + "")
                .add(Constants.PARAM_KEY_LONGITUDE, mStudentLongitude + "")
                .add(Constants.PARAM_KEY_LOCATION_ADDRESS, mStudentLocAddress + "")
                .add(Constants.PARAM_KEY_DISTANCE, mDistance + "")
                .build();

        OkHttpClient httpClient = new OkHttpClient();
        Request httpRequest = new Request.Builder()
                .url(App.URL_POST_BOOK_REGULAR)
                .post(formBody)
                .build();

        Call httpCall = httpClient.newCall(httpRequest);
        httpCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200 && response.isSuccessful()) {
                    String json = response.body().string();
                    JSONObject jsonObj = null;
                    try {
                        jsonObj = new JSONObject(json);
                        if(jsonObj.getInt("success") == 1){
                            mContextActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                    builder.setTitle("Booking Berhasil");
                                    builder.setMessage("Permintaan sesi telah terkirim ke tutor");
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ((MainActivity)mContext).popAllFragment();
                                            ((MainActivity)mContext).switchMenu(R.id.nav_my_session);
                                        }
                                    });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            });
                        }else{

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        });

    }

    private void setSelectDayVisibility() {
        if(mSelectedFreq == 2) {
            selectDay.setVisibility(View.VISIBLE);
        }else{
            selectDay.setVisibility(View.GONE);
        }
    }

    private View.OnClickListener showDialogDate(final String type) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Set default value */
                int year = type.equalsIgnoreCase("start") ? mSelectedY : mSelectedYEnd;
                int month = type.equalsIgnoreCase("start") ? mSelectedM - 1 : mSelectedMEnd - 1;
                int day = type.equalsIgnoreCase("start") ? mSelectedD : mSelectedDEnd;

                if (year == 0 && month == 0 && day == 0) {
                    final Calendar c = Calendar.getInstance();
                    year = c.get(Calendar.YEAR);
                    month = c.get(Calendar.MONTH);
                    day = c.get(Calendar.DAY_OF_MONTH);
                }

                View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_layout_select_date, null);
                final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker);
                datePicker.init(year, month, day, null);
                if(type == "end"){
                    datePicker.setMinDate(mStartDate.toDateTimeAtStartOfDay().getMillis());
                }else {
                    datePicker.setMinDate(System.currentTimeMillis() - 1000);
                }

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setNegativeButton(getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialogBuilder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(type.equalsIgnoreCase("start")) {
                            mSelectedD = datePicker.getDayOfMonth();
                            mSelectedM = datePicker.getMonth() + 1;
                            mSelectedY = datePicker.getYear();
                            mStartDate = new LocalDate(mSelectedY, mSelectedM, mSelectedD);
                            mTvDate.setText(
                                    Constants.DAY_INA[Integer.parseInt(mStartDate.toString("e")) - 1] + ", " +
                                            mStartDate.toString("dd MMM yyy")
                            );
                            if(mEndDate.isBefore(mStartDate)){
                                mEndDate = mStartDate;
                                mTvDateEnd.setText(
                                        Constants.DAY_INA[Integer.parseInt(mEndDate.toString("e")) - 1] + ", " +
                                                mEndDate.toString("dd MMM yyy")
                                );
                            }
                        }else{
                            mSelectedDEnd = datePicker.getDayOfMonth();
                            mSelectedMEnd = datePicker.getMonth() + 1;
                            mSelectedYEnd = datePicker.getYear();
                            mEndDate = new LocalDate(mSelectedYEnd, mSelectedMEnd, mSelectedDEnd);
                            mTvDateEnd.setText(
                                    Constants.DAY_INA[Integer.parseInt(mEndDate.toString("e")) - 1] + ", " +
                                            mEndDate.toString("dd MMM yyy")
                            );
                        }
                    }
                });
                AlertDialog alertDialog = dialogBuilder.create();
                if (!alertDialog.isShowing()) alertDialog.show();
            }
        };
    };

    private View.OnClickListener showDialogDay(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_layout_select_day, null);
                final CheckBox cbox1 = (CheckBox) dialogView.findViewById(R.id.cbox1);
                cbox1.setOnCheckedChangeListener(onCheckedChange(1));
                final CheckBox cbox2 = (CheckBox) dialogView.findViewById(R.id.cbox2);
                cbox2.setOnCheckedChangeListener(onCheckedChange(2));
                final CheckBox cbox3 = (CheckBox) dialogView.findViewById(R.id.cbox3);
                cbox3.setOnCheckedChangeListener(onCheckedChange(3));
                final CheckBox cbox4 = (CheckBox) dialogView.findViewById(R.id.cbox4);
                cbox4.setOnCheckedChangeListener(onCheckedChange(4));
                final CheckBox cbox5 = (CheckBox) dialogView.findViewById(R.id.cbox5);
                cbox5.setOnCheckedChangeListener(onCheckedChange(5));
                final CheckBox cbox6 = (CheckBox) dialogView.findViewById(R.id.cbox6);
                cbox6.setOnCheckedChangeListener(onCheckedChange(6));
                final CheckBox cbox7 = (CheckBox) dialogView.findViewById(R.id.cbox7);
                cbox7.setOnCheckedChangeListener(onCheckedChange(7));

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Pilih Hari");
                builder.setView(dialogView);
                builder.setNegativeButton(getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Collections.sort(mSelectedDay);
                        String[] days  = {"Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu"};
                        String[] selectedDays = new String[mSelectedDay.size()];
                        int i = 0;
                        for(Integer day : mSelectedDay){
                            selectedDays[i] = days[(day-1)];
                            i++;
                        }

                        mTvDay.setText(TextUtils.join(", ", selectedDays));
                    }
                });

                AlertDialog alertDialog = builder.create();
                if(!alertDialog.isShowing()) alertDialog.show();
            }
        };
    }

    private CompoundButton.OnCheckedChangeListener onCheckedChange(final int id) {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(!mSelectedDay.contains(id)){
                        mSelectedDay.add(id);
                    }
                }else{
                    if(mSelectedDay.contains(Integer.valueOf(id))){
                        mSelectedDay.remove(Integer.valueOf(id));
                    }
                }
            }
        };
    }

    public void addTime(int timeId) {
        Integer value = Integer.valueOf(timeId);
        if(!mSelectedTimesTemp.contains(value))
            mSelectedTimesTemp.add(value);
    }

    public void removeTime(int timeId) {
        Integer value = Integer.valueOf(timeId);
        if(mSelectedTimesTemp.contains(value)) {
            int index = mSelectedTimesTemp.indexOf(value);
            mSelectedTimesTemp.remove(index);
        }
    }
}
