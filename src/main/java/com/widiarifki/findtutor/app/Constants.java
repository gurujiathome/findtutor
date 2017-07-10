package com.widiarifki.findtutor.app;

/**
 * Created by widiarifki on 21/06/2017.
 */

public class Constants {
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME = "com.widiarifki.findtutor";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";
    public static final String PARAM_KEY_ID_USER = "id_user";
    public static final String PARAM_KEY_ID_REQUESTOR = "id_requestor";
    public static final String PARAM_KEY_ID_TUTOR = "id_tutor";
    public static final String PARAM_KEY_SUBJECTS = "subjects";
    public static final String PARAM_KEY_PHOTO_URL = "photo_url";
    public static final String PARAM_KEY_NAME = "name";
    public static final String PARAM_KEY_GENDER = "gender";
    public static final String PARAM_KEY_DISTANCE = "distance";
    public static final String PARAM_KEY_LOCATION_ADDRESS = "location_address";
    public static final String PARAM_KEY_LATITUDE = "latitude";
    public static final String PARAM_KEY_LONGITUDE = "longitude";
    public static final String PARAM_KEY_DAY = "day";
    public static final String PARAM_KEY_SCHEDULE_DATE = "schedule_date";
    public static final String PARAM_KEY_START_TIME_ID = "start_time_id";
    public static final String PARAM_KEY_TIME_LENGTH = "time_length";
    public static final String PARAM_KEY_TIMESLOTS = "timeslots";
    public static final String PARAM_KEY_BOOKED_TIME = "booked_time";
    public static final String PARAM_KEY_USER_CONTEXT = "user_context";
    public static final String PARAM_KEY_SESSION_STATE = "session_state";
    public static final String PARAM_KEY_APPROVAL_TYPE = "approval_type";
    public static final int TUTOR_AVAILABLE = 1;
    public static final int TUTOR_UNAVAILABLE = 0;
    public static final int TUTOR_AVAILABLE_BY_SCHEDULE = 2;

    public static final int SESSION_CONTEXT_AS_TUTOR = 1;
    public static final int SESSION_CONTEXT_AS_STUDENT = 2;

    public static final int SESSION_PENDING = 1;
    public static final int SESSION_RESCHEDULE = 2;
    public static final int SESSION_ACCEPTED = 3;
    public static final int SESSION_REJECTED = 4;
    public static final int SESSION_CANCELED_BY_STUDENT = 5;
    public static final int SESSION_CANCELED_BY_TUTOR = 6;
    public static final int SESSION_STARTED = 7;
    public static final int SESSION_FINISHED = 8;
}
