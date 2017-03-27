package com.oerdev.traveller.Home;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.oerdev.traveller.Contacts.ContactsFragment;
import com.oerdev.traveller.Google.LocationService;
import com.oerdev.traveller.Google.MapsActivity;
import com.oerdev.traveller.Hangout.HangoutFragment;
import com.oerdev.traveller.Network.HttpHandler;
import com.oerdev.traveller.PlaceDetails.DetailedActivity;
import com.oerdev.traveller.R;
import com.oerdev.traveller.Suggest.SuggestFragment;
import com.oerdev.traveller.User.MyProfile;
import com.oerdev.traveller.app.AppConfigs;
import com.oerdev.traveller.app.Logger;
import com.oerdev.traveller.app.SessionManager;
import com.oerdev.traveller.myPlan.MyPlanFragment;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class HomeTabbedActivity
        extends AppCompatActivity
        implements SuggestFragment.SuggestFIListener,
        HangoutFragment.PopularFIListener,
        MyPlanFragment.MyPlanFIListener,
        SearchView.OnQueryTextListener,
        ContactsFragment.ProfileFIListener,
        LocationService.LocationListener, MenuItem.OnMenuItemClickListener {

    private static final String TAG = HomeTabbedActivity.class.getSimpleName();
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private static final int PLACE_PICKER_REQUEST = 2;
    private static final int REQUEST_CODE_SHARE_TO_MESSAGE = 3;
    public static Context sContext;
    private SuggestFragment suggestFragment;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed_home);

        sContext = getApplicationContext();
        setTitle("Traveller");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        getCloudData();
    }

    private void getCloudData() {
        HttpHandler httpHandler = new HttpHandler(this);
        httpHandler.getSuggestContent();
        httpHandler.getHangoutContent();
        httpHandler.getUserData();
        httpHandler.getMyPlan();
    }

    private void openAutocompleteActivity() {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Log.e(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        switch (requestCode) {
            case REQUEST_CODE_AUTOCOMPLETE:
                if (resultCode == RESULT_OK) {
                    // Get the user's selected place from the Intent.
                    Place place = PlaceAutocomplete.getPlace(this, data);
                    Log.i(TAG, "Place Selected: " + place.getName());

                    // Format the place's details and display them in the TextView.
                    Logger.d(TAG, place.getName().toString());
                    Logger.d(TAG, place.getLatLng().latitude + " " + place.getLatLng().longitude);
                    Logger.d(TAG, place.getId().toString());

                    // Display attributions if required.
                    CharSequence attributions = place.getAttributions();
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(this, data);
                    Log.e(TAG, "Error: Status = " + status.toString());
                } else if (resultCode == RESULT_CANCELED) {
                    // Indicates that the activity closed before a selection was made. For example if
                    // the user pressed the back button.
                }
                break;

            case PLACE_PICKER_REQUEST:
                if (resultCode == RESULT_OK) {
                    com.google.android.gms.location.places.Place p
                            = PlacePicker.getPlace(this, data);
                    Logger.d(TAG, "Name :" + p.getName());
                    Logger.d(TAG, "Addr :" + p.getAddress());
                    Logger.d(TAG, "Id   :" + p.getId());

                    HttpHandler httpHandler = new HttpHandler(this);
                    httpHandler.getGooglePlaceDetailsJson(p.getId());
//                    GooglePlaceGetter googlePlaceGetter = new GooglePlaceGetter(this);
//                    googlePlaceGetter.setImageSize(160, 160);
//                    googlePlaceGetter.getGooglePlaceDetails(p.getId());
//                    com.oerdev.traveller.ItemsModel.Place place = new com.oerdev.traveller.ItemsModel.Place(p.getId(), p.getName().toString(), p.getLatLng().latitude, p.getLatLng().longitude);
//                    HttpHandler httpHandler = new HttpHandler(this);
//                    httpHandler.newHangout(this, place);
                }
                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tabbed_home, menu);

//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        //make R.id.action_search visible in menu. Default is set to gone.
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.setOnQueryTextListener(this);

        MenuItem startHangout = menu.findItem(R.id.action_hangout);
        startHangout.setOnMenuItemClickListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_logout) {
            SessionManager sessionManager = SessionManager.getInstance(this);
            sessionManager.logoutUser();
        }

        if (id == R.id.activity_my_profile) {
            Intent intent = new Intent(this, MyProfile.class);
            startActivity(intent);
        }

        if (id == R.id.app_share_to_facebook) {
            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.oerdev.traveller"))
                    .build();
            FacebookSdk.sdkInitialize(this);

            ShareApi.share(content, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onSuccess(Sharer.Result result) {
                    Logger.d(TAG, "facebook success: " + result);
                }

                @Override
                public void onCancel() {
                    Logger.d(TAG, "facebook post cancel ");
                }

                @Override
                public void onError(FacebookException error) {
                    Logger.d(TAG, "facebook post error: " + error.getMessage());
                    error.printStackTrace();
                }
            });
            //           MessengerUtils.shareToMessenger(this, REQUEST_CODE_SHARE_TO_MESSAGE, content);
        }

        if (id == R.id.action_map) {
            Logger.d(TAG, "map .... ");
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("from", HomeTabbedActivity.class.getSimpleName());
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onPlaceClick(String placeId, String fragment, int tappedItem) {
//        Intent intent = new Intent(this, DetailedActivity.class);
//        intent.putExtra(AppConfigs.INTENT_EXTRA_PLACE_ID, placeId);
//        intent.putExtra(AppConfigs.INTENT_EXTRA_FROM_FRAGMENT, fragment);
//        intent.putExtra(AppConfigs.INTENT_EXTRA_TAPPED_ITEM, tappedItem);
//        startActivity(intent);
//    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Logger.d(TAG, "onQueryTextSubmit");
        if (Geocoder.isPresent()) {
            try {
                Geocoder gc = new Geocoder(this);
                List<Address> addresses = gc.getFromLocationName(query, 5);
                Address address = addresses.get(0);
                Logger.d(TAG, address.getFeatureName());
                Logger.d(TAG, address.getLongitude() + "");
                Logger.d(TAG, address.getLatitude() + "");

                Logger.d(TAG, addresses + "");
                //        Logger.d(TAG, addresses);
            } catch (IOException e) {
                // handle the exception
            }
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Logger.d(TAG, "onQueryTextChange");
        //HangoutFragment.mAdapter.
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
        return false;
    }

    @Override
    public void onLocationUpdate(String place) {
        SessionManager sessionManager = SessionManager.getInstance(this);
        sessionManager.savePastLocation(place);
        setTitle(place);
    }

    @Override
    public void onPlaceClick(com.oerdev.traveller.ItemsModel.Place place, int tappedItem) {
        Intent intent = new Intent(this, DetailedActivity.class);
        intent.putExtra("place", place);
        intent.putExtra(AppConfigs.INTENT_EXTRA_TAPPED_ITEM, tappedItem);
        startActivity(intent);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_hangout:
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;
            default:
        }
        return false;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    suggestFragment = new SuggestFragment();
                    return suggestFragment;
                case 1:
                    return new HangoutFragment();
                case 2:
                    return new MyPlanFragment();
                case 3:
                    return new ContactsFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SUGGEST";
                case 1:
                    return "HANGOUT";
                case 2:
                    return "MY PLACES";
                case 3:
                    return "CONTACTS";
//                    String name = SessionManager.getInstance(sContext).getProfileName();
//                    if (name == null) {
//                        return "USER";
//                    }
//                    return "USER";
            }
            return null;
        }
    }
}
