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
public class LoginFragment extends Fragment {

    LinearLayout view;
    Button btn_login;
    EditText et_email, et_password;

    private static final String TAG_PESAN = "status";
    private static final String TAG_STATUS_USER = "STATUS_USER";

    String pesan;
    String tag_json_obj = "json_obj_req";

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = (LinearLayout) inflater.inflate(R.layout.fragment_login, container, false);

        btn_login = view.findViewById(R.id.btn_login);
        et_email = view.findViewById(R.id.et_email);
        et_password = view.findViewById(R.id.et_password);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = et_email.getText().toString();
                String password = et_password.getText().toString();

                if (!email.equalsIgnoreCase("") && !password.equalsIgnoreCase("")){
                    login(email, password);
                }
            }
        });

        return view;
    }

    private void login(final String email, final String password) {
        String url = Server.URL+"login/login.php";

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
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

                            LoginActivity.status_user = status;
                            LoginActivity.sharedPrefManager.saveSPString(SharedPrefManager.SP_STATUS, status);
                            LoginActivity.profile_pic = profile;

                            if (status.equals("user")){
                                Intent i = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                                i.putExtra("ID", id);
                                i.putExtra("PERSON_EMAIL", email);
                                i.putExtra("NAMA_LENGKAP", nama_lengkap);
                                i.putExtra(TAG_STATUS_USER, status);
                                i.putExtra("PROFILE_PIC", profile);
                                startActivity(i);
                            }else {
                                Intent i = new Intent(getActivity().getApplicationContext(), ListBeasiswaActivity.class);
                                i.putExtra("ID", id);
                                i.putExtra("PERSON_EMAIL", email);
                                i.putExtra("NAMA_LENGKAP", nama_lengkap);
                                i.putExtra(TAG_STATUS_USER, status);
                                i.putExtra("PROFILE_PIC", profile);
                                startActivity(i);
                            }
                            LoginActivity.sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_LOGIN, true);

                        }else {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "User Belum Terdaftar, Silahkan Daftar", Toast.LENGTH_LONG).show();
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
                params.put("email", email);
                params.put("password", password);
                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

}
