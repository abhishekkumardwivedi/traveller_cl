package com.oerdev.traveller.Login;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.oerdev.traveller.Home.HomeTabbedActivity;
import com.oerdev.traveller.Network.HttpHandler;
import com.oerdev.traveller.R;
import com.oerdev.traveller.app.AppConfigs;
import com.oerdev.traveller.app.Logger;
import com.oerdev.traveller.app.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by abhishek on 24/8/16.
 */
public class LoginFragment extends Fragment implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String TAG = LoginFragment.class.getSimpleName();
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final int RC_SIGN_IN = 0;
    private static final int PROFILE_PIC_SIZE = 110;
    private static android.app.Application mApplication;
    private CallbackManager callbackManager;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static GoogleApiClient mGoogleApiClient;
    View rootView;
    private boolean mSignInClicked;
    private boolean mIntentInProgress;
    private ConnectionResult mConnectionResult;

    public LoginFragment() {}

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static LoginFragment newInstance(int sectionNumber, android.app.Application application) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        Logger.d(TAG, "setction number(Login) = " + ARG_SECTION_NUMBER);
        mApplication = application;
        return fragment;
    }

    public static void saveFile(Context context, Bitmap b, String picName) {
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(picName, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Logger.d(TAG, "file not found");
            e.printStackTrace();
        } catch (IOException e) {
            Logger.d(TAG, "io exception");
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_login, container, false);

        ImageView imgView = (ImageView) rootView.findViewById(R.id.section_img);
        imgView.setImageResource(LoginActivity.imgMap.get(getArguments().getInt(ARG_SECTION_NUMBER)));

        facebookCreateView(rootView, container, savedInstanceState);

        Logger.d(TAG, "section: " + getArguments().getInt(ARG_SECTION_NUMBER));
        return rootView;
    }


    private void facebookCreateView(
            final View rootView,
            ViewGroup container,
            Bundle savedInstanceState) {

        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) rootView.findViewById(R.id.facebook_login_button);
        loginButton.setReadPermissions("email");
        // If using in a fragment
        loginButton.setFragment(this);
        // Other app specific specialization

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            ProgressDialog progressDialog;
            @Override
            public void onSuccess(LoginResult loginResult) {
                Logger.d(TAG, "facebook login success !!");
                System.out.println("onSuccess");
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Procesando datos...");
                progressDialog.show();
                String accessToken = loginResult.getAccessToken().getToken();
                Log.i("accessToken", accessToken);

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i("LoginActivity", response.toString());
                        progressDialog.cancel();
                        // Get facebook data from login
                        Bundle bFacebookData = getFacebookData(object);
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location"); // Parámetros que pedimos a facebook
                request.setParameters(parameters);
                request.executeAsync();

                GraphRequest request1 = GraphRequest.newMyFriendsRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(JSONArray objects, GraphResponse response) {
                        int i = objects.length();
                        Logger.d(TAG, "Total Friends:" + i);
                        Logger.d(TAG, "Friends: " + objects);
                    }
                });

                request1.executeAsync();
            }

            private Bundle getFacebookData(JSONObject object) {

                try {
                    Bundle bundle = new Bundle();
                    String id = object.getString("id");

                    try {
                        URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                        Log.i("profile_pic", profile_pic + "");
                        bundle.putString("profile_pic", profile_pic.toString());

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        return null;
                    }

                    bundle.putString("idFacebook", id);
                    if (object.has("first_name"))
                        bundle.putString("first_name", object.getString("first_name"));
                    if (object.has("last_name"))
                        bundle.putString("last_name", object.getString("last_name"));
                    if (object.has("email"))
                        bundle.putString("email", object.getString("email"));
                    if (object.has("gender"))
                        bundle.putString("gender", object.getString("gender"));
                    if (object.has("birthday"))
                        bundle.putString("birthday", object.getString("birthday"));
                    if (object.has("location"))
                        bundle.putString("location", object.getJSONObject("location").getString("name"));

                    String idFacebook = object.getString("id");
                    String name = object.getString("first_name") + " " + object.getString("last_name");
                    String email = object.getString("email");
                    String gender = object.getString("gender");

                    HttpHandler httpHandler = new HttpHandler(getContext());
                    httpHandler.registerUser(AppConfigs.FACEBOOK, idFacebook, email, name, gender);

                    Logger.d(TAG, "Facebook data: " + object);

                    return bundle;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            }

                @Override
            public void onCancel() {
                Logger.d(TAG, "facebook login cancelled !!");
            }

            @Override
            public void onError(FacebookException exception) {
                Logger.d(TAG, "facebook login error: " + exception.getMessage());
                Toast.makeText(getContext(), "Login failed!! Check internet connectivity.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SignInButton btnGoogleSignIn = (SignInButton) rootView.findViewById(R.id.btn_google_sign_in);
        //       Button btnFacebookSignIn = (Button) rootView.findViewById(R.id.btn_facebook_sign_in);
        btnGoogleSignIn.setOnClickListener(this);
        Button btnTest = (Button) rootView.findViewById(R.id.btn_test_register);
        btnTest.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.d(TAG, "onResume()");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
            Logger.d(TAG, "mGoogleApiClient.disconnect()");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(this.getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

        FacebookSdk.sdkInitialize(getContext());
        AppEventsLogger.activateApp(mApplication);
    }

    /**
     * Sign-out from google
     */
    public void signOutFromGplus() {

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                    }
                });
    }

    /**
     * Revoking access from google
     */
    private void revokeGplusAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_google_sign_in:
                GoogleSignIn();
                break;
            case R.id.btn_test_register:
                testRegister();
                break;
        }
    }

    public void testRegister() {
        EditText etPhone = (EditText) rootView.findViewById(R.id.et_phone);
        EditText etName = (EditText) rootView.findViewById(R.id.et_name);
        EditText etEmail = (EditText) rootView.findViewById(R.id.et_email);
        String phone = String.valueOf(etPhone.getText());
        String name = String.valueOf(etName.getText());
        String email = String.valueOf(etEmail.getText());

        if (phone == null || email == null || name == null) {
            Snackbar.make(rootView, "All fields requred!!", Snackbar.LENGTH_LONG).show();
            return;
        }

        HttpHandler httpHandler = new HttpHandler(rootView.getContext());
        httpHandler.registerUser(name, email, phone);
    }

    private void GoogleSignIn() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    private void resolveSignInError() {
        if (mConnectionResult == null) {
            mConnectionResult = mGoogleApiClient.getConnectionResult(Plus.API);
            Logger.d(TAG, "mConnectionResult = " + mConnectionResult);
        }
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this.getActivity(), RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Logger.d(TAG, "Google: onConnected()");
        mSignInClicked = false;
        Toast.makeText(this.getActivity(), "User is connected!", Toast.LENGTH_LONG).show();

        // Get user's information
        Logger.d(TAG, "profile permission = " + checkProfilePermission());
        if (checkProfilePermission()) {
            Logger.d(TAG, "getting profile info : .....");
            if (getProfileInformation()) {
                Intent intent = new Intent(rootView.getContext(), HomeTabbedActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        } else {
            Logger.d(TAG, "profile permission denied!! ");
        }

        // Update the UI after signin
        //    proceedOnConnected(true);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Logger.d(TAG, "Google: onConnectionSuspended()");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Logger.d(TAG, "Google: onConnectionFailed()");
        if (!result.hasResolution()) {
//                GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this.getActivity(),
//                        0).show();
            Logger.d(TAG, "Error:" + result.getErrorMessage());
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    public boolean checkProfilePermission() {
        if (ContextCompat.checkSelfPermission(rootView.getContext(),
                Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.activity, Manifest.permission.GET_ACCOUNTS)) {
            } else {
                ActivityCompat.requestPermissions(LoginActivity.activity, new String[]{Manifest.permission.GET_ACCOUNTS}, 23);
            }

            Logger.d(TAG, "permission: " + ContextCompat.checkSelfPermission(rootView.getContext(),
                    Manifest.permission.GET_ACCOUNTS));
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Logger.d(TAG, "1(): " + requestCode);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Logger.d(TAG, "2()");

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(rootView.getContext(),
                            Manifest.permission.GET_ACCOUNTS)
                            == PackageManager.PERMISSION_GRANTED) {

                        Logger.d(TAG, "onRequestPermissionsResult()");
                        getProfileInformation();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(rootView.getContext(), "permission denied", Toast.LENGTH_LONG).show();
                    Logger.d(TAG, "permission denied");
                }
                return;
            }

        }
    }

    /**
     * Fetching user's information name, email, profile pic
     */
    private boolean getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String personId = currentPerson.getId();
                String personLocation = currentPerson.getCurrentLocation();
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return false;
                }
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                Logger.d(TAG, "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + personPhotoUrl + ", location: "
                        + personLocation + ", id: " + personId);

                SessionManager session = SessionManager.getInstance(rootView.getContext());
                session.saveLoginSession(null, personName, email, null);

                // by default the profile url gives 50x50 px image only
                // we can replace the value with whatever dimension we want by
                // replacing sz=X
                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;

                new LoadProfileImage().execute(personPhotoUrl);

                return (personName != null && email != null);

            } else {
                Toast.makeText(rootView.getContext(), "personal info is null", Toast.LENGTH_LONG).show();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Background Async task to load user profile picture from url
     */
    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {

        public LoadProfileImage() {
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Logger.d("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap bitmap) {
            SessionManager.getInstance(rootView.getContext()).saveImage(rootView.getContext(),
                    bitmap, "profile", "png");
//            saveFile(rootView.getContext(), result, "profile.png");
        }
    }
}
