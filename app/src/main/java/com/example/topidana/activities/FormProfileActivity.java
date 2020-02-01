package com.example.topidana.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.topidana.R;
import com.example.topidana.app.AppController;
import com.example.topidana.app.Server;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FormProfileActivity extends AppCompatActivity {

    EditText et_nama_lengkap,et_pendidikan,et_tempat_kuliah,et_tanggal_lahir;
    Button btn_selanjutnya;

    private static final String TAG = "FormProfileActivity";
    public final static String TAG_EMAIL = "PERSON_EMAIL";
    private static final String TAG_STATUS_USER = "STATUS_USER";

    String person_email, nama_lengkap, pendidikan, tempat_kuliah, tanggal_lahir, status_user;

    final Calendar myCalendar = Calendar.getInstance();

    private static final String TAG_PESAN = "status";

    String pesan;
    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_profile);

        person_email = getIntent().getStringExtra(TAG_EMAIL);
        status_user = getIntent().getStringExtra(TAG_STATUS_USER);

        et_nama_lengkap = findViewById(R.id.et_nama_lengkap);
        et_pendidikan = findViewById(R.id.et_pendidikan);
        et_tempat_kuliah = findViewById(R.id.et_tempat_kuliah);
        et_tanggal_lahir = findViewById(R.id.et_tanggal_lahir);
        btn_selanjutnya = findViewById(R.id.btn_selanjutnya);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        et_tanggal_lahir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(FormProfileActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btn_selanjutnya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nama_lengkap = et_nama_lengkap.getText().toString();
                pendidikan = et_pendidikan.getText().toString();
                tempat_kuliah = et_tempat_kuliah.getText().toString();
                tanggal_lahir = et_tanggal_lahir.getText().toString();

                if (!nama_lengkap.equals("") && !pendidikan.equals("") && !tempat_kuliah.equals("") && !tanggal_lahir.equals("")){
                    insertUser(person_email, nama_lengkap, pendidikan, tempat_kuliah, tanggal_lahir, status_user);
                }else{
                    Toast.makeText(getApplicationContext(), "Data Harus Diisi Semua", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void insertUser(final String person_email, final String nama_lengkap, final String pendidikan, final String tempat_kuliah, final String tanggal_lahir, final String status_user) {
        String url = Server.URL+"user/create.php";

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    pesan = jObj.getString(TAG_PESAN);

                    // Check for error node in json
                    if (pesan.equals("berhasil")) {

                        Intent i = new Intent(FormProfileActivity.this, FormPersonalityActivity.class);
                        i.putExtra("ID", jObj.getString("id"));
                        i.putExtra("pendidikan", pendidikan);
                        i.putExtra("tempat_kuliah", tempat_kuliah);
                        i.putExtra("tanggal_lahir", tanggal_lahir);
                        startActivity(i);

                    } else {
                        Toast.makeText(getApplicationContext(),
                                jObj.getString("message"), Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    // JSON error
                    Log.e("Daftar Berhasil!", "error");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Daftar Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", person_email);
                params.put("nama_lengkap", nama_lengkap);
                params.put("status", status_user);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        et_tanggal_lahir.setText(sdf.format(myCalendar.getTime()));
    }
}
