package com.example.topidana.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.topidana.R;
import com.example.topidana.app.Server;

public class DetailBeasiswaActivity extends AppCompatActivity {

    TextView tv_title,tv_deadline,tv_total_donasi,tv_total_biaya,tv_pecen,tv_deskripsi,tv_info, tv_lihat_pdf;
    EditText et_jumlah_lain;
    ProgressBar pb_percen;
    Button btn_donasi,btn_10,btn_50,btn_100,btn_150, btn_jml_lain;
    int jumlah_donasi = 0;

    String login_id = ListBeasiswaActivity.login_id;
    String beasiswa_id, user_id, nama_user, keperluan, deskripsi, donatur, flag_button, transkrip_urlpdf;
    float biaya, terkumpul;
    int deadline, percen;

    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    View dialogView;

    final String googleDocsUrl = "http://docs.google.com/viewer?embedded=true&url=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_beasiswa);

        beasiswa_id = getIntent().getStringExtra("beasiswa_id");
        user_id = getIntent().getStringExtra("user_id");
        nama_user = getIntent().getStringExtra("nama_user");
        keperluan = getIntent().getStringExtra("keperluan");
        deadline = getIntent().getIntExtra("deadline", 0);
        deskripsi = getIntent().getStringExtra("deskripsi");
        biaya = getIntent().getFloatExtra("biaya", 0);
        terkumpul = getIntent().getFloatExtra("terkumpul",0);
        percen = getIntent().getIntExtra("percen", 0);
        donatur = getIntent().getStringExtra("donatur");

        String url = getIntent().getStringExtra("transkrip_nilai");
        transkrip_urlpdf = Server.URL_IMAGE+"transkrips/"+url;

        tv_title = findViewById(R.id.tv_title);
        tv_deadline = findViewById(R.id.tv_deadline);
        tv_total_donasi = findViewById(R.id.tv_total_donasi);
        tv_total_biaya = findViewById(R.id.tv_total_biaya);
        tv_pecen = findViewById(R.id.tv_pecen);
        tv_deskripsi = findViewById(R.id.tv_deskripsi);
        tv_info = findViewById(R.id.tv_info);
        tv_lihat_pdf = findViewById(R.id.tv_lihat_pdf);
        pb_percen = findViewById(R.id.pb_percen);
        btn_10 = findViewById(R.id.btn_10);
        btn_50 = findViewById(R.id.btn_50);
        btn_100 = findViewById(R.id.btn_100);
        btn_150 = findViewById(R.id.btn_150);
        btn_donasi = findViewById(R.id.btn_donasi);
        btn_jml_lain = findViewById(R.id.btn_jml_lain);

        tv_title.setText("Bantu "+ nama_user +" untuk mendapatkan "+ keperluan);
        tv_deadline.setText("Deadline : "+deadline+" Hari lagi");
        tv_total_biaya.setText(String.valueOf(biaya));
        tv_total_donasi.setText(String.valueOf(terkumpul));
        tv_pecen.setText(String.valueOf(percen));
        tv_deskripsi.setText(deskripsi);
        pb_percen.setProgress(percen);

        if(!donatur.equals("0")){
            tv_info.setText("Sudah "+ donatur +" telah berdonasi kepada "+ nama_user +", kamu kapan ?");
        }else {
            tv_info.setText("belum ada yang berdonasi kepada "+ nama_user +", jadi yang pertama donasi.");
        }

