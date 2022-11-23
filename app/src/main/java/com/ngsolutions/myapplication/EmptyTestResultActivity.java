package com.ngsolutions.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ngsolutions.myapplication.Model.SoilFacts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class EmptyTestResultActivity extends AppCompatActivity {

    ImageView preview;
    Button save,cancel;
    TextView soilType,H1,H2,H3,H4,H5,A1,A2,A3,A4,A5;
    int Type;
    ScrollView pdf;
    Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_test_result);
        preview = findViewById(R.id.previewImage1);
        cancel = findViewById(R.id.cancelBtn1);
        save = findViewById(R.id.saveBtn1);

        uri = (Uri) getIntent().getExtras().get("imageUri");
        preview.setImageURI(uri);
        Type = getIntent().getExtras().getInt("Type");

        soilType = findViewById(R.id.TypeAns1);
        H1 = findViewById(R.id.H1);
        H2 = findViewById(R.id.H2);
        H3 = findViewById(R.id.H3);
        H4 = findViewById(R.id.H4);
        H5 = findViewById(R.id.H5);

        A1 = findViewById(R.id.A1);
        A2 = findViewById(R.id.A2);
        A3 = findViewById(R.id.A3);
        A4 = findViewById(R.id.A4);
        A5 = findViewById(R.id.A5);






        String[] classes = {"Clay Soil","Black Soil","Alluvial Soil", "Red Soil"};

        //Data Saving
        SoilFacts[] soilFacts = {
                new SoilFacts("Clay Soil","Suggested Crops","Moiture and Temperature","pH Level","Improvements and Fertilizers","Other Information","Rice, Lettuce, Chard, Broccoli, Cabbage, Snap Beans, Apple trees, Hydrangeas, Lavender, Peonys, Roses, Sunflowers, Turfgrass","45 to 55% for clay soils. clay has high water retention and poor drainage.\nTemperature: 27.7℃ to 28.9℃" ,"Clay soil is alkaline with pH level between 8-10.","To improve your soil, you'll need to add 6 to 8 inches of organic matter to the entire bed.\n" +
                        "You can add any type of organic matter.\n" +
                        "Clay soils are best improved with the addition of compost and other organic materials only.\n\n" +
                        "Fertilizer Suggestions:\n" +
                        "We recommend using natural fertilizers like manure, compost, or peat moss \n" +
                        "instead of a granulated or liquid fertilizer, which will not effect the soil texture. These organic materials will loosen up the soil and add vital nutrients to help plants thrive.","Rich in organic matter. It is also not good for many plants.\n" +
                        "It is only good for crops like paddy, which require a lot of water. It is used for making toys, pots, and many other purposes. Clay, because of its density, retains moisture well. It also tends to be more nutrient-rich than other soil types."),
                new SoilFacts("Black Soil","Suggested Crops","pH Levels","Chemical","Moisture","Problems and Deficiencies","cotton, wheat, jowar, linseed, virginia tobacco, castor, sunflower and millets.\n\nRice and sugarcane where irrigation facilities are available.","pH of Black Soil ranges between 7.2 - 8.5.","10 per cent of alumina,\n" +
                        "9-10 per cent of iron oxide,\n" +
                        "6-8 per cent of lime and magnesium carbonates,\n" +
                        "Potash is variable (less than 0.5 per cent) and Phosphates, nitrogen and humus are low.\n" +
                        "Reach in Iron, Magnesium,  Aluminum and potash and calcium\n" +
                        "soils are with more cation exchange capacity (40-60 m.e./100 g).","Water retention of about 150 - 250 mm/m.","Suffers from moisture stress during drought. Poor in Nitrogen, Potassium, Phosphorus and Humus. Poor drainage and water logging during rainfall. Optimum conditions for tillage occur immediately after harvesting when the surface soil is still moist."),
                new SoilFacts("Alluvial Soil","Suggested Crops","pH Levels","Chemical Properties","Moisture Content","Fertility","They yield splendid crops of rice, wheat, sugarcane, tobacco, cotton, jute, maize, oilseeds, vegetables and fruits.","The pH is 7.5 to 8.0.","The proportion of nitrogen is generally low. Potash, phosphoric acid and alkalies are adequate. Iron oxide and lime vary within a wide range. And also Poor in phosphorous.","Natural moisture content of Alluvial Soil 17.98 to 19.65%.", "It is extremely fertile because it is formed by the sediments transported by rivers and is a mixture of sand, clay and silt.\n" +
                        "It has a loamy texture and it is rich in humus.\n" +
                        "It has good water retention and water absorbing capacity."),
                new SoilFacts("Red Soil","Suggested Crops","Deficiency","Temperature","pH Level","Improvements","Cotton, Wheat, Pulses, Millets, OilSeeds, Potatoes, Rice,  Sugarcane , Bananas", "Lime, phosphate, manganese, nitrogen, humus and potash, phosphoric acid, magnesium","Generally develops in warm, temperate, and humid climate. So, the temperature is high here.","Natural pH of Red Soil\nAcidic - 3.5 - 6.5\n\nIn higher rainfall areas:\nAcidic to Neutral - 5 to 7.\n\nWhile in drier areas:\nSlightly Acidic to Neutral - 6.5 to 7.\n\nThey are acidic mainly due to the nature of the parent rocks. The alkali content is fair. They are fairly rich in potassium","1. Crop rotation\n2. Apply organic manure\n3. Use Nutrient Application Technique")};



        soilType.setText(soilFacts[Type].getType());
        H1.setText(soilFacts[Type].getH1());
        H2.setText(soilFacts[Type].getH2());
        H3.setText(soilFacts[Type].getH3());
        H4.setText(soilFacts[Type].getH4());
        H5.setText(soilFacts[Type].getH5());
        A1.setText(soilFacts[Type].getA1());
        A2.setText(soilFacts[Type].getA2());
        A3.setText(soilFacts[Type].getA3());
        A4.setText(soilFacts[Type].getA4());
        A5.setText(soilFacts[Type].getA5());



        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
//                ViewGroup.LayoutParams layoutParams = pdf.getLayoutParams();
//                layoutParams.width = 2300;
//
//
//                pdf.setLayoutParams(layoutParams);
//                pdf.setVisibility(View.INVISIBLE);
//                final Handler handler = new Handler(Looper.getMainLooper());
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        createPdfFromView(pdf,"Test"+System.currentTimeMillis(),2480,4008 ,0);
//                    }
//                }, 1000);

            }
        });

        pdf=findViewById(R.id.pdfViewer);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ViewGroup.LayoutParams
                //createPdfFromView(pdf,"Test"+System.currentTimeMillis(),2480,4008 ,0);
                Intent i = new Intent(EmptyTestResultActivity.this, TestResultActivity.class);

                i.putExtra("imageUri",uri);
                i.putExtra("Type",Type);
                i.putExtra("Mode",0);
                startActivity(i);
            }
        });
    }
    private void createPdfFromView(View view, String fileName, int pageWidth, int pageHeight, int pageNumber) {

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(path, fileName.concat(".pdf"));

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
            PdfDocument.Page page = document.startPage(pageInfo);

            view.draw(page.getCanvas());

            document.finishPage(page);

            try {
                Toast.makeText(this, "Saving...", Toast.LENGTH_SHORT).show();
                document.writeTo(fOut);
            } catch (IOException e) {
                Toast.makeText(this, "Failed...", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            document.close();

            /*Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);*/

        } else {
            //..
        }

    }
}