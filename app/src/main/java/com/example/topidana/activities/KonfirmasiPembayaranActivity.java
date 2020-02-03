package com.example.topidana.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.topidana.R;
import com.example.topidana.app.AppController;
import com.example.topidana.app.Server;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class KonfirmasiPembayaranActivity extends AppCompatActivity {

    ProgressDialog pDialog;

    Button btn_konfirmasi;
    TextView tv_title, tv_nama_bank, tv_norek;
    EditText et_no_transaksi;
    ImageView iv_bank;

    private static final String TAG_PESAN = "status";

    String login_id, user_id, beasiswa_id, nama_user, pesan, nomor_transaksi;
    int jumlah_donasi;
    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfirmasi_pembayaran);

        login_id = getIntent().getStringExtra("ID");
        user_id = getIntent().getStringExtra("user_id");
        beasiswa_id = getIntent().getStringExtra("beasiswa_id");
        nama_user = getIntent().getStringExtra("nama_user");
        jumlah_donasi = getIntent().getIntExtra("jumlah_donasi", 0);

        btn_konfirmasi = findViewById(R.id.btn_konfirmasi);
        tv_title = findViewById(R.id.tv_title);
        et_no_transaksi = findViewById(R.id.et_no_transaksi);
        iv_bank = findViewById(R.id.iv_bank);
        tv_nama_bank = findViewById(R.id.tv_nama_bank);
        tv_norek = findViewById(R.id.tv_norek);

        getDataBank();

        tv_title.setText("Anda berdonasi kepada " + nama_user + " sebesar " + jumlah_donasi);

        btn_konfirmasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nomor_transaksi = et_no_transaksi.getText().toString();
                if (!nomor_transaksi.equals(""))
                    konfirmasiPembayaran(login_id, beasiswa_id, jumlah_donasi, nomor_transaksi);
                else {
                    Toast.makeText(getApplicationContext(), "Nomor transaksi Tidak Boleh Kosong.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getDataBank() {
        String url = Server.URL+"bank/read.php";

        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response != null){
                    try {

                        JSONObject jObj = new JSONObject(response);
                        pesan = jObj.getString(TAG_PESAN);

                        if(pesan.equals("berhasil")){

                            JSONArray array = jObj.getJSONArray("data");

                            JSONObject data = array.getJSONObject(0);

                            String nama_bank = data.getString("nama_bank");
                            String logo_bank = data.getString("logo_bank");
                            String no_rek = data.getString("no_rek");

                            String url = Server.URL_ASSET+logo_bank;

                            tv_nama_bank.setText("Silahkan Transfer Ke account "+nama_bank);
                            tv_norek.setText(no_rek);

                            Picasso.with(getApplicationContext()).load(url)
                                    .placeholder(R.drawable.bca).error(R.drawable.bca)
                                    .into(iv_bank);
                        }

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        "koneksi internet tidak stabil", Toast.LENGTH_LONG).show();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void konfirmasiPembayaran(final String login_id, final String beasiswa_id, final int jumlah_donasi, final String nomor_transaksi) {
        String url = Server.URL+"donasi/create.php";

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    pesan = jObj.getString(TAG_PESAN);

                    // Check for error node in json
                    if (pesan.equals("berhasil")) {

                        Intent i = new Intent(KonfirmasiPembayaranActivity.this, NotifActivity.class);
                        i.putExtra("nama_user", nama_user);
                        i.putExtra("jumlah_donasi", jumlah_donasi);
                        startActivity(i);

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
                params.put("user_id", login_id);
                params.put("beasiswa_id", beasiswa_id);
                params.put("total_donasi", String.valueOf(jumlah_donasi));
                params.put("nomor_transaksi", nomor_transaksi);
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
