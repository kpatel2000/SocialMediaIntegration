package com.example.tsfsocialmediaintegration;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    CircleImageView user_profile;
    TextView user_name, user_email;
    Button fb_logout, gm_logout;

    String Pfb_name, Pfb_profileUrl, Pfb_email;

    GoogleSignInClient mGoogleSignInClient;

    SharedPreferences sharedPreferences;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String FbProfileUrl = "PFbProfileUrl", FbName = "PFb_Name", FbEmail = "PFb_Email";
    public static final String FB_LOGIN = "fb_login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user_profile = findViewById(R.id.profile_image);
        user_name = findViewById(R.id.name);
        user_email = findViewById(R.id.email);
        fb_logout = findViewById(R.id.facebook_logout_btn);
        gm_logout = findViewById(R.id.google_logout_btn);

        Intent intent = getIntent();
        String type = intent.getStringExtra("MediaType");
        if (type.equals("Google")) {
            getGoogleData(intent);
            gm_logout.setVisibility(View.VISIBLE);
            fb_logout.setVisibility(View.GONE);
        } else {
            sharedPreferences = getSharedPreferences("fbLogin", 0);
            getFacebookData();
            gm_logout.setVisibility(View.GONE);
            fb_logout.setVisibility(View.VISIBLE);
        }


        gm_logout.setOnClickListener(view -> {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(ProfileActivity.this, gso);
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(ProfileActivity.this, task -> {
                        Intent intent1 = new Intent(ProfileActivity.this, MainActivity.class);
                        Toast.makeText(ProfileActivity.this, "LogOut Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(intent1);
                        finish();
                    });
        });

        fb_logout.setOnClickListener(view -> {
            LoginManager.getInstance().logOut();
            SharedPreferences settings = getSharedPreferences(FB_LOGIN, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.remove("fb_logged");
            editor.clear();
            editor.apply();

            SharedPreferences.Editor editor1 = sharedPreferences.edit();
            editor1.clear();
            editor1.apply();

            Toast.makeText(ProfileActivity.this, "Facebook Logged out successfully!", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent1);
            finish();

        });

    }

    private void getFacebookData() {
        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Pfb_name = sharedPreferences.getString(FbName, "");
        Pfb_email = sharedPreferences.getString(FbEmail, "");
        Pfb_profileUrl = sharedPreferences.getString(FbProfileUrl, "");

        Glide.with(this).load(Pfb_profileUrl).into(user_profile);
        user_name.setText(Pfb_name);
        user_email.setText(Pfb_email);
    }

    private void getGoogleData(Intent intent) {
        Pfb_name = intent.getStringExtra("name");
        Pfb_email = intent.getStringExtra("email");
        Pfb_profileUrl = intent.getStringExtra("profile");

        Glide.with(this).load(Pfb_profileUrl)
                .placeholder(R.drawable.profile).into(user_profile);
        user_name.setText(Pfb_name);
        user_email.setText(Pfb_email);
    }

    AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if (currentAccessToken == null) {
                user_profile.setImageResource(0);
                user_name.setText(" ");
                user_email.setText(" ");
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }
}