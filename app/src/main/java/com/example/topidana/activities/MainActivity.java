package com.example.topidana.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.FragmentManager;

import android.app.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.topidana.R;
import com.example.topidana.app.AppController;
import com.example.topidana.app.CircleTransform;
import com.example.topidana.app.Server;
import com.example.topidana.fargments.ListBeasiswaDonaturFragment;
import com.example.topidana.fargments.ListBeasiswaFragment;
import com.example.topidana.fargments.MainFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.topidana.activities.LoginActivity.mGoogleSignInClient;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static String id, status_user, profile_pic;
    public static ProgressDialog pDialog;

    boolean doubleBackToExitPressedOnce = false;

    private static final String TAG = "MainActivity";
    public final static String TAG_ID = "ID";
    private static final String TAG_STATUS_USER = "STATUS_USER";
    private static final String TAG_PROFILE_PIC = "PROFILE_PIC";

    ImageView ivFotoChose;

    private static final String TAG_PESAN = "status";
    String pesan;
    String tag_json_obj = "json_obj_req";

    FragmentManager fragmentManager;
    Fragment fragment = null;
    Menu navMenuMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorRedRose));
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        id = getIntent().getStringExtra(TAG_ID);
        status_user = getIntent().getStringExtra(TAG_STATUS_USER);
        profile_pic = getIntent().getStringExtra(TAG_PROFILE_PIC);

        ivFotoChose = findViewById(R.id.ivFotoChose);

        String url = Server.URL_IMAGE+"profiles/"+profile_pic;

        Picasso.with(this).load(url)
                .placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile)
                .transform(new CircleTransform()).into(ivFotoChose);

        ivFotoChose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                i.putExtra(TAG_ID, id);
                i.putExtra(TAG_STATUS_USER, status_user);
                startActivity(i);
            }
        });

        pDialog = new ProgressDialog(this);

        getData();

        if (savedInstanceState == null) {
            navMenuMain = navigationView.getMenu();
            fragment = new MainFragment();
            callFragment(fragment);
            navMenuMain.findItem(R.id.nav_riwayat).setTitle("List Beasiswa");
        }
    }

    private void getData() {
        String url = Server.URL+"user/readby.php?id="+id;

        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response != null){
                    try {

                        JSONObject jObj = new JSONObject(response);
                        pesan = jObj.getString(TAG_PESAN);

                        if(pesan.equals("berhasil")){

                            JSONObject data = jObj.getJSONObject("data");

                            profile_pic = data.getString("profile_pic");

                            String url = Server.URL_IMAGE+"profiles/"+profile_pic;


                            Picasso.with(getApplicationContext()).load(url)
                                    .placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile)
                                    .transform(new CircleTransform()).into(ivFotoChose);
                        }

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                hideDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Profile", "GET DATA Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "koneksi internet tidak stabil", Toast.LENGTH_LONG).show();

                hideDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void callFragment(Fragment fragment) {
        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_container, fragment)
                .commit();
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent i = new Intent(MainActivity.this, WelcomePageActivity.class);
                        startActivity(i);
                    }
                });
    }

    public static void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    public static void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav_beranda) {
            fragment = new MainFragment();
            callFragment(fragment);
        } else if (id == R.id.nav_riwayat) {
            fragment = new ListBeasiswaFragment();
            callFragment(fragment);
        } else if (id == R.id.nav_logout) {
            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        fragment = new MainFragment();
        callFragment(fragment);

        if (doubleBackToExitPressedOnce) {
            signOut();
            super.onBackPressed();
            Toast.makeText(this, "signOut Done.", Toast.LENGTH_SHORT).show();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to signOut", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }
}
