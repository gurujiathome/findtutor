package com.widiarifki.findtutor;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.rits.cloning.Cloner;
import com.squareup.picasso.Picasso;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.fragment.HomeFragment;
import com.widiarifki.findtutor.fragment.MySessionFragment;
import com.widiarifki.findtutor.fragment.NotificationFragment;
import com.widiarifki.findtutor.fragment.SearchTutorFragment;
import com.widiarifki.findtutor.fragment.SettingAvailabilityFragment;
import com.widiarifki.findtutor.fragment.SettingFragment;
import com.widiarifki.findtutor.helper.CircleTransform;
import com.widiarifki.findtutor.app.SessionManager;
import com.widiarifki.findtutor.model.SavedSubject;
import com.widiarifki.findtutor.model.User;

import org.joda.time.LocalDate;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    SessionManager mSession;
    User mUserLogin;
    FragmentManager mFragmentManager;
    protected GoogleApiClient mGoogleApiClient;

    // UI Component
    Toolbar mToolbar;
    ActionBarDrawerToggle mToggle;
    DrawerLayout mDrawer;
    int mDrawerMenuLayout;
    private FusedLocationProviderApi mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //buildGoogleApiClient();

        setContentView(R.layout.activity_main);

        mSession = new SessionManager(getApplicationContext());
        mUserLogin = mSession.getUserDetail();
        int isUserStudent = mUserLogin.getIsStudent();
        int isUserTutor = mUserLogin.getIsTutor();

        // adjust layout menu depend to user type
        if (isUserStudent == 1 && isUserTutor == 0)
            mDrawerMenuLayout = R.menu.drawer_menu_student;
        else if (isUserStudent == 0 && isUserTutor == 1)
            mDrawerMenuLayout = R.menu.drawer_menu_tutor;
        else mDrawerMenuLayout = R.menu.drawer_menu_all;

        // Toolbar/Action Bar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(mToggle);
        mToggle.syncState();

        // treatment for fragment manager
        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back button
                    mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onBackPressed();
                        }
                    });
                } else {
                    //show hamburger
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    mToggle.syncState();
                    mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDrawer.openDrawer(GravityCompat.START);
                        }
                    });
                }
            }
        });

        // Navigation view instance
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.inflateMenu(mDrawerMenuLayout);
        // Initially select first menu
        selectDrawerItem(navigationView.getMenu().getItem(0));

        // Navigation header instance
        View navViewHeader = navigationView.getHeaderView(0);

        // Bind Nav header Component with Data
        TextView tvUserName = (TextView) navViewHeader.findViewById(R.id.text_user_name);
        TextView tvUserEmail = (TextView) navViewHeader.findViewById(R.id.text_user_email);
        ImageView imgUserPhoto = (ImageView) navViewHeader.findViewById(R.id.imgv_user_photo);
        /*ImageView btnEditProfile = (ImageView) navViewHeader.findViewById(R.id.btn_edit_profile);
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, UserProfileActivity.class);
                startActivity(i);
            }
        });*/

        Picasso.with(this).load(App.URL_PATH_PHOTO + mUserLogin.getPhotoUrl())
                .transform(new CircleTransform())
                .placeholder(R.drawable.ic_person_black_24dp)
                .error(R.drawable.ic_broken_image_black_24dp)
                .into(imgUserPhoto);
        tvUserName.setText(mUserLogin.getName());
        tvUserEmail.setText(mUserLogin.getEmail());
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        selectDrawerItem(item);
        return true;
    }

    // Event when drawer menu selected
    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass = null;
        int itemId = menuItem.getItemId();

        if (itemId == R.id.nav_logout) {
            Intent welcomeIntent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(welcomeIntent);
            finish();
            mSession.logout();
        } else {
            switch (menuItem.getItemId()) {
                case R.id.nav_notification:
                    fragment = new NotificationFragment();
                    break;
                case R.id.nav_search_tutor:
                    fragment = new SearchTutorFragment();
                    break;
                case R.id.nav_my_session:
                    fragment = new MySessionFragment();
                    break;
                case R.id.nav_availibility:
                    fragment = new SettingAvailabilityFragment();
                    break;
                case R.id.nav_preferences:
                    fragment = new SettingFragment();
                    break;
                /*case R.id.nav_set_basic_profile:
                    fragment = new SettingProfileFragment();
                    break;
                case R.id.nav_set_tutor_pref:
                    fragment = new SettingTutorPrefFragment();
                    break;
                case R.id.nav_set_account:
                    fragment = new SettingAccountFragment();
                    break;*/
                default:
                    fragmentClass = HomeFragment.class;
            }

            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Insert the fragment by replacing any existing fragment
            mFragmentManager.beginTransaction()
                    .replace(R.id.content_layout, fragment, menuItem.getTitle() + "")
                    .commit();

            // Highlight the selected item has been done by NavigationView
            menuItem.setChecked(true);
            // Set action bar title
            setTitle(menuItem.getTitle());
            // Close the bottom_menu drawer
            mDrawer.closeDrawers();
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            // Check fragment back stack so we can set the right action bar title
            int fragmentCount = mFragmentManager.getBackStackEntryCount();
            if (fragmentCount != 0) {
                FragmentManager.BackStackEntry backEntry = mFragmentManager.getBackStackEntryAt(fragmentCount - 1);
                String fragmentTag = backEntry.getName();

                if (fragmentTag != null) {
                    // in case user back from fragment: select subject
                    if (fragmentTag == getString(R.string.menu_preferences)) {
                        mTempSavedLocation = null;
                        mTempSavedLocationAddress = null;
                        setSelectedSubject(null);
                    }
                    setTitle(fragmentTag);
                }
            }

            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.appbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    // dipanggil dari class lain utk mereplace fragment
    public void switchContent(Fragment fragment) {
        mFragmentManager.beginTransaction().replace(R.id.content_layout, fragment)
                .commit();
    }

    public void switchContent(Fragment fragment, String title) {
        mFragmentManager.beginTransaction()
                .replace(R.id.content_layout, fragment, title)
                .commit();

        if (title != null) setTitle(title);
    }

    public void addStackedFragment(Fragment fragment, String title, String stackedFragment) {
        mFragmentManager.beginTransaction()
                .replace(R.id.content_layout, fragment, title)
                .addToBackStack(stackedFragment)
                .commit();

        /** Set tutor preference subject with the ones saved in session when navigate to Setting Tutor Pref */
        if (title == getString(R.string.set_tutor_preference)) {
            mUserLogin = mSession.getUserDetail();
            if (mUserLogin.getSubjects() != null) {
                setSelectedSubject(mUserLogin.getSubjects());
            }
        }

        if (title != null) setTitle(title);
    }

    public void addStackedFragment(Fragment currentFragment, Fragment newFragment, String title, String stackedFragment) {
        mFragmentManager.beginTransaction()
                .hide(currentFragment)
                .add(R.id.content_layout, newFragment, title)
                .addToBackStack(stackedFragment)
                .commit();

        /** Set tutor preference subject with the ones saved in session when navigate to Setting Tutor Pref */
        if (title == getString(R.string.set_tutor_preference)) {
            mUserLogin = mSession.getUserDetail();
            if (mUserLogin.getSubjects() != null) {
                setSelectedSubject(mUserLogin.getSubjects());
            }
        }

        if (title != null) setTitle(title);
    }

    public void removeFragmentFromStack(Fragment fragment){
        FragmentTransaction fragmentTrans = mFragmentManager.beginTransaction();
        fragmentTrans.remove(fragment);
        fragmentTrans.commit();
        mFragmentManager.popBackStack();

        int fragmentCount = mFragmentManager.getBackStackEntryCount();
        if (fragmentCount != 0) {
            FragmentManager.BackStackEntry backEntry = mFragmentManager.getBackStackEntryAt(fragmentCount - 1);
            String fragmentTag = backEntry.getName();

            if (fragmentTag != null) {
                setTitle(fragmentTag);
            }
        }
    }

    /** Passing value between edit tutor profile and choose subject **/
    public static Location mTempSavedLocation;
    public static String mTempSavedLocationAddress;

    private HashMap<String, SavedSubject> mSavedSubject;
    public HashMap<String, SavedSubject> getSelectedSubject() {
        return this.mSavedSubject;
    }
    public void setSelectedSubject(HashMap<String, SavedSubject> selectedSubject) {
        //this.mSavedSubject = selectedSubject;
        if (selectedSubject != null) {
            Cloner cloner = new Cloner();
            mSavedSubject = cloner.deepClone(selectedSubject);
        } else {
            mSavedSubject = new HashMap<String, SavedSubject>();
        }
    }
    /** END - Passing value  between edit profile and choose subject **/

    /** Variable for saerch tutor **/
    public static LocalDate mSearchTutorDate;
    public static Location mSearchTutorLocation;
    public static String mSearchTutorLocationTxt;
    private HashMap<String, SavedSubject> mSearchTutorSubject;
    public HashMap<String, SavedSubject> getSearchTutorSubject() {
        return this.mSearchTutorSubject;
    }
    public void setSearchTutorSubject(HashMap<String, SavedSubject> selectedSubject) {
        if (selectedSubject != null) {
            Cloner cloner = new Cloner();
            mSearchTutorSubject = cloner.deepClone(selectedSubject);
        } else {
            mSearchTutorSubject = new HashMap<String, SavedSubject>();
        }
    }
    /** END - Variable for saerch tutor **/

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if(locationResult != null) {
                List<Location> locationList = locationResult.getLocations();
                if (locationList != null){
                    if(locationList.size() > 0){
                        App.detectedDeviceLocations = locationList;
                        App.detectedDeviceLocation = locationList.get(0);
                        stopLocationUpdates();
                    }
                }
            }
        };
    };

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mGoogleApiClient, mLocationCallback);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // Update location every second

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient = LocationServices.FusedLocationApi;
        mFusedLocationClient.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mLocationCallback, null);
        /*mFusedLocationClient
                .requestLocationUpdates(mGoogleApiClient, mLocationRequest, mLocationCallback, null);*/
                //.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
