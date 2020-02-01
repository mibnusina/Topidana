package com.example.topidana.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.topidana.R;
import com.example.topidana.adapters.DonasiAdapter;
import com.example.topidana.app.AppController;
import com.example.topidana.app.Server;
import com.example.topidana.models.Donasi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListDonasiActivity extends AppCompatActivity {

    ProgressDialog pDialog;

    ListView lvDonasi;
    LinearLayout lyList;
    RelativeLayout lyInfo;

    DonasiAdapter adapter;

    private static final String TAG_PESAN = "status";
    String pesan, login_id, status_user;
    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_donasi);

        login_id = getIntent().getStringExtra("ID");
        status_user = getIntent().getStringExtra("STATUS_USER");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Kembali");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        
        lvDonasi = findViewById(R.id.lvDonasi);
        lyList = findViewById(R.id.lyList);
        lyInfo = findViewById(R.id.lyInfo);

        getData();
    }

    private void getData() {
        String url = Server.URL+"donasi/readby.php?user_id="+login_id;
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response != null){
                    try {

                        JSONObject jObj = new JSONObject(response);
                        pesan = jObj.getString(TAG_PESAN);

                        if(pesan.equals("berhasil")){

                            lyList.setVisibility(View.VISIBLE);
                            lyInfo.setVisibility(View.GONE);

                            JSONArray array_data = jObj.getJSONArray("data");
                            Log.e("List Beasiswa", "Data Api Response: " + array_data.toString());

                            ArrayList<Donasi> donasis = new ArrayList<>();
                            for(int i = 0; i < array_data.length(); i ++){
                                JSONObject data = array_data.getJSONObject(i);

                                String nama_lengkap = data.getString("nama_lengkap");
                                String keperluan = data.getString("keperluan");
                                String total_donasi = data.getString("total_donasi");
                                String created = data.getString("created");
                                String status = data.getString("status");

                                Donasi object = new Donasi(nama_lengkap, keperluan, total_donasi, created, status);

                                donasis.add(object);
                            }

                            adapter = new DonasiAdapter(getApplicationContext(), R.layout.list_donasi, donasis);
                            adapter.notifyDataSetChanged();
                            lvDonasi.setAdapter(adapter);
                        }else {
                            lyList.setVisibility(View.GONE);
                            lyInfo.setVisibility(View.VISIBLE);
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
                Log.e("List Beasiswa", "GET DATA Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "koneksi internet tidak stabil", Toast.LENGTH_LONG).show();

                hideDialog();
            }
        });

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

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(ListDonasiActivity.this, ListBeasiswaActivity.class);
        intent.putExtra("ID", login_id);
        intent.putExtra("STATUS_USER", status_user);
        startActivity(intent);
        return true;
    }
}
