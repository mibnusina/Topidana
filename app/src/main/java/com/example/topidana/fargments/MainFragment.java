package com.example.topidana.fargments;


import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.topidana.R;
import com.example.topidana.activities.FormProfileActivity;
import com.example.topidana.activities.LoginActivity;
import com.example.topidana.activities.MainActivity;
import com.example.topidana.activities.ProfileActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    RelativeLayout view;
    Button btn_ajukan_beasiswa, btn_personalisasi_profile, btn_lihat_beasiswa;
    FragmentManager fragmentManager;
    Fragment fragment = null;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = (RelativeLayout) inflater.inflate(R.layout.fragment_main, container, false);

        btn_ajukan_beasiswa = view.findViewById(R.id.btn_ajukan_beasiswa);
        btn_personalisasi_profile = view.findViewById(R.id.btn_personalisasi_profile);
        btn_lihat_beasiswa = view.findViewById(R.id.btn_lihat_beasiswa);

        btn_ajukan_beasiswa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new AjukanBeasiswaFragment();
                callFragment(fragment);
            }
        });

        btn_lihat_beasiswa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new ListBeasiswaFragment();
                callFragment(fragment);
            }
        });

        btn_personalisasi_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity().getApplicationContext(), ProfileActivity.class);
                i.putExtra("ID", MainActivity.id);
                startActivity(i);
            }
        });

        return view;
    }

    private void callFragment(Fragment fragment) {
        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_container, fragment)
                .commit();
    }

}
