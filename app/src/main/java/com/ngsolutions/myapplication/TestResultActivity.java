package com.ngsolutions.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class TestResultActivity extends AppCompatActivity {

    ImageView preview;
    double Lat, Long;
    Button analyseBtn,save,cancel;
    int Type;
    TextView soilType,OC,N,P,K;
    RequestQueue requestQueue;
    private final String url = "https://soil-health.herokuapp.com/post";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);
        preview = findViewById(R.id.previewImage);
        cancel = findViewById(R.id.cancelBtn);
        save = findViewById(R.id.saveBtn);

        Uri uri = (Uri) getIntent().getExtras().get("imageUri");
        preview.setImageURI(uri);


        Lat = getIntent().getExtras().getDouble("Lat");
        Long = getIntent().getExtras().getDouble("Long");
        Type = getIntent().getExtras().getInt("Type");

        soilType = findViewById(R.id.TypeAns);
        N = findViewById(R.id.nitrogenContentAns);
        P = findViewById(R.id.phosphorusAns);
        K = findViewById(R.id.potassiumAns);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,response -> Toast.makeText(this, "Success"+response, Toast.LENGTH_SHORT).show(),error -> {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Lat","20.594321");
                params.put("Long","74.248489");
                params.put("date","2022-07-01");
                params.put("end_dt","2022-07-30");
                return params;
            }
        };

        requestQueue = Volley.newRequestQueue(TestResultActivity.this);
        //requestQueue.add(stringRequest);




        analyseBtn = findViewById(R.id.AnalyzeBtn);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TestResultActivity.this, HomePage.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });
        analyseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DoAnalysis();
                analyseBtn.setEnabled(false);
                save.setEnabled(true);

            }
        });

    }

    private void DoAnalysis() {
        String[] classes = {"Clay Soil","Black Soil","Alluvial Soil", "Red Soil"};
        soilType.setText(classes[Type]);
    }
}