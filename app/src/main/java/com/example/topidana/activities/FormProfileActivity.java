package com.example.topidana.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.topidana.R;

public class FormProfileActivity extends AppCompatActivity {

    EditText et_nama_lengkap,et_pendidikan,et_tempat_kuliah,et_tanggal_lahir;

    Button btn_selanjutnya;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_profile);

        et_nama_lengkap = findViewById(R.id.et_nama_lengkap);
        et_pendidikan = findViewById(R.id.et_pendidikan);
        et_tempat_kuliah = findViewById(R.id.et_tempat_kuliah);
        et_tanggal_lahir = findViewById(R.id.et_tanggal_lahir);
        btn_selanjutnya = findViewById(R.id.btn_selanjutnya);

        btn_selanjutnya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FormProfileActivity.this, FormPersonalityActivity.class);
                //i.putExtra("PERSON_EMAIL", personEmail);
                startActivity(i);
            }
        });
    }
}
