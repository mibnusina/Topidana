package com.example.topidana.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.topidana.R;
import com.example.topidana.models.Donasi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DonasiAdapter extends ArrayAdapter<Donasi> {

    private ArrayList<Donasi> objects;
    private Context mContext;
    int mResource;

    public DonasiAdapter(Context context, int resource, ArrayList<Donasi> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.objects = objects;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String nama_user = getItem(position).getNama_user();
        String keperluan = getItem(position).getKeperluan();
        String total_donasi = getItem(position).getTotal_donasi();
        String tgl_donasi = getItem(position).getTgl_donasi();
        String status = getItem(position).getStatus();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tv_title = convertView.findViewById(R.id.tv_title);
        TextView tv_total_donasi = convertView.findViewById(R.id.tv_total_donasi);
        TextView tv_tgl_donasi = convertView.findViewById(R.id.tv_tgl_donasi);
        TextView tv_status = convertView.findViewById(R.id.tv_status);

        tv_title.setText("Bantu "+ nama_user + " untuk mendapatkan "+ keperluan);
        tv_total_donasi.setText("Rp. " + total_donasi);
        tv_tgl_donasi.setText(tgl_donasi);
        tv_status.setText(String.valueOf(status)+" disetujui");

        return convertView;
    }
}
