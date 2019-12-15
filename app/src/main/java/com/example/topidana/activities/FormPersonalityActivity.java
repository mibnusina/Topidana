package com.example.topidana.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.topidana.R;

public class FormPersonalityActivity extends AppCompatActivity {

    EditText et_narasi_kamu,et_penghasilan_orangtua,et_transkrip_nilai;
    Button btn_selesai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_personality);

        et_narasi_kamu = findViewById(R.id.et_narasi_kamu);
        et_penghasilan_orangtua = findViewById(R.id.et_penghasilan_orangtua);
        et_transkrip_nilai = findViewById(R.id.et_transkrip_nilai);
        btn_selesai = findViewById(R.id.btn_selesai);

        btn_selesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FormPersonalityActivity.this, MainActivity.class);
                //i.putExtra("PERSON_EMAIL", personEmail);
                startActivity(i);
            }
        });
    }
}
