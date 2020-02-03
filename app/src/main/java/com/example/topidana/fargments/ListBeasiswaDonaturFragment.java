package com.example.topidana.fargments;


import android.app.ProgressDialog;
import android.os.Bundle;

import android.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.topidana.R;
import com.example.topidana.activities.ListBeasiswaActivity;
import com.example.topidana.adapters.BeasiswaAdapter;
import com.example.topidana.app.AppController;
import com.example.topidana.app.Server;
import com.example.topidana.models.Beasiswa;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListBeasiswaDonaturFragment extends Fragment {

    RelativeLayout view, lyInfo;
    LinearLayout lyList;
    ListView lvBeasiswa;

    private static final String TAG_PESAN = "status";

    String pesan;
    String tag_json_obj = "json_obj_req";

    BeasiswaAdapter adapter;

    public ListBeasiswaDonaturFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = (RelativeLayout) inflater.inflate(R.layout.fragment_list_beasiswa_donatur, container, false);

        lvBeasiswa = view.findViewById(R.id.lvBeasiswa);
        lyList = view.findViewById(R.id.lyList);
        lyInfo = view.findViewById(R.id.lyInfo);

        getDataBeasiswa();

        return view;
    }

    private void getDataBeasiswa() {
        String url = Server.URL+"beasiswa/read.php";
        ListBeasiswaActivity.pDialog.setCancelable(false);
        ListBeasiswaActivity.pDialog.setMessage("Loading...");
        ListBeasiswaActivity.showDialog();

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

                            ArrayList<Beasiswa> beasiswas = new ArrayList<>();
                            for(int i = 0; i < array_data.length(); i ++){
                                JSONObject data = array_data.getJSONObject(i);

                                String id = data.getString("id");
                                String user_id = data.getString("user_id");
                                String nama_user = data.getString("nama");
                                String keperluan = data.getString("keperluan");
                                String biaya = data.getString("biaya");
                                String terkumpul = data.getString("terkumpul");
                                String deadline = data.getString("deadline");
                                String deskripsi = data.getString("deskripsi");
                                String donatur = data.getString("donatur");
                                String transkrip = data.getString("transkrip_nilai");
                                String status = data.getString("status");

                                String flag = "donatur";

                                Float fBiaya = Float.valueOf(biaya);
                                Float fTerkumpul = Float.valueOf(terkumpul);

                                Beasiswa object = new Beasiswa(flag, id, user_id, nama_user, keperluan, fBiaya, fTerkumpul, deadline, deskripsi, donatur, transkrip, status);

                                beasiswas.add(object);
                            }

                            adapter = new BeasiswaAdapter(getActivity().getApplicationContext(), R.layout.list_beasiswa, beasiswas);
                            adapter.notifyDataSetChanged();
                            lvBeasiswa.setAdapter(adapter);
                        }else {
                            lyList.setVisibility(View.GONE);
                            lyInfo.setVisibility(View.VISIBLE);
                        }

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                ListBeasiswaActivity.hideDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "koneksi internet tidak stabil", Toast.LENGTH_LONG).show();

                ListBeasiswaActivity.hideDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

}
