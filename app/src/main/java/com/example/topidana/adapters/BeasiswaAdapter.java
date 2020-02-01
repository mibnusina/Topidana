package com.example.topidana.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.topidana.R;
import com.example.topidana.activities.DetailBeasiswaActivity;
import com.example.topidana.activities.ListBeasiswaActivity;
import com.example.topidana.models.Beasiswa;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BeasiswaAdapter extends ArrayAdapter<Beasiswa> {

    private static final String TAG = "ProdukArrayAdapter";
    private ArrayList<Beasiswa> objects;
    private Context mContext;
    int mResource;

    public BeasiswaAdapter(Context context, int resource, ArrayList<Beasiswa> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.objects = objects;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String flag = getItem(position).getFlag();
        final String id = getItem(position).getId();
        final String user_id = getItem(position).getUser_id();
        final String nama = getItem(position).getNama_user();
        final String keperluan = getItem(position).getKeperluan();
        final Float biaya = getItem(position).getBiaya();
        final Float terkumpul = getItem(position).getTerkumpul();
        final String deadline = getItem(position).getDeadline();
        final String deskripsi = getItem(position).getDeskripsi();
        final String donatur = getItem(position).getDonatur();
        final String transkrip = getItem(position).getTranskrip();
        final String status = getItem(position).getStatus();

        int daysBetween = 0;
        final int percen = (int) ((terkumpul / biaya) * 100);

        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        try {
//            Date dateBefore = myFormat.parse(String.valueOf(now));
            Date dateAfter = myFormat.parse(deadline);
            long difference = dateAfter.getTime() - now.getTime();
            daysBetween = (int) (difference / (1000*60*60*24));
        } catch (Exception e) {
            e.printStackTrace();
        }

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        LinearLayout layout_btn_donasi = convertView.findViewById(R.id.layout_btn_donasi);
        Button btn_beri_donasi = convertView.findViewById(R.id.btn_beri_donasi);
        final TextView tv_title = convertView.findViewById(R.id.tv_title);
        TextView tv_deadline = convertView.findViewById(R.id.tv_deadline);
        TextView tv_total_donasi = convertView.findViewById(R.id.tv_total_donasi);
        TextView tv_total_biaya = convertView.findViewById(R.id.tv_total_biaya);
        TextView tv_pecen = convertView.findViewById(R.id.tv_pecen);
        TextView tv_status = convertView.findViewById(R.id.tv_status);
        ProgressBar pb_percen = convertView.findViewById(R.id.pb_percen);

        if (flag.equals("user")){
            layout_btn_donasi.setVisibility(View.GONE);
            tv_status.setVisibility(View.VISIBLE);
            tv_title.setText(keperluan);
        }else {
            layout_btn_donasi.setVisibility(View.VISIBLE);
            tv_status.setVisibility(View.GONE);
            tv_title.setText("Bantu "+ nama +" untuk mendapatkan "+ keperluan);
            btn_beri_donasi.setText("Beri Donasi Untuk " + nama);
        }
        tv_deadline.setText("Deadline : "+String.valueOf(daysBetween)+" Hari lagi");
        tv_total_biaya.setText(String.valueOf(biaya));
        tv_total_donasi.setText(String.valueOf(terkumpul));
        tv_pecen.setText(String.valueOf(percen));
        tv_status.setText(String.valueOf(status)+" disetujui");
        pb_percen.setProgress(percen);

        final int finalDaysBetween = daysBetween;
        btn_beri_donasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, DetailBeasiswaActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("beasiswa_id", id);
                intent.putExtra("user_id", user_id);
                intent.putExtra("nama_user", nama);
                intent.putExtra("keperluan", keperluan);
                intent.putExtra("biaya", biaya);
                intent.putExtra("terkumpul", terkumpul);
                intent.putExtra("deadline", finalDaysBetween);
                intent.putExtra("deskripsi", deskripsi);
                intent.putExtra("percen", percen);
                intent.putExtra("transkrip_nilai", transkrip);
                intent.putExtra("donatur", donatur);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }
}
