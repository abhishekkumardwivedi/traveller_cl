package com.oerdev.traveller.Contacts;


import com.bumptech.glide.Glide;
import com.oerdev.traveller.ItemsModel.Friend;
import com.oerdev.traveller.R;
import com.oerdev.traveller.app.Logger;

import android.Manifest;
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
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * Created by abhishek on 14/9/16.
 */

public class ContactsFragment extends Fragment implements View.OnClickListener {

    public static final ArrayList<Friend> FRIENDS_LIST = new ArrayList<Friend>();
    public static final ArrayList<Friend> INCOMING_FRIEND_REQUEST_LIST = new ArrayList<Friend>();
    public static final ArrayList<Friend> SENT_FRIEND_REQUEST_LIST = new ArrayList<Friend>();
    private static final String TAG = ContactsFragment.class.getSimpleName();
    private static final int GET_CONTACT_REQUEST = 1;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 2;
    private static final int FRIENDS = 0;
    private static final int INCOMING_PENDING = 1;
    private static final int SENT_PENDING = 2;
    ListView incomingContactListener;
    ListView sentContactListener;
    //    TextView locationView;
//    String[] values;
    private ProfileFIListener mListener;
    private ListView contactListener;


    public ContactsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        TextView textView = (TextView) view.findViewById(R.id.add_friend_text_view);
        textView.setOnClickListener(this);

        FriendsAdapter friendsAdapter = new FriendsAdapter(getContext(), FRIENDS_LIST, FRIENDS);
        contactListener = (ListView) view.findViewById(R.id.contact_list_view);
        contactListener.setAdapter(friendsAdapter);

        FriendsAdapter incomingFriendsAdapter = new FriendsAdapter(getContext(), INCOMING_FRIEND_REQUEST_LIST, INCOMING_PENDING);
        incomingContactListener = (ListView) view.findViewById(R.id.incoming_contact_list_view);
        incomingContactListener.setAdapter(incomingFriendsAdapter);

        FriendsAdapter pendingFriendsAdapter = new FriendsAdapter(getContext(), SENT_FRIEND_REQUEST_LIST, SENT_PENDING);
        sentContactListener = (ListView) view.findViewById(R.id.sent_contact_list_view);
        sentContactListener.setAdapter(pendingFriendsAdapter);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProfileFIListener) {
            mListener = (ProfileFIListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ProfileFIListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_friend_text_view:
                startActivityForResult(new Intent(Intent.ACTION_PICK,
                        ContactsContract.Contacts.CONTENT_URI), GET_CONTACT_REQUEST);
                break;
            default:
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            Logger.d(TAG, "Result code = " + resultCode + " for request code " + requestCode);
            return;
        }
        switch (requestCode) {
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
                    for (int i = 0; i < FRIENDS_LIST.size(); i++) {
                        if (FRIENDS_LIST.get(i).contactNumber.equals(friend.contactNumber)) {
                            Toast.makeText(getContext(),
                                    FRIENDS_LIST.get(i).contactNumber
                                            + " is already in friends list with the name "
                                            + FRIENDS_LIST.get(i).name, Toast.LENGTH_LONG);
                        }
                    }
                    SENT_FRIEND_REQUEST_LIST.add(friend);
                    contactListener.deferNotifyDataSetChanged();
                    sentContactListener.deferNotifyDataSetChanged();
                    incomingContactListener.deferNotifyDataSetChanged();
                    contactListener.invalidateViews();

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

    public interface ProfileFIListener {
        void onLocationUpdate(String place);
    }

    public class FriendsAdapter extends ArrayAdapter<Friend> {
        private int status;
        private ArrayList<Friend> mFriends;

        public FriendsAdapter(Context context, ArrayList<Friend> friends, int status) {
            super(context, 0, friends);
            this.status = status;
            mFriends = friends;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Friend friend = getItem(position);
            TextView titleMsg = null;
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                switch (status) {
                    case FRIENDS:
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.friends_single_row, parent, false);
                        break;
                    case INCOMING_PENDING:
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.friends_single_row, parent, false);
                        titleMsg = (TextView) convertView.findViewById(R.id.title_message);
                        titleMsg.setText("[Incoming request]");
                        titleMsg.setTextColor(getResources().getColor(R.color.blue, null));
                        Button approve = (Button) convertView.findViewById(R.id.btn_action);
                        approve.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //approve
                            }
                        });
                        break;
                    case SENT_PENDING:
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.friends_single_row, parent, false);
                        titleMsg = (TextView) convertView.findViewById(R.id.title_message);
                        titleMsg.setText("[Pending for approval]");
                        titleMsg.setTextColor(getResources().getColor(R.color.blue, null));

                        Button addFriend = (Button) convertView.findViewById(R.id.btn_action);
                        addFriend.setVisibility(View.VISIBLE);
                        addFriend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //send sms
                                String number =  mFriends.get(position).contactNumber;
                                if(number != null) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + number));
                                    intent.putExtra("sms_body", "Install https://play.google.com/store/apps/details?id=com.oerdev.traveller");
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getContext(), "Contact number not found!! \n Add contact number and send request again!!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        break;
                }
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
