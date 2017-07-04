package com.widiarifki.findtutor;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.widiarifki.findtutor.adapter.TabsPagerAdapter;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.fragment.LoginFragment;
import com.widiarifki.findtutor.fragment.RegisterFragment;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If mSession has started, skip this activity
        App.loadInitActivity(WelcomeActivity.this);

        setContentView(R.layout.activity_welcome);

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);

        TabsPagerAdapter adapter = new TabsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new LoginFragment(), getString(R.string.title_activity_login));
        adapter.addFragment(new RegisterFragment(), getString(R.string.title_activity_register));
        pager.setAdapter(adapter);

        tabs.setupWithViewPager(pager);
    }

}
