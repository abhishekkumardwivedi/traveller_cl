package com.oerdev.traveller.PlaceDetails;

import com.bumptech.glide.Glide;
import com.oerdev.traveller.ItemsModel.Place;
import com.oerdev.traveller.R;
import com.oerdev.traveller.app.AppConfigs;
import com.oerdev.traveller.myPlan.MyPlanFragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class DetailedActivity extends AppCompatActivity implements MyPlanFragment.MyPlanFIListener {
    private static final String TAG = DetailedActivity.class.getSimpleName();

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private Place mPlace;
    private int tappedItem;
    private int imageIndex;

    private ImageView titleImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout collapsableView = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
//        titleText.setTitle("Change It");


        Bundle bundle = getIntent().getExtras();
        mPlace = bundle.getParcelable("place");
        tappedItem = bundle.getInt(AppConfigs.INTENT_EXTRA_TAPPED_ITEM);

        titleImage = (ImageView) findViewById(R.id.backdrop);

        ListView imageListView = (ListView) findViewById(R.id.image_items);

        if (mPlace.primaryImageUrl != null) {
            Glide.with(this).load(mPlace.primaryImageUrl).into(titleImage);

        } else if (mPlace.photoReferenceList != null && mPlace.photoReferenceList.size() > 0) {
            String url = "https://maps.googleapis.com/maps/api/place/photo?"
                    + "maxwidth=300"
                    + "&photoreference=" + mPlace.photoReferenceList.get(0)
                    + "&key=" + getApplicationContext().getResources().getString(R.string.google_api_key);
            Glide.with(getApplicationContext()).load(url).into(titleImage);

            imageIndex = 0;
            titleImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ++imageIndex;
                    imageIndex = imageIndex == mPlace.photoReferenceList.size() ? 0 : imageIndex;
                    String url = "https://maps.googleapis.com/maps/api/place/photo?"
                            + "maxwidth=300"
                            + "&photoreference=" + mPlace.photoReferenceList.get(imageIndex)
                            + "&key=" + getApplicationContext().getResources().getString(R.string.google_api_key);
                    Glide.with(getApplicationContext()).load(url).into(titleImage);
                }
            });
//            ImagesAdapter imagesAdapter = new ImagesAdapter(this, mPlace.photoReferenceList);
//            imageListView.setVisibility(View.VISIBLE);
//            imageListView.setAdapter(imagesAdapter);
        }
        collapsableView.setTitle(mPlace.name);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.detail_tabs);
//        tabLayout.addTab(tabLayout.newTab().setText("ABOUT PLACE"));
//        tabLayout.addTab(tabLayout.newTab().setText("MY PLAN"));
        tabLayout.setupWithViewPager(viewPager);
    }

    public Place getPlace() {
        return mPlace;
    }

    @Override
    public void onPlaceClick(Place place, int tappedItem) {

    }

    public class ImagesAdapter extends ArrayAdapter<String> {
        private ArrayList<String> mImageList = null;

        public ImagesAdapter(Context context, ArrayList<String> imageList) {
            super(context, 0, imageList);
            mImageList = imageList;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            String image = getItem(position);
            TextView titleMsg = null;
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.image_single_item, parent, false);
                ImageView imageItem = (ImageView) convertView.findViewById(R.id.place_thumbnail);
                final String url = "https://maps.googleapis.com/maps/api/place/photo?"
                        + "maxwidth=200"
                        + "&photoreference=" + mImageList.get(position)
                        + "&key=" + getContext().getResources().getString(R.string.google_api_key);
                imageItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Glide.with(getContext()).load(url).into(titleImage);
                    }
                });
                Glide.with(getContext()).load(url).into(imageItem);
            }
            return convertView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new DetailsFragment();
                case 1:
                    return new SnFragment();
                case 2:
                    return new PlanFragment();
                case 3:
                    return new BookingsFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "ABOUT";
                case 1:
                    return "COMMENTS";
                case 2:
                    return "MY PLAN";
                case 3:
                    return "BOOKING";
            }
            return null;
        }
    }
}
