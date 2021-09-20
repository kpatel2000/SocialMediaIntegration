package com.example.tsfsocialmediaintegration;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;

import java.util.Collections;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "GMail";
    private CallbackManager callbackManager;
    String name, profile, email;
    String gm_profile;

    GoogleSignInClient mGoogleSignInClient;
    SharedPreferences sharedPreferences;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String FbProfileUrl = "PFbProfileUrl", FbName = "PFb_Name", FbEmail = "PFb_Email";
    public static final String FB_LOGIN = "fb_Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //For Google
        Button gm_login = findViewById(R.id.google_login_btn);
        GoogleLoginCheck();
        gm_login.setOnClickListener(view -> googleSignIn());

        //For Facebook
        Button fb_login = findViewById(R.id.facebook_login_btn);
        callbackManager = CallbackManager.Factory.create();
        // Callback registration
        fb_login.setOnClickListener(view -> {
            LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Collections.singletonList("email"));
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), (object, response) -> {
                        try {
                            name = object.getString("name");
                            profile = object.getJSONObject("picture").getJSONObject("data").getString("url");

                            if (object.has("email")) {
                                email = object.getString("email");
                            } else {
                                email = " ";
                            }

                            SharedPreferences settings = getSharedPreferences(FB_LOGIN, 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("fb_logged", "fb_logged");
                            editor.apply();
                            sendFbData();
                            Toast.makeText(MainActivity.this, "Login successfully :)", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                            intent.putExtra("MediaType", "Facebook");
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    });
                    Bundle bundle = new Bundle();
                    bundle.putString("fields", "picture.type(large), name, email");
                    graphRequest.setParameters(bundle);
                    graphRequest.executeAsync();
                }

                @Override
                public void onCancel() {
                    Toast.makeText(MainActivity.this, "Login cancelled :(", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(FacebookException exception) {
                    Toast.makeText(MainActivity.this, "Login error :(", Toast.LENGTH_SHORT).show();
                }
            });
        });

    }

    private void GoogleLoginCheck() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("MediaType", "Google");
            name = account.getDisplayName();
            email = account.getEmail();
            if (account.getPhotoUrl() != null) {
                gm_profile = account.getPhotoUrl().toString();
            } else {
                gm_profile = " ";
            }
            intent.putExtra("name", name);
            intent.putExtra("email", email);
            intent.putExtra("profile", gm_profile);
            Toast.makeText(MainActivity.this, "Already LoggedIn :)", Toast.LENGTH_SHORT).show();
            startActivity(intent);
            finish();
        }
    }

    private void sendFbData() {
        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FbName, name);
        editor.putString(FbEmail, email);
        editor.putString(FbProfileUrl, profile);
        editor.apply();
    }

    private void googleSignIn() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signIn();
    }

    @SuppressWarnings("deprecation")
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("MediaType", "Google");
            name = account.getDisplayName();
            email = account.getEmail();
            if (account.getPhotoUrl() != null) {
                gm_profile = account.getPhotoUrl().toString();
            } else {
                gm_profile = " ";
            }
            Log.d(TAG, "handleSignInResult: " + gm_profile);
            intent.putExtra("name", name);
            intent.putExtra("email", email);
            intent.putExtra("profile", gm_profile);
            Toast.makeText(MainActivity.this, "LogIn Successful :)", Toast.LENGTH_SHORT).show();
            startActivity(intent);
            finish();
        } catch (ApiException e) {
            Toast.makeText(this, "LoggedIn Failed :(", Toast.LENGTH_SHORT).show();
        }
    }
}