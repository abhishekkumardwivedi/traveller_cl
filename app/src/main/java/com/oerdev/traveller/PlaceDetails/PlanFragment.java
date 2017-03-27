package com.oerdev.traveller.PlaceDetails;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;

import com.bumptech.glide.Glide;
import com.oerdev.traveller.ItemsModel.Friend;
import com.oerdev.traveller.ItemsModel.Place;
import com.oerdev.traveller.ItemsModel.Plan;
import com.oerdev.traveller.Network.HttpHandler;
import com.oerdev.traveller.R;
import com.oerdev.traveller.app.Logger;
import com.oerdev.traveller.myPlan.MyPlanFragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

/**
 * Created by abhishek on 16/9/16.
 */

public class PlanFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = PlanFragment.class.getSimpleName();
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int GET_CONTACT_REQUEST = 2;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 3;
    private static TextView dv;
    private static TextView rv;
    private static TextView n;
    private static ImageView i;
    private static Place mPlace;
    private static View view;
    public Plan plan;
    private ViewStub mViewStub;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPlace = ((DetailedActivity) getActivity()).getPlace();
        Logger.d(TAG, "Place selected:" + mPlace.name);
        view = inflater.inflate(R.layout.fragment_details_activity_plan_content, container, false);
        if (MyPlanFragment.PLACE_PLAN_MAP.get(mPlace.id) != null) {
            mViewStub = (ViewStub) view.findViewById(R.id.id_plan_view);
            mViewStub.setInflatedId(R.id.inflated_id_plan_view);
            mViewStub.setLayoutResource(R.layout.plan_content_views);
        } else {
            mViewStub = (ViewStub) view.findViewById(R.id.id_add_to_plan);
            mViewStub.setInflatedId(R.id.inflated_id_add_to_plan);
            mViewStub.setLayoutResource(R.layout.add_to_plan);
        }
        mViewStub.inflate();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mViewStub.getInflatedId() == R.id.inflated_id_plan_view) {
            plan = MyPlanFragment.PLACE_PLAN_MAP.get(mPlace.id);
            n = (TextView) view.findViewById(R.id.person_name);
            i = (ImageView) view.findViewById(R.id.friend_img);
            TextView f = (TextView) view.findViewById(R.id.add_friend);
            f.setOnClickListener(this);
            TextView d = (TextView) view.findViewById(R.id.date_label);
            d.setOnClickListener(this);
            dv = (TextView) view.findViewById(R.id.date_value);
            dv.setOnClickListener(this);
            TextView r = (TextView) view.findViewById(R.id.route_label);
            r.setOnClickListener(this);
            rv = (TextView) view.findViewById(R.id.place_value);
            rv.setOnClickListener(this);

            String date = MyPlanFragment.PLACE_PLAN_MAP.get(mPlace.id).getDateDdmmyyyy();
            if (date != null) {
                dv.setText(date);
            } else {
                dv.setText("add date ..");
            }

            ArrayList<Plan.Route> routes = MyPlanFragment.PLACE_PLAN_MAP.get(mPlace.id).getRoutes();
            if (routes != null) {
                rv.setText(routes.get(0).address);
            } else {
                rv.setText("add location ..");
            }

            FriendsArrayAdapter friendsAdapter = new FriendsArrayAdapter(getContext(), R.layout.planfragment_friends_single_row);
            ListView friendsListView = (ListView) view.findViewById(R.id.contact_list_view);
            friendsListView.setAdapter(friendsAdapter);


//            HttpHandler httpHandler = new HttpHandler(getContext());
//            httpHandler.getMyPlan();
        }

        if (mViewStub.getInflatedId() == R.id.inflated_id_add_to_plan) {
            View a = view.findViewById(R.id.textview_add_to_plan);
            a.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        HttpHandler httpHandler = new HttpHandler(getContext());
        switch (view.getId()) {
            case R.id.add_friend:

                startActivityForResult(new Intent(Intent.ACTION_PICK,
                        ContactsContract.Contacts.CONTENT_URI), GET_CONTACT_REQUEST);
//                Intent intentContact = new Intent(Intent.ACTION_GET_CONTENT);
//                intentContact.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
//                startActivityForResult(intentContact, GET_CONTACT_REQUEST);
                break;
            case R.id.date_label:
            case R.id.date_value:
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "Plan Date");
                break;

            case R.id.route_label:
            case R.id.place_value:
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    Logger.d("PlacesAPI Demo", "GooglePlayServicesRepairableException thrown");
                } catch (GooglePlayServicesNotAvailableException e) {
                    Logger.d("PlacesAPI Demo", "GooglePlayServicesNotAvailableException thrown");
                }
                break;

            case R.id.textview_add_to_plan:
                Plan plan = new Plan(mPlace);
                MyPlanFragment.PLANS_ITEMS.add(MyPlanFragment.PLANS_ITEMS.size(), plan);
                httpHandler.updateMyPlan();
                break;
            default:
                Logger.d(TAG, "Unhandled onClick event!!!");
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            Logger.d(TAG, "Result code = " + resultCode + " for request code " + requestCode);
            return;
        }
        switch (requestCode) {
            case PLACE_PICKER_REQUEST:
                com.google.android.gms.location.places.Place place = PlacePicker.getPlace(getContext(), data);
                rv.setText(place.getAddress());
                MyPlanFragment.PLACE_PLAN_MAP.get(mPlace.id)
                        .addRoute(Double.toString(place.getLatLng().latitude),
                                Double.toString(place.getLatLng().longitude),
                                place.getAddress().toString(),
                                place.getId());
                Logger.d(TAG, place.getAddress().toString());
                HttpHandler httpHandler = new HttpHandler(getContext());
                httpHandler.updateMyPlan();
                break;
            case GET_CONTACT_REQUEST:
                Uri uriContact = data.getData();
//                Cursor cursor = getContext().getContentResolver().query(uriContact, null, null, null, null);
//                cursor.moveToFirst();
//                String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                Logger.d(TAG, "" + number);
//
//                Logger.d(TAG, "Response: " + data.toString());
                if (getContactPermission(uriContact)) {
                    Friend friend = getPhonebookContactInfo(uriContact);
                    MyPlanFragment.PLACE_PLAN_MAP.get(mPlace.id)
                            .mFriendsList.add(friend);
                } else {
                    Logger.d(TAG, "contact permission denied!!");
                }
                break;
        }
    }

    private Friend getPhonebookContactInfo(Uri uriContact) {

        Friend friend;
        String contactID;

        // contactId
        Cursor cursorID = getContext().getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst()) {

            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
            friend = new Friend();
            friend.contactId = contactID;
            Logger.d(TAG, "id:" + friend.contactId);

        } else {
            return null;
        }
        cursorID.close();

        //Number
        Cursor cursorPhone = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                new String[]{contactID},
                null);

        if (cursorPhone.moveToFirst()) {
            String contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            friend.contactNumber = contactNumber;
            Logger.d(TAG, "number:" + friend.contactNumber);

        }
        cursorPhone.close();

        //Name
        Cursor cursor = getContext().getContentResolver().query(uriContact, null, null, null, null);
        if (cursor.moveToFirst()) {
            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.
            String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            friend.name = contactName;
            Logger.d(TAG, "name:" + friend.name);

        }
        cursor.close();

        //Photo
        try {
            InputStream inputStream =
                    ContactsContract.Contacts.openContactPhotoInputStream(getContext().getContentResolver(),
                            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactID)));

            if (inputStream != null) {
                Bitmap photo = BitmapFactory.decodeStream(inputStream);
                friend.photo = photo;
            }
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return friend;
    }

    private boolean getContactPermission(final Uri uriContact) {
        int hasWriteContactsPermission = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hasWriteContactsPermission = getContext().checkSelfPermission(Manifest.permission.READ_CONTACTS);
        }
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                showMessageOKCancel("Allow read access of Contact",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                                        REQUEST_CODE_ASK_PERMISSIONS);
                                Friend friend = getPhonebookContactInfo(uriContact);
                                Logger.d(TAG, friend.contactId);
                                Logger.d(TAG, friend.contactNumber);
                                Logger.d(TAG, friend.name);
                            }
                        });
                return false;
            }
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return false;
        }
        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
