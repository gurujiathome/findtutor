package com.widiarifki.findtutor;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

import com.rits.cloning.Cloner;
import com.widiarifki.findtutor.fragment.HomeFragment;
import com.widiarifki.findtutor.fragment.SettingAvailabilityFragment;
import com.widiarifki.findtutor.fragment.SettingsFragment;
import com.widiarifki.findtutor.helper.SessionManager;
import com.widiarifki.findtutor.model.SavedSubject;
import com.widiarifki.findtutor.model.User;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SessionManager mSession;
    User mUserLogin;
    FragmentManager mFragmentManager;

    // UI Component
    Toolbar mToolbar;
    ActionBarDrawerToggle mToggle;
    DrawerLayout mDrawer;
    int mDrawerMenuLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSession = new SessionManager(getApplicationContext());
        mUserLogin = mSession.getUserDetail();
        int isUserStudent = mUserLogin.getIsStudent();
        int isUserTutor = mUserLogin.getIsTutor();

        // adjust layout menu depend to user type
        if (isUserStudent == 1 && isUserTutor == 0) mDrawerMenuLayout = R.menu.drawer_menu_student;
        else if (isUserStudent == 0 && isUserTutor == 1) mDrawerMenuLayout = R.menu.drawer_menu_tutor;
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

        // Bind UI Component with Data
        TextView tvUserName = (TextView) navViewHeader.findViewById(R.id.text_user_name);
        TextView tvUserEmail = (TextView) navViewHeader.findViewById(R.id.text_user_email);
        ImageView btnEditProfile = (ImageView) navViewHeader .findViewById(R.id.btn_edit_profile);
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, UserProfileActivity.class);
                startActivity(i);
            }
        });

        tvUserName.setText(mUserLogin.getName());
        tvUserEmail.setText(mUserLogin.getEmail());
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

        if(itemId == R.id.nav_logout){
            Intent welcomeIntent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(welcomeIntent);
            finish();
            mSession.logout();
        }else{
            switch(menuItem.getItemId()) {
                case R.id.nav_availibility:
                    //fragment = new AvailabilityFragment();
                    fragment = new SettingAvailabilityFragment();
                    break;
                case R.id.nav_preferences:
                    fragment = new SettingsFragment();
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
                    .replace(R.id.content_layout, fragment, menuItem.getTitle()+"")
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
            if (fragmentCount != 0)
            {
                FragmentManager.BackStackEntry backEntry = mFragmentManager.getBackStackEntryAt(fragmentCount - 1);
                String fragmentTag = backEntry.getName();

                if (fragmentTag != null)
                {
                    // occassionally when user back from fragment: select subject
                    if(fragmentTag == getString(R.string.menu_preferences)){
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
        //getMenuInflater().inflate(R.menu.main, menu);
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

        if(title != null) setTitle(title);
    }

    public void addStackedFragment(Fragment fragment, String title, String stackedFragment){
        mFragmentManager.beginTransaction()
                .replace(R.id.content_layout, fragment, title)
                .addToBackStack(stackedFragment)
                .commit();

        if(title == getString(R.string.set_tutor_preference)){
            mUserLogin = mSession.getUserDetail();
            if(mUserLogin.getSubjects()!=null){
                setSelectedSubject(mUserLogin.getSubjects());
            }
        }

        if(title != null) setTitle(title);
    }

    /** To communicate between edit tutor profile and choose subject **/
    private HashMap<String, SavedSubject> mSavedSubject;

    public HashMap<String, SavedSubject> getSelectedSubject(){
        return this.mSavedSubject;
    }

    public void setSelectedSubject(HashMap<String, SavedSubject> selectedSubject){
        //this.mSavedSubject = selectedSubject;
        if(selectedSubject != null) {
            Cloner cloner=new Cloner();
            mSavedSubject = cloner.deepClone(selectedSubject);
        }
        else mSavedSubject = new HashMap<String, SavedSubject>();
    }
    /** END - To communicate between edit profile and choose subject **/

}
