package com.oerdev.traveller.User;

import com.bumptech.glide.Glide;
import com.oerdev.traveller.R;
import com.oerdev.traveller.app.SessionManager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class MyProfile extends AppCompatActivity implements View.OnClickListener {

    static boolean val = false;

    private static HashMap<String, Integer> tags = new HashMap<>(100);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        ImageView image = (ImageView) findViewById(R.id.profile_pic);
        TextView name = (TextView) findViewById(R.id.profile_name);
        TextView email = (TextView) findViewById(R.id.profile_email);

        SessionManager sessionManager = SessionManager.getInstance(this);
        name.setText(sessionManager.getProfileName());
        email.setText(sessionManager.getProfileEmail());
        Glide.with(this).load(sessionManager.getProfileImageUrl()).into(image);
        View view = findViewById(R.id.heritage);
        view.setOnClickListener(this);
    }


    private void createTags() {
        tags.put("religious", 1);
        tags.put("family", 1);
        tags.put("trekking", 1);
        tags.put("heritage", 1);
        tags.put("beaches", 1);
    }

    @Override
    public void onClick(View view) {

        if(val) {
            view.setBackgroundColor(getResources().getColor(R.color.light_grey));
            val = false;
        } else {
            view.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            val = true;
        }
    }
}
