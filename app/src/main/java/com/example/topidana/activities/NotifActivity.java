package com.example.topidana.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.topidana.R;

public class NotifActivity extends AppCompatActivity {

    TextView deskripsi_notif;
    Button btn_kembali, btn_lihat_list;

    String nama_user;
    int jumlah_donasi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif);

        deskripsi_notif = findViewById(R.id.deskripsi_notif);
        btn_kembali = findViewById(R.id.btn_kembali);
        btn_lihat_list = findViewById(R.id.btn_lihat_list);

        nama_user = getIntent().getStringExtra("nama_user");
        jumlah_donasi = getIntent().getIntExtra("jumlah_donasi", 0);

        deskripsi_notif.setText("Anda berhasil berdonasi kepada "+ nama_user +" sebesar "+ jumlah_donasi);

        btn_kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(NotifActivity.this, ListBeasiswaActivity.class);
                i.putExtra("ID", ListBeasiswaActivity.login_id);
                startActivity(i);
            }
        });

        btn_lihat_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(NotifActivity.this, ListDonasiActivity.class);
                i.putExtra("ID", ListBeasiswaActivity.login_id);
                startActivity(i);
            }
        });
    }
}
