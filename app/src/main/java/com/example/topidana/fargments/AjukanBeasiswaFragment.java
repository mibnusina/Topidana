package com.example.topidana.fargments;


import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.topidana.R;
import com.example.topidana.activities.FormPersonalityActivity;
import com.example.topidana.activities.FormProfileActivity;
import com.example.topidana.activities.MainActivity;
import com.example.topidana.app.AppController;
import com.example.topidana.app.Server;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class AjukanBeasiswaFragment extends Fragment {

    ProgressDialog pDialog;
    RelativeLayout view;
    Button btn_ajukan;
    EditText et_untuk_keperluan, et_biaya_yang_dibutuhkan, et_deadline, et_deskripsi;
    FragmentManager fragmentManager;
    Fragment fragment = null;

    private static final String TAG_PESAN = "status";

    final Calendar myCalendar = Calendar.getInstance();

    String keperluan, biaya, deadline, deskripsi;

    String pesan;
    String tag_json_obj = "json_obj_req";

    public AjukanBeasiswaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = (RelativeLayout) inflater.inflate(R.layout.fragment_ajukan_beasiswa, container, false);

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

        btn_ajukan = view.findViewById(R.id.btn_ajukan);
        et_untuk_keperluan = view.findViewById(R.id.et_untuk_keperluan);
        et_biaya_yang_dibutuhkan = view.findViewById(R.id.et_biaya_yang_dibutuhkan);
        et_deadline = view.findViewById(R.id.et_deadline);
        et_deskripsi = view.findViewById(R.id.et_deskripsi);

        et_deadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btn_ajukan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                keperluan = et_untuk_keperluan.getText().toString();
                biaya = et_biaya_yang_dibutuhkan.getText().toString();
                deadline = et_deadline.getText().toString();
                deskripsi = et_deskripsi.getText().toString();

                if (!MainActivity.id.equals("") && !keperluan.equals("") && !biaya.equals("") && !deadline.equals("") && !deskripsi.equals("")){
                    ajukanBeasiswa(MainActivity.id, keperluan, biaya, deadline, deskripsi);
                }else{
                    Toast.makeText(getActivity().getApplicationContext(), "Data Harus Diisi Semua", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void ajukanBeasiswa(final String id, final String keperluan, final String biaya, final String deadline, final String deskripsi) {
        String url = Server.URL+"beasiswa/create.php";
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    pesan = jObj.getString(TAG_PESAN);
                    if (pesan.equals("berhasil")) {

                        fragment = new NotifPengajuanFragment();
                        callFragment(fragment);

                    } else {
                        Toast.makeText(getApplicationContext(),
                                jObj.getString("message"), Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hideDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getApplicationContext(),
//                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", id);
                params.put("keperluan", keperluan);
                params.put("biaya", biaya);
                params.put("deadline", deadline);
                params.put("deskripsi", deskripsi);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void callFragment(Fragment fragment) {
        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_container, fragment)
                .commit();
    }

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        et_deadline.setText(sdf.format(myCalendar.getTime()));
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
