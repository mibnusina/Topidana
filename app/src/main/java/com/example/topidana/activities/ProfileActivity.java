package com.example.topidana.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.topidana.R;
import com.example.topidana.app.AppController;
import com.example.topidana.app.CircleTransform;
import com.example.topidana.app.Server;
import com.squareup.picasso.Picasso;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.example.topidana.app.FilePath.getPath;

public class ProfileActivity extends AppCompatActivity {

    ImageView ivFotoChose;
    EditText et_nama_lengkap, et_pendidikan, et_universitas, et_penghasilan_orangtua, et_deskripsi;
    Button btn_simpan;

    //Image request code
    private int PICK_IMAGE_REQUEST = 1;

    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;

    //Bitmap to get image from gallery
    private Bitmap bitmap;

    //Uri to store the image uri
    private Uri filePath;

    ProgressDialog pDialog;

    private static final String TAG_PESAN = "status";
    String pesan, login_id, status_user;
    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Kembali");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Requesting storage permission
        requestStoragePermission();

        login_id = getIntent().getStringExtra("ID");
        status_user = getIntent().getStringExtra("STATUS_USER");

        ivFotoChose = findViewById(R.id.ivFotoChose);
        et_nama_lengkap = findViewById(R.id.et_nama_lengkap);
        et_pendidikan = findViewById(R.id.et_pendidikan);
        et_universitas = findViewById(R.id.et_universitas);
        et_penghasilan_orangtua = findViewById(R.id.et_penghasilan_orangtua);
        et_deskripsi = findViewById(R.id.et_deskripsi);
        btn_simpan = findViewById(R.id.btn_simpan);

        getData();

        ivFotoChose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nama_lengkap= et_nama_lengkap.getText().toString();
                String pendidikan= et_pendidikan.getText().toString();
                String universitas= et_universitas.getText().toString();
                String penghasilan_orangtua= et_penghasilan_orangtua.getText().toString();
                String narasi= et_deskripsi.getText().toString();

                if (login_id.equalsIgnoreCase("") || nama_lengkap.equalsIgnoreCase("") || pendidikan.equalsIgnoreCase("")
                        || universitas.equalsIgnoreCase("") || penghasilan_orangtua.equalsIgnoreCase("") || narasi.equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(), "Data Harus Diisi Semua", Toast.LENGTH_SHORT).show();
                }else{
                    uploadMultipart();
                }
            }
        });
    }

    private void uploadMultipart() {
        String url = Server.URL+"user/update.php";
        String nama_lengkap= et_nama_lengkap.getText().toString();
        String pendidikan= et_pendidikan.getText().toString();
        String universitas= et_universitas.getText().toString();
        String penghasilan_orangtua= et_penghasilan_orangtua.getText().toString();
        String narasi= et_deskripsi.getText().toString();
        String foto;
        if(filePath != null){
            foto = getPath(getApplicationContext(),filePath);
        }else {
            foto = "";
        }

        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();
            if(filePath != null){
                //Creating a multi part request
                new MultipartUploadRequest(this, uploadId, url)
                        .addFileToUpload(foto, "profile_pic") //Adding file
                        .addParameter("id", login_id) //Adding text parameter to the request
                        .addParameter("nama_lengkap", nama_lengkap) //Adding text parameter to the request
                        .addParameter("pendidikan", pendidikan) //Adding text parameter to the request
                        .addParameter("tempat_kuliah", universitas) //Adding text parameter to the request
                        .addParameter("penghasilan_orangtua", penghasilan_orangtua) //Adding text parameter to the request
                        .addParameter("narasi", narasi) //Adding text parameter to the request
                        .setMaxRetries(2)
                        .startUpload(); //Starting the upload

                Toast.makeText(getApplicationContext(),
                        "user was updated.", Toast.LENGTH_LONG).show();

                Intent i = new Intent(ProfileActivity.this, MainActivity.class);
                i.putExtra("ID", login_id);
                i.putExtra("STATUS_USER", status_user);
                startActivity(i);
            }else {
                updateProfile(login_id, nama_lengkap, pendidikan, universitas, penghasilan_orangtua, narasi);
            }

        } catch (Exception exc) {
            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProfile(final String login_id, final String nama_lengkap, final String pendidikan, final String universitas, final String penghasilan_orangtua, final String narasi) {
        String url = Server.URL+"user/update.php";
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading ...");
        showDialog();;

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideDialog();

                try{
                    JSONObject jObj = new JSONObject(response);
                    pesan = jObj.getString(TAG_PESAN);

                    if (pesan.equals("berhasil")){

                        Toast.makeText(getApplicationContext(),
                                jObj.getString("message"), Toast.LENGTH_LONG).show();

                        Intent i = new Intent(ProfileActivity.this, MainActivity.class);
                        i.putExtra("ID", login_id);
                        i.putExtra("STATUS_USER", status_user);
                        startActivity(i);
                    }else{

                        Toast.makeText(getApplicationContext(),
                                jObj.getString("message"), Toast.LENGTH_LONG).show();
                    }
                }catch (JSONException e) {
                    // JSON error
                    Log.e("Edit Profile!", "error");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Profile Activity", "Edit Profile Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", login_id);
                params.put("nama_lengkap", nama_lengkap);
                params.put("pendidikan", pendidikan);
                params.put("tempat_kuliah", universitas);
                params.put("penghasilan_orangtua", penghasilan_orangtua);
                params.put("narasi", narasi);
                params.put("profile_pic", "x");
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void getData() {
        String url = Server.URL+"user/readby.php?id="+login_id;
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response != null){
                    try {

                        JSONObject jObj = new JSONObject(response);
                        pesan = jObj.getString(TAG_PESAN);

                        if(pesan.equals("berhasil")){

                            JSONObject data = jObj.getJSONObject("data");

                            et_nama_lengkap.setText(data.getString("nama_lengkap"));
                            et_pendidikan.setText(data.getString("pendidikan"));
                            et_universitas.setText(data.getString("tempat_kuliah"));
                            et_penghasilan_orangtua.setText(data.getString("penghasilan_orangtua"));
                            et_deskripsi.setText(data.getString("narasi"));

                            String profile_pic = data.getString("profile_pic");

                            String url = Server.URL_IMAGE+"profiles/"+profile_pic;


                            Picasso.with(getApplicationContext()).load(url)
                                    .placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile)
                                    .transform(new CircleTransform()).into(ivFotoChose);
                        }

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                hideDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Profile", "GET DATA Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "koneksi internet tidak stabil", Toast.LENGTH_LONG).show();

                hideDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
            } else {
                //Displaying another toast if permission is not granted
                //Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
                requestStoragePermission();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ivFotoChose.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent i = new Intent(ProfileActivity.this, MainActivity.class);
        i.putExtra("ID", login_id);
        i.putExtra("STATUS_USER", status_user);
        startActivity(i);
        return true;
    }
}
