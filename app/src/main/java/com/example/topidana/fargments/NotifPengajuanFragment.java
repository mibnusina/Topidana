package com.example.topidana.fargments;


import android.app.FragmentManager;
import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.topidana.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotifPengajuanFragment extends Fragment {

    RelativeLayout view;
    Button btn_lihat_pengajuan, btn_kembali;
    FragmentManager fragmentManager;
    Fragment fragment = null;

    public NotifPengajuanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = (RelativeLayout) inflater.inflate(R.layout.fragment_notif_pengajuan, container, false);

        btn_lihat_pengajuan = view.findViewById(R.id.btn_lihat_pengajuan);
        btn_kembali = view.findViewById(R.id.btn_kembali);

        btn_lihat_pengajuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new ListBeasiswaFragment();
                callFragment(fragment);
            }
        });

        btn_kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new MainFragment();
                callFragment(fragment);
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
