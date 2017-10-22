package com.widiarifki.findtutor;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.rits.cloning.Cloner;
import com.squareup.picasso.Picasso;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.app.SessionManager;
import com.widiarifki.findtutor.fragment.HelpFragment;
import com.widiarifki.findtutor.fragment.HomeFragment;
import com.widiarifki.findtutor.fragment.SearchTutorFragment;
import com.widiarifki.findtutor.fragment.SessionFragment;
import com.widiarifki.findtutor.fragment.SessionPreviousFragment;
import com.widiarifki.findtutor.fragment.SettingAccountFragment;
import com.widiarifki.findtutor.fragment.SettingAvailabilityFragment;
import com.widiarifki.findtutor.fragment.SettingProfileFragment;
import com.widiarifki.findtutor.fragment.SettingTutorPrefFragment;
import com.widiarifki.findtutor.helper.CircleTransform;
import com.widiarifki.findtutor.model.SavedSubject;
import com.widiarifki.findtutor.model.User;

import org.joda.time.LocalDate;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SessionManager mSession;
    User mUserLogin;
    FragmentManager mFragmentManager;
    protected GoogleApiClient mGoogleApiClient;

    // UI Component
    Toolbar mToolbar;
    DrawerLayout mDrawer;
    ActionBarDrawerToggle mDrawerToggle;
    int mDrawerMenuLayout;
    NavigationView mNavigationView;

    private FusedLocationProviderApi mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSession = new SessionManager(getApplicationContext());
        mUserLogin = mSession.getUserDetail();

        setContentView(R.layout.activity_main);

        // Toolbar/Action Bar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        int isUserStudent = mUserLogin.getIsStudent();
        int isUserTutor = mUserLogin.getIsTutor();
        // adjust layout menu depend to user type
        if (isUserStudent == 1 && isUserTutor == 0)
            mDrawerMenuLayout = R.menu.drawer_menu_student;
        else if (isUserStudent == 0 && isUserTutor == 1)
            mDrawerMenuLayout = R.menu.drawer_menu_tutor;
        else
            mDrawerMenuLayout = R.menu.drawer_menu_all;

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.inflateMenu(mDrawerMenuLayout);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

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
                    mDrawerToggle.syncState();
                    mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDrawer.openDrawer(GravityCompat.START);
                        }
                    });
                }
            }
        });

        selectDrawerItem(mNavigationView.getMenu().getItem(0));

        // Navigation header instance
        View navViewHeader = mNavigationView.getHeaderView(0);

        // Bind Nav header Component with Data
        TextView tvUserName = (TextView) navViewHeader.findViewById(R.id.text_user_name);
        TextView tvUserEmail = (TextView) navViewHeader.findViewById(R.id.text_user_email);
        TextView tvUserType = (TextView) navViewHeader.findViewById(R.id.text_user_type);

        ImageView imgUserPhoto = (ImageView) navViewHeader.findViewById(R.id.imgv_user_photo);

        Picasso.with(this).load(App.URL_PATH_PHOTO + mUserLogin.getPhotoUrl())
                .transform(new CircleTransform())
                .placeholder(R.drawable.ic_person_black_24dp)
                .error(R.drawable.ic_broken_image_black_24dp)
                .into(imgUserPhoto);
        tvUserName.setText(mUserLogin.getName());
        tvUserEmail.setText(mUserLogin.getEmail());
        String userType = "";
        if(mUserLogin.getIsTutor() == 0 && mUserLogin.getIsStudent() == 1){
            userType = "Siswa";
        }
        else if(mUserLogin.getIsTutor() == 1 && mUserLogin.getIsStudent() == 0){
            userType = "Tutor";
        }
        else if(mUserLogin.getIsTutor() == 1 && mUserLogin.getIsStudent() == 1){
            userType = "Tutor & Siswa";
        }
        tvUserType.setText(userType);
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
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage("Anda yakin akan logout dari akun anda?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent welcomeIntent = new Intent(MainActivity.this, WelcomeActivity.class);
                            startActivity(welcomeIntent);
                            finish();
                            mSession.logout();
                        }
                    }).create().show();
        } else {
            switch (menuItem.getItemId()) {
                case R.id.nav_search_tutor:
                    fragment = new SearchTutorFragment();
                    break;
                case R.id.nav_my_session:
                    fragment = new SessionFragment();
                    break;
                case R.id.nav_session_history:
                    fragment = new SessionPreviousFragment();
                    break;
                case R.id.nav_availibility:
                    fragment = new SettingAvailabilityFragment();
                    break;
                case R.id.nav_set_basic_profile:
                    fragment = new SettingProfileFragment();
                    break;
                case R.id.nav_set_tutor_pref:
                    fragment = new SettingTutorPrefFragment();
                    break;
                case R.id.nav_set_account:
                    fragment = new SettingAccountFragment();
                    break;
                case R.id.nav_help:
                    fragment = new HelpFragment();
                    break;
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
            // Close the drawer
            mDrawer.closeDrawers();

            if (menuItem.getItemId() == R.id.nav_set_tutor_pref) {
                mUserLogin = mSession.getUserDetail();
                if (mUserLogin.getSubjects() != null) {
                    setSelectedSubject(mUserLogin.getSubjects());
                }else{
                    setSelectedSubject(new HashMap<String, SavedSubject>());
                }
            }else{
                mTempSavedLocation = null;
                mTempSavedLocationAddress = null;
                setSelectedSubject(null);
            }
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
        return super.onOptionsItemSelected(item);
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

    public void popAllFragment(){
        FragmentManager fm = mFragmentManager;
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    public void switchMenu(int id){
        MenuItem itemMenu = mNavigationView.getMenu().findItem(id);
        selectDrawerItem(itemMenu);
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
}
