package com.example.topidana.fargments;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.topidana.R;
import com.example.topidana.activities.FormProfileActivity;
import com.example.topidana.activities.ListBeasiswaActivity;
import com.example.topidana.activities.LoginActivity;
import com.example.topidana.activities.MainActivity;
import com.example.topidana.app.AppController;
import com.example.topidana.app.Server;
import com.example.topidana.services.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class DaftarFragment extends Fragment {

    LinearLayout view;
    Button btn_sign_in;
    EditText et_nama, et_email, et_password;

    private static final String TAG_PESAN = "status";
    private static final String TAG_STATUS_USER = "STATUS_USER";

    String pesan;
    String tag_json_obj = "json_obj_req";

    public DaftarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = (LinearLayout) inflater.inflate(R.layout.fragment_daftar, container, false);

        btn_sign_in = view.findViewById(R.id.btn_sign_in);
        et_nama = view.findViewById(R.id.et_nama);
        et_email = view.findViewById(R.id.et_email);
        et_password = view.findViewById(R.id.et_password);

        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nama = et_nama.getText().toString();
                String email = et_email.getText().toString();
                String password = et_password.getText().toString();

                if (!nama.equalsIgnoreCase("") && !email.equalsIgnoreCase("") && !password.equalsIgnoreCase("")){
                    daftar(email, nama, password);
                }
            }
        });

        return view;
    }

    private void daftar(final String personEmail, final String personDisplayName, final String personPassword) {
        String url = Server.URL+"login/ceklogin.php?email="+personEmail;

        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response != null){
                    try {
                        JSONObject jObj = new JSONObject(response);
                        pesan = jObj.getString(TAG_PESAN);
                        if(pesan.equals("berhasil")){

                            Toast.makeText(getActivity().getApplicationContext(),
                                    "User Sudah Terdaftar, Silahkan Login", Toast.LENGTH_LONG).show();

                        }else {
                            // Signed in successfully, show authenticated UI.
                            if (LoginActivity.status_user.equals("user")){
                                Intent i = new Intent(getActivity().getApplicationContext(), FormProfileActivity.class);
                                i.putExtra("PERSON_EMAIL", personEmail);
                                i.putExtra("PERSON_NAMA", personDisplayName);
                                i.putExtra("PERSON_PASSWORD", personPassword);
                                i.putExtra(TAG_STATUS_USER, LoginActivity.status_user);
                                startActivity(i);
                                LoginActivity.sharedPrefManager.saveSPString(SharedPrefManager.SP_STATUS, LoginActivity.status_user);
                            }else {
                                createDonatur(personEmail, personDisplayName, LoginActivity.status_user, personPassword);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "koneksi internet tidak stabil", Toast.LENGTH_LONG).show();
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

                        Intent i = new Intent(getActivity().getApplicationContext(), ListBeasiswaActivity.class);
                        i.putExtra("ID", jObj.getString("id"));
                        startActivity(i);
                        LoginActivity.sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_LOGIN, true);
                        LoginActivity.sharedPrefManager.saveSPString(SharedPrefManager.SP_STATUS, status_user);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(),
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
                Toast.makeText(getActivity().getApplicationContext(),
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

}
