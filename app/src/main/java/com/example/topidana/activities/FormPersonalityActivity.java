package com.example.topidana.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.topidana.R;
import com.example.topidana.app.AppController;
import com.example.topidana.app.FilePath;
import com.example.topidana.app.FilenameUtils;
import com.example.topidana.app.Server;

import net.gotev.uploadservice.MultipartUploadRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.example.topidana.app.FilePath.getPath;

public class FormPersonalityActivity extends AppCompatActivity {

    EditText et_narasi_kamu,et_penghasilan_orangtua,et_transkrip_nilai;
    Button btn_selesai;

    String id, pendidikan, tempat_kuliah, tanggal_lahir, narasi, penghasilan, transkrip;

    private static final String TAG = "FormPersonalityActivity";
    public final static String TAG_ID = "ID";

    private static final String TAG_PESAN = "status";

    private int PICKFILE_REQUEST_CODE = 1;

    //Uri to store the image uri
    private Uri filePath;

    String pesan, filename, stringFilePath;
    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_personality);

        id = getIntent().getStringExtra(TAG_ID);
        pendidikan = getIntent().getStringExtra("pendidikan");
        tempat_kuliah = getIntent().getStringExtra("tempat_kuliah");
        tanggal_lahir = getIntent().getStringExtra("tanggal_lahir");

        et_narasi_kamu = findViewById(R.id.et_narasi_kamu);
        et_penghasilan_orangtua = findViewById(R.id.et_penghasilan_orangtua);
        et_transkrip_nilai = findViewById(R.id.et_transkrip_nilai);
        btn_selesai = findViewById(R.id.btn_selesai);

        et_transkrip_nilai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                //intent.putExtra("browseCoa", itemToBrowse);
                //Intent chooser = Intent.createChooser(intent, "Select a File to Upload");
                try {
                    //startActivityForResult(chooser, FILE_SELECT_CODE);
                    startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"),PICKFILE_REQUEST_CODE);
                } catch (Exception ex) {
                    System.out.println("browseClick :"+ex);//android.content.ActivityNotFoundException ex
                }
            }
        });

        btn_selesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                narasi = et_narasi_kamu.getText().toString();
                penghasilan = et_penghasilan_orangtua.getText().toString();
                transkrip = et_transkrip_nilai.getText().toString();

                if (!narasi.equals("") && !penghasilan.equals("") && !transkrip.equals("")){
                    uploadMultipart();
                }else{
                    Toast.makeText(getApplicationContext(), "Data Harus Diisi Semua", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadMultipart() {
        String url = Server.URL+"personality/create.php";
        narasi = et_narasi_kamu.getText().toString();
        penghasilan = et_penghasilan_orangtua.getText().toString();

        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();
            if(filePath != null){
                //Creating a multi part request
                new MultipartUploadRequest(this, uploadId, url)
                        .addFileToUpload(stringFilePath, "transkrip_nilai") //Adding file
                        .addParameter("user_id", id) //Adding text parameter to the request
                        .addParameter("pendidikan", pendidikan) //Adding text parameter to the request
                        .addParameter("tempat_kuliah", tempat_kuliah) //Adding text parameter to the request
                        .addParameter("tanggal_lahir", tanggal_lahir) //Adding text parameter to the request
                        .addParameter("narasi", narasi) //Adding text parameter to the request
                        .addParameter("penghasilan_orangtua", penghasilan) //Adding text parameter to the request
                        .setMaxRetries(2)
                        .startUpload(); //Starting the upload

                Toast.makeText(getApplicationContext(),
                        "user was updated.", Toast.LENGTH_LONG).show();

                Intent i = new Intent(FormPersonalityActivity.this, MainActivity.class);
                i.putExtra(TAG_ID, id);
                startActivity(i);
            }else {
                insertPersonality(id, pendidikan, tempat_kuliah, tanggal_lahir, narasi, penghasilan, transkrip);
            }

        } catch (Exception exc) {
            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void insertPersonality(final String id, final String pendidikan, final String tempat_kuliah, final String tanggal_lahir, final String narasi, final String penghasilan, final String transkrip) {
        String url = Server.URL+"personality/create.php";

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    pesan = jObj.getString(TAG_PESAN);

                    // Check for error node in json
                    if (pesan.equals("berhasil")) {
                        Intent i = new Intent(FormPersonalityActivity.this, MainActivity.class);
                        i.putExtra(TAG_ID, id);
                        startActivity(i);

                    } else {
                        Toast.makeText(getApplicationContext(),
                                jObj.getString("message"), Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", id);
                params.put("pendidikan", pendidikan);
                params.put("tempat_kuliah", tempat_kuliah);
                params.put("tanggal_lahir", tanggal_lahir);
                params.put("narasi", narasi);
                params.put("penghasilan_orangtua", penghasilan);
                params.put("transkrip_nilai", transkrip);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKFILE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    filePath = data.getData();

                    String mimeType = getContentResolver().getType(filePath);
                    if (mimeType == null) {
                        String path = getPath(this, filePath);
                        if (path == null) {
                            filename = FilenameUtils.getName(filePath.toString());
                        } else {
                            File file = new File(path);
                            filename = file.getName();
                            et_transkrip_nilai.setText(filename);
                            stringFilePath = path;
                        }
                    } else {
                        filePath = data.getData();
                        Cursor returnCursor = getContentResolver().query(filePath, null, null, null, null);
                        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                        returnCursor.moveToFirst();
                        filename = returnCursor.getString(nameIndex);
                        String size = Long.toString(returnCursor.getLong(sizeIndex));
                        et_transkrip_nilai.setText(filename);
                    }
                    File fileSave = getExternalFilesDir(null);
                    String sourcePath = getExternalFilesDir(null).toString();
                    stringFilePath = sourcePath + "/" + filename;
                    try {

                        copyFileStream(new File(sourcePath + "/" + filename), filePath,this);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void copyFileStream(File dest, Uri uri, Context context)
            throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;

            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is.close();
            os.close();
        }
    }
}
