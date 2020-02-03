package com.example.topidana.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.topidana.R;
import com.example.topidana.adapters.SectionsPageAdapter;
import com.example.topidana.app.AppController;
import com.example.topidana.app.Server;
import com.example.topidana.fargments.DaftarFragment;
import com.example.topidana.fargments.LoginFragment;
import com.example.topidana.services.SharedPrefManager;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    ProgressDialog pDialog;
    public static SharedPrefManager sharedPrefManager;
    private ViewPager mViewPager;

    private static final String TAG = "LoginActivity";
    SignInButton sign_in_google_button;
    LoginButton sign_in_fb_button;

    AccessToken accessToken = AccessToken.getCurrentAccessToken();
    boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

    CallbackManager mcallbackManager;
    public static GoogleSignInClient mGoogleSignInClient;

    public static String status_user, profile_pic;

    private static final String TAG_PESAN = "status";
    private static final String TAG_STATUS_USER = "STATUS_USER";

    private int RC_SIGN_IN = 21299;

    String pesan;
    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPrefManager = new SharedPrefManager(this);

        status_user = getIntent().getStringExtra(TAG_STATUS_USER);

        mcallbackManager = CallbackManager.Factory.create();

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
//         GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

//        sign_in_fb_button = findViewById(R.id.sign_in_fb_button);

        sign_in_google_button = findViewById(R.id.sign_in_google_button);

        if (sharedPrefManager.getSPSudahLogin()){
            String status_login = sharedPrefManager.getSPStatus();
            if (status_login.equals("user")){
                startActivity(new Intent(LoginActivity.this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            }else {
                startActivity(new Intent(LoginActivity.this, ListBeasiswaActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
            }
        }

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

//        sign_in_fb_button.setReadPermissions("email");

//        sign_in_fb_button.registerCallback(mcallbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                Log.d(TAG, "onSuccess: "+ loginResult);
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//
//            }
//        });

        sign_in_google_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new LoginFragment(), "Login");
        adapter.addFragment(new DaftarFragment(), "Daftar");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String personEmail = account.getEmail();
            String personDisplayName = account.getDisplayName();
            Uri personPhoto = account.getPhotoUrl();

            String pass = "";

            cekUser(personEmail, personDisplayName, pass);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());

            //updateUI(null);
        }
    }

    private void cekUser(final String personEmail, final String personDisplayName, final String personPassword) {
        String url = Server.URL+"login/ceklogin.php?email="+personEmail;
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Cek User Data ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response != null){
                    try {
                        JSONObject jObj = new JSONObject(response);
                        pesan = jObj.getString(TAG_PESAN);
                        if(pesan.equals("berhasil")){

                            JSONObject data = jObj.getJSONObject("data");

                            String id = data.getString("id");
                            String email = data.getString("email");
                            String nama_lengkap = data.getString("nama_lengkap");
                            String status = data.getString("status");
                            String profile = data.getString("profile_pic");

                            status_user = status;
                            sharedPrefManager.saveSPString(SharedPrefManager.SP_STATUS, status);
                            profile_pic = profile;

                            if (status.equals("user")){
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                i.putExtra("ID", id);
                                i.putExtra("PERSON_EMAIL", email);
                                i.putExtra("NAMA_LENGKAP", nama_lengkap);
                                i.putExtra(TAG_STATUS_USER, status);
                                i.putExtra("PROFILE_PIC", profile);
                                startActivity(i);
                            }else {
                                Intent i = new Intent(LoginActivity.this, ListBeasiswaActivity.class);
                                i.putExtra("ID", id);
                                i.putExtra("PERSON_EMAIL", email);
                                i.putExtra("NAMA_LENGKAP", nama_lengkap);
                                i.putExtra(TAG_STATUS_USER, status);
                                i.putExtra("PROFILE_PIC", profile);
                                startActivity(i);
                            }
                            sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_LOGIN, true);

                        }else {
                            // Signed in successfully, show authenticated UI.
                            if (status_user.equals("user")){
                                Intent i = new Intent(LoginActivity.this, FormProfileActivity.class);
                                i.putExtra("PERSON_EMAIL", personEmail);
                                i.putExtra("PERSON_NAMA", personDisplayName);
                                i.putExtra("PERSON_PASSWORD", personPassword);
                                i.putExtra(TAG_STATUS_USER, status_user);
                                startActivity(i);
                                sharedPrefManager.saveSPString(SharedPrefManager.SP_STATUS, status_user);
                            }else {
                                createDonatur(personEmail, personDisplayName, status_user, personPassword);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                hideDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        "koneksi internet tidak stabil", Toast.LENGTH_LONG).show();

                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void createDonatur(final String personEmail, final String personDisplayName, final String status_user, final String personPassword) {
        String url = Server.URL+"user/create.php";

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    pesan = jObj.getString(TAG_PESAN);

                    // Check for error node in json
                    if (pesan.equals("berhasil")) {

                        Intent i = new Intent(LoginActivity.this, ListBeasiswaActivity.class);
                        i.putExtra("ID", jObj.getString("id"));
                        startActivity(i);
                        sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_LOGIN, true);
                        sharedPrefManager.saveSPString(SharedPrefManager.SP_STATUS, status_user);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                jObj.getString("message"), Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", personEmail);
                params.put("nama_lengkap", personDisplayName);
                params.put("status", status_user);
                params.put("password", personPassword);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
