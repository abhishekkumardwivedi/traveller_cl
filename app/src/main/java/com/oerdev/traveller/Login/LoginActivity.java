package com.oerdev.traveller.Login;

import com.oerdev.traveller.Network.HttpHandler;
import com.oerdev.traveller.R;
import com.oerdev.traveller.app.AppConfigs;
import com.oerdev.traveller.app.Logger;
import com.oerdev.traveller.app.SessionManager;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    public static final Map<Integer, Integer> imgMap = new HashMap<>();
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final Map<Integer, Integer> msgMap = new HashMap<>();
    public static Activity activity;
    private static Context mContext;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getApplicationContext();
        SessionManager session = SessionManager.getInstance(mContext);

        Logger.d(TAG, "onCreate:");
        Logger.d(TAG, "user id: " + session.getUserId());
        Logger.d(TAG, "user name: " + session.getProfileName());
        Logger.d(TAG, "user email: " + session.getProfileEmail());
        Logger.d(TAG, "-----------------------");

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.oerdev.traveller",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Logger.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        if (session.getUserId() != null) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_splash_screen);
            HttpHandler httpHandler = new HttpHandler(mContext);
            httpHandler.loginUser(AppConfigs.KEY_ID);
            return;
        }
        msgMap.put(1, R.string.help_1);
        msgMap.put(2, R.string.help_2);
        msgMap.put(3, R.string.help_3);
        msgMap.put(4, R.string.help_4);
        msgMap.put(5, R.string.login);

        imgMap.put(1, R.drawable.umbrella);
        imgMap.put(2, R.drawable.marker);
        imgMap.put(3, R.drawable.ind_calendar);
        imgMap.put(4, R.drawable.journey);
        imgMap.put(5, R.drawable.ind_calendar);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_walkthrough, container, false);
            LoginActivity.activity = getActivity();

            TextView msgView = (TextView) rootView.findViewById(R.id.section_message);
            msgView.setText(msgMap.get(getArguments().getInt(ARG_SECTION_NUMBER)));

            ImageView imgView = (ImageView) rootView.findViewById(R.id.section_img);
            imgView.setImageResource(imgMap.get(getArguments().getInt(ARG_SECTION_NUMBER)));

            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Logger.d(TAG, "getItem: " + position);
            if (position == 4) {
                return LoginFragment.newInstance(position + 1, getApplication());
            } else {
                return PlaceholderFragment.newInstance(position + 1);
            }
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }
}