        tv_lihat_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog = new ProgressDialog(DetailBeasiswaActivity.this);
                progressDialog.setMessage("Loading Data...");
                progressDialog.setCancelable(false);
                final Dialog dialog = new Dialog(DetailBeasiswaActivity.this);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.pdf_view_dialog);
                WebView web_view = dialog.findViewById(R.id.webview);
                web_view.requestFocus();
                web_view.getSettings().setJavaScriptEnabled(true);
                String url = googleDocsUrl+transkrip_urlpdf;
                web_view.loadUrl(url);
                web_view.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }
                });
                web_view.setWebChromeClient(new WebChromeClient() {
                    public void onProgressChanged(WebView view, int progress) {
                        if (progress < 100) {
                            progressDialog.show();
                            dialog.dismiss();
                        }

                        if (progress == 100) {
                            progressDialog.dismiss();
                            dialog.show();
                        }
                        Log.e("AWAWAW", "onProgressChanged: "+progress);
                    }
                });
            }
        });

        btn_10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag_button = "btn_10";
                jumlah_donasi = 10000;
                activateButton(flag_button);
            }
        });

        btn_50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag_button = "btn_50";
                jumlah_donasi = 50000;
                activateButton(flag_button);
            }
        });

        btn_100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag_button = "btn_100";
                jumlah_donasi = 100000;
                activateButton(flag_button);
            }
        });

        btn_150.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag_button = "btn_150";
                jumlah_donasi = 150000;
                activateButton(flag_button);
            }
        });

        btn_jml_lain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag_button = "btn_jml_lain";
                DialogForm();
                activateButton(flag_button);
            }
        });

        btn_donasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (jumlah_donasi != 0){
                    Intent i = new Intent(DetailBeasiswaActivity.this, KonfirmasiPembayaranActivity.class);
                    i.putExtra("ID", login_id);
                    i.putExtra("user_id", user_id);
                    i.putExtra("beasiswa_id", beasiswa_id);
                    i.putExtra("nama_user", nama_user);
                    i.putExtra("jumlah_donasi", jumlah_donasi);
                    startActivity(i);
                }
            }
        });
    }

    private void activateButton(String flag_button) {
        if (flag_button.equals("btn_10")){
            btn_10.setBackgroundResource(R.drawable.banana_button);
            btn_50.setBackgroundResource(R.drawable.border_button);
            btn_100.setBackgroundResource(R.drawable.border_button);
            btn_150.setBackgroundResource(R.drawable.border_button);
            btn_jml_lain.setBackgroundResource(R.drawable.border_button);
            btn_jml_lain.setText("Jumlah Lainnya");
        }else if(flag_button.equals("btn_50")){
            btn_10.setBackgroundResource(R.drawable.border_button);
            btn_50.setBackgroundResource(R.drawable.banana_button);
            btn_100.setBackgroundResource(R.drawable.border_button);
            btn_150.setBackgroundResource(R.drawable.border_button);
            btn_jml_lain.setBackgroundResource(R.drawable.border_button);
            btn_jml_lain.setText("Jumlah Lainnya");
        }else if(flag_button.equals("btn_100")){
            btn_10.setBackgroundResource(R.drawable.border_button);
            btn_50.setBackgroundResource(R.drawable.border_button);
            btn_100.setBackgroundResource(R.drawable.banana_button);
            btn_150.setBackgroundResource(R.drawable.border_button);
            btn_jml_lain.setBackgroundResource(R.drawable.border_button);
            btn_jml_lain.setText("Jumlah Lainnya");
        }else if(flag_button.equals("btn_150")){
            btn_10.setBackgroundResource(R.drawable.border_button);
            btn_50.setBackgroundResource(R.drawable.border_button);
            btn_100.setBackgroundResource(R.drawable.border_button);
            btn_150.setBackgroundResource(R.drawable.banana_button);
            btn_jml_lain.setBackgroundResource(R.drawable.border_button);
            btn_jml_lain.setText("Jumlah Lainnya");
        }else {
            btn_10.setBackgroundResource(R.drawable.border_button);
            btn_50.setBackgroundResource(R.drawable.border_button);
            btn_100.setBackgroundResource(R.drawable.border_button);
            btn_150.setBackgroundResource(R.drawable.border_button);
            btn_jml_lain.setBackgroundResource(R.drawable.banana_button);
        }
    }

    private void DialogForm() {
        dialog = new AlertDialog.Builder(DetailBeasiswaActivity.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_jumlah_donasi, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        et_jumlah_lain    = (EditText) dialogView.findViewById(R.id.et_jumlah_lain);

        dialog.setPositiveButton("Set", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String jumlah = et_jumlah_lain.getText().toString();
                btn_jml_lain.setText(jumlah);
                jumlah_donasi = Integer.parseInt(jumlah);
                dialog.dismiss();
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                btn_jml_lain.setBackgroundResource(R.drawable.border_button);
                btn_jml_lain.setText("Jumlah Lainnya");
                jumlah_donasi = 0;
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
