package com.ngsolutions.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.ngsolutions.myapplication.Model.SoilTest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class TestResultActivity extends AppCompatActivity {

    ImageView preview;
    double Lat, Long;
    Button save,cancel;
    int Type;
    TextView soilType,OC,N,P,K;
    RequestQueue requestQueue;


    FirebaseStorage storage;
    FirebaseFirestore db;
    FirebaseAuth auth;

    int mode;
    private final String url = "https://soil-health.herokuapp.com/post";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);
        preview = findViewById(R.id.previewImage);
        cancel = findViewById(R.id.cancelBtn);
        save = findViewById(R.id.saveBtn);
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();


        Uri uri = (Uri) getIntent().getExtras().get("imageUri");
        preview.setImageURI(uri);

        mode = getIntent().getExtras().getInt("Mode");
        //Toast.makeText(this, "Mode"+mode, Toast.LENGTH_SHORT).show();
        Type = getIntent().getExtras().getInt("Type");

        soilType = findViewById(R.id.TypeAns);
        N = findViewById(R.id.nitrogenContentAns);
        P = findViewById(R.id.phosphorusAns);
        K = findViewById(R.id.potassiumAns);
        if(mode==1) {
            Lat = getIntent().getExtras().getDouble("Lat", 0);
            Long = getIntent().getExtras().getDouble("Long", 0);

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = "https://soil-health.herokuapp.com/post?Lat=" + Lat + "&Long=" + Long + "&date=2022-07-01&end_dt=2022-07-30";
            JSONObject jsonBody = new JSONObject();

            final String mRequestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("LOG_VOLLEY", response);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("LOG_VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        return null;
                    }
                }
            };

            requestQueue.add(stringRequest);
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TestResultActivity.this, HomePage.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });
        DoAnalysis();

    }

    private void saveData() {
        save.setEnabled(false);
        Toast.makeText(this, R.string.upload_in_progress, Toast.LENGTH_SHORT).show();
        // Create a storage reference from our app
        StorageReference storageReference = storage.getReference("tests").child(System.currentTimeMillis() + ".jpeg");
        preview.setDrawingCacheEnabled(true);
        preview.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) preview.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageReference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(TestResultActivity.this, R.string.image_upload_failed, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    SoilTest soilTest = new SoilTest(downloadUri.toString(),"0",0,0,0,0,0);

                    Map<String, Object> user = new HashMap<>();
                    user.put("Link", soilTest.link);
                    user.put("uid",auth.getUid());
                    user.put("timestamp", FieldValue.serverTimestamp());
                    user.put("oc", OC.getText().toString());
                    user.put("n", N.getText().toString());
                    user.put("p", P.getText().toString());
                    user.put("k", K.getText().toString());


                    db.collection("TestReports")
                            .add(user)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(TestResultActivity.this, R.string.report_saved, Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(TestResultActivity.this, R.string.error_savig_report, Toast.LENGTH_SHORT).show();
                                }
                            });

                } else {
                    // Handle failures
                    // ...
                }
            }
        });


    }

    private void DoAnalysis() {
        String[] classes = {"Clay Soil","Black Soil","Alluvial Soil", "Red Soil"};
        soilType.setText(classes[Type]);
        if(mode==0)
        {
            save.setEnabled(true);
        }
    }
}