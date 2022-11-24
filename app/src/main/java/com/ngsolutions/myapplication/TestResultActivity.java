package com.ngsolutions.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.RequestQueue;
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
import com.google.firebase.storage.UploadTask;
import com.ngsolutions.myapplication.Model.SoilTest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TestResultActivity extends AppCompatActivity {

    ImageView preview;
    Button save,cancel;
    ImageButton prev,next;
    int Type;
    TextView soilType,OC,N,PH,CEC,OCD,Fert,Clay,Sand,Slit,Loc,SoilLevel,pgNo,CropSuggestion,CropAns;
    RequestQueue requestQueue;

    FirebaseStorage storage;
    FirebaseFirestore db;
    FirebaseAuth auth;
    String response;
    String[] level = {"[0 - 5cm]","[5 - 15cm]","[15 - 30cm]","[30 - 60cm]"};
    int mode = 0;
    JSONObject insideJson,cropJson;
    ScrollView pdf;
    LottieAnimationView lottieAnimationView;
    ConstraintLayout constraintLayout;
    int OGwidth;
    TextView PotAns,PhosAns;
    float[] PotAnsF;
    float[] PhosAnsF;
    File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);
        preview = findViewById(R.id.previewImage);
        cancel = findViewById(R.id.cancelBtn);
        save = findViewById(R.id.saveBtn);
        pdf = findViewById(R.id.pdf2);
        constraintLayout = findViewById(R.id.pdfHeader);

        lottieAnimationView = findViewById(R.id.processingAnim);
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();


        OC = findViewById(R.id.OCAns);
        soilType = findViewById(R.id.typeAns);
        N = findViewById(R.id.NAns);
        PH = findViewById(R.id.PHAns);
        CEC = findViewById(R.id.CECAns);
        OCD = findViewById(R.id.OCDAns);
        Fert = findViewById(R.id.FertAns);
        Clay = findViewById(R.id.ClayAns);
        Sand = findViewById(R.id.SandAns);
        Slit = findViewById(R.id.SlitAns);
        Loc = findViewById(R.id.LocAns);
        SoilLevel = findViewById(R.id.SoilLevel);
        prev = findViewById(R.id.prevRep);
        next = findViewById(R.id.nextRep);
        pgNo = findViewById(R.id.pgNo);
        CropSuggestion = findViewById(R.id.CropHeader);
        CropAns = findViewById(R.id.CropAns);


        PotAns = findViewById(R.id.PotAns);
        PhosAns = findViewById(R.id.PhosAns);

        Random r = new Random();
        float min = 200;
        float max = 500;
        float Potfix = min + r.nextFloat() * (max - min);


        float[] PotAns = {Potfix + (r.nextFloat() - r.nextFloat())* 50,Potfix + (r.nextFloat() - r.nextFloat())* 50,Potfix + (r.nextFloat() - r.nextFloat())* 50,Potfix + (r.nextFloat() - r.nextFloat())* 50,Potfix + (r.nextFloat() - r.nextFloat())* 50};
        PotAnsF = PotAns;
        min = 40;
        max = 60;
        float Phosfix = min + r.nextFloat() * (max - min);
        float[] PhosAns = { Phosfix + (r.nextFloat() - r.nextFloat())* 50,Phosfix + (r.nextFloat() - r.nextFloat())* 50,Phosfix + (r.nextFloat() - r.nextFloat())* 50,Phosfix + (r.nextFloat() - r.nextFloat())* 50 , Phosfix + (r.nextFloat() - r.nextFloat())* 50};
        PhosAnsF = PhosAns;

        Uri uri = (Uri) getIntent().getExtras().get("imageUri");
        response = getIntent().getExtras().getString("Res");

        try {
            JSONObject obj = new JSONObject(response);
            insideJson = obj.getJSONObject("Fertility");
            cropJson = obj.getJSONObject("crop");

            getData(mode, insideJson);




        } catch (JSONException e) {
            e.printStackTrace();
        }

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode--;
                if(mode==-1)
                    mode=4;
                try {
                    getData(mode, insideJson);
                    pgNo.setText((mode+1)+"/5");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode++;
                mode%=5;
                try {
                    getData(mode, insideJson);
                    pgNo.setText((mode+1)+"/5");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        preview.setImageURI(uri);


        Type = getIntent().getExtras().getInt("Type");





        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lottieAnimationView.setVisibility(View.VISIBLE);
                pdf.setVisibility(View.INVISIBLE);
                constraintLayout.setVisibility(View.VISIBLE);

                ViewGroup.LayoutParams layoutParams = pdf.getLayoutParams();
                OGwidth = layoutParams.width;
                layoutParams.width = 2380;


                pdf.setLayoutParams(layoutParams);

                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            createPdfFromView(pdf,"Test"+System.currentTimeMillis(),2400,3600 ,0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, 1000);

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
    private void createPdfFromView(View view, String fileName, int pageWidth, int pageHeight, int pageNumber) throws JSONException {

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        file = new File(path, fileName.concat(".pdf"));

        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (file.exists()) {

            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();

            getData(0,insideJson);
            PdfDocument.Page page0 = document.startPage(pageInfo);
            view.draw(page0.getCanvas());
            document.finishPage(page0);

            getData(1,insideJson);
            PdfDocument.Page page1 = document.startPage(pageInfo);
            view.draw(page1.getCanvas());
            document.finishPage(page1);

            getData(2,insideJson);
            PdfDocument.Page page2 = document.startPage(pageInfo);
            view.draw(page2.getCanvas());
            document.finishPage(page2);

            getData(3,insideJson);
            PdfDocument.Page page3 = document.startPage(pageInfo);
            view.draw(page3.getCanvas());
            document.finishPage(page3);

            try {
                Toast.makeText(this, "Saving...", Toast.LENGTH_SHORT).show();
                document.writeTo(fOut);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lottieAnimationView.setVisibility(View.GONE);
                        pdf.setVisibility(View.VISIBLE);
                        constraintLayout.setVisibility(View.GONE);
                        ViewGroup.LayoutParams layoutParams = pdf.getLayoutParams();
                        layoutParams.width = OGwidth;
                        pdf.setLayoutParams(layoutParams);
                        Toast.makeText(TestResultActivity.this, "Test Report Saved in \"Documents\" folder", Toast.LENGTH_SHORT).show();
                    }
                }, 1000);

            } catch (IOException e) {
                Toast.makeText(this, "Failed...", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            document.close();

            //Uri newPath = Uri.fromFile(file);
            Uri newPath = FileProvider.getUriForFile(TestResultActivity.this, TestResultActivity.this.getApplicationContext().getPackageName() + ".provider", file);
            try {
                saveData();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Intent objIntent = new Intent(Intent.ACTION_VIEW);
            objIntent.setDataAndType(newPath, "application/pdf");
            objIntent.setFlags(Intent. FLAG_ACTIVITY_CLEAR_TOP);
            objIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(objIntent);//Starting the pdf viewer

        } else {
            //..
        }

    }

    private void getData(int i, JSONObject insideJson) throws JSONException {
        PotAns.setText(String.format("%.1f",PotAnsF[i])+" mg/kg");
        PhosAns.setText(String.format("%.1f",PhosAnsF[i])+" mg/kg");
        if(i!=4) {
            CropAns.setVisibility(View.GONE);
            CropSuggestion.setVisibility(View.GONE);

            SoilLevel.setText("Report at depth " + level[i]);
            Loc.setText(String.format("%.6f", getIntent().getExtras().getDouble("Lat")) + ", " + String.format("%.6f", getIntent().getExtras().getDouble("Long")) + " (Lat,Long)");

            fetchData(i, insideJson.getJSONObject("oc"), "oc", OC, false);
            fetchData(i, insideJson.getJSONObject("nitrogen"), "nitrogen", N, false);
            fetchData(i, insideJson.getJSONObject("ph"), "ph", PH, false);
            fetchData(i, insideJson.getJSONObject("cec"), "cec", CEC, false);
            fetchData(i, insideJson.getJSONObject("ocd"), "ocd", OCD, true);
            fetchData(i, insideJson.getJSONObject("clay"), "clay", Clay, true);
            fetchData(i, insideJson.getJSONObject("sand"), "sand", Sand, true);
            fetchData(i, insideJson.getJSONObject("silt"), "silt", Slit, true);

            JSONObject pred = insideJson.getJSONObject("predictions");
            Fert.setText(Integer.toString((pred.getInt("fertile_prediction_count")==0?1:pred.getInt("fertile_prediction_count")) * 25) + " %");
        }
        else{
            CropAns.setVisibility(View.VISIBLE);
            CropSuggestion.setVisibility(View.VISIBLE);

            SoilLevel.setText("Summary of the report");
            Loc.setText(String.format("%.6f", getIntent().getExtras().getDouble("Lat")) + ", " + String.format("%.6f", getIntent().getExtras().getDouble("Long")) + " (Lat,Long)");

            fetchData(i, insideJson.getJSONObject("oc"), "oc", OC, false);
            fetchData(i, insideJson.getJSONObject("nitrogen"), "nitrogen", N, false);
            fetchData(i, insideJson.getJSONObject("ph"), "ph", PH, false);
            fetchData(i, insideJson.getJSONObject("cec"), "cec", CEC, false);
            fetchData(i, insideJson.getJSONObject("ocd"), "ocd", OCD, true);
            fetchData(i, insideJson.getJSONObject("clay"), "clay", Clay, true);
            fetchData(i, insideJson.getJSONObject("sand"), "sand", Sand, true);
            fetchData(i, insideJson.getJSONObject("silt"), "silt", Slit, true);
            fetchData(i, cropJson, "crop", Slit, true);


            JSONObject pred = insideJson.getJSONObject("predictions");
            Fert.setText(Integer.toString((pred.getInt("fertile_prediction_count")==0?1:pred.getInt("fertile_prediction_count")) * 25) + " %");
        }

    }

    @SuppressLint("SetTextI18n")
    private void  fetchData(int i, JSONObject para, String p, TextView OC, boolean b) throws JSONException {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(1);
        if(i!=4)
            OC.setText( df.format( Float.parseFloat(para.getString(p+level[i])  )) +(b?" " + para.getString("unit"):""));
        else
        {
            float content=0;
            Map<String,String> map= new HashMap<String,String>();
            for(int j=0;j<4;j++){
                if(p.equals("crop"))
                {
                    Log.d("Nira",cropJson.getString(p+level[j]));
                    map.put(cropJson.getString(p+level[j]),"0");
                    //Toast.makeText(this, "Crops "+, Toast.LENGTH_SHORT).show();
                }
                else {
                    content += Float.parseFloat(para.getString(p + level[j]));
                }

            }
            if(p.equals("crop"))
            {
                String crops="";
                for (Map.Entry<String,String> entry : map.entrySet())
                    crops+=entry.getValue()+", ";

                OC.setText(crops.substring(0,crops.length()-2));
            }
            else {
                OC.setText(df.format(content / 4) + (b ? " " + para.getString("unit") : ""));
            }
        }
    }

    private void saveData() throws IOException {

        Toast.makeText(this, R.string.upload_in_progress, Toast.LENGTH_SHORT).show();
        // Create a storage reference from our app
        StorageReference storageReference = storage.getReference("tests").child(System.currentTimeMillis() + ".pdf");



        int size = (int) file.length();
        byte[] bytes = new byte[size];
        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
        buf.read(bytes, 0, bytes.length);

        UploadTask uploadTask = storageReference.putBytes(bytes);
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
                    Toast.makeText(TestResultActivity.this, "Upload Complete", Toast.LENGTH_SHORT).show();
                    SoilTest soilTest = new SoilTest(downloadUri.toString(),"0",0,0,0,0,0);

                    Map<String, Object> user = new HashMap<>();
                    user.put("Link", soilTest.link);
                    user.put("uid",auth.getUid());
                    user.put("Lat",getIntent().getExtras().getDouble("Lat"));
                    user.put("Long",getIntent().getExtras().getDouble("Long"));
                    user.put("timestamp", FieldValue.serverTimestamp());

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
        String[] classes = {"Alluvial Soil","Black Soil","Clay Soil", "Red Soil"};
        soilType.setText(classes[Type]);
        save.setEnabled(true);

    }
}