//        private int mPosition;
//
//        public void setPosition(int position) {
//            mPosition = position;
//        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @TargetApi(Build.VERSION_CODES.N)
        public void onDateSet(DatePicker view, int year, int month, int day) {

            final Calendar c = Calendar.getInstance();
            c.set(year, month, day);
            c.getTimeInMillis();
            MyPlanFragment.PLACE_PLAN_MAP.get(((DetailedActivity) getActivity()).getPlace().id)
                    .setDate(Long.toString(c.getTimeInMillis()));
            String date = MyPlanFragment.PLACE_PLAN_MAP.get(mPlace.id).getDateDdmmyyyy();
            dv.setText(date);
            HttpHandler httpHandler = new HttpHandler(getContext());
            httpHandler.updateMyPlan();
        }
    }

    public class FriendsArrayAdapter extends ArrayAdapter<Friend> {

        public FriendsArrayAdapter(Context context, int resource) {
            super(context, resource);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Friend friend = plan.mFriendsList.get(position);//getItem(position);
            TextView titleMsg = null;
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.planfragment_friends_single_row, parent, false);
            }
            // Lookup view for data population
            TextView tvName = (TextView) convertView.findViewById(R.id.friend_name);
            ImageView ivFriend = (ImageView) convertView.findViewById(R.id.friend_img);

            tvName.setText(friend.name);
            Glide.with(getContext()).load(friend.getThumbnailImageUrl()).into(ivFriend);
            return convertView;
        }
    }
}
