package com.ngsolutions.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.ngsolutions.myapplication.ml.SoilNet;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class CameraActivity extends AppCompatActivity {

    float ThresholdVal = 0.60f;
    Button capture;
    ImageView image;
    Button backBtn, nextBtn;
    LottieAnimationView lottieAnimationView;
    private final static int imageSize = 224;

    Uri imageUri;
    int Type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        capture = findViewById(R.id.CaptureBtn);
        image = findViewById(R.id.cameraPreview);
        lottieAnimationView = findViewById(R.id.soilAnimation);
        backBtn = findViewById(R.id.soilBack2Btn);
        nextBtn = findViewById(R.id.soilNext2Btn);


//        Lat = getIntent().getExtras().getDouble("Lat");
//        Long = getIntent().getExtras().getDouble("Long");


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(CameraActivity.this, EmptyTestResultActivity.class);
//                i.putExtra("Lat", Lat);
//                i.putExtra("Long", Long);
                i.putExtra("imageUri",imageUri);
                i.putExtra("Type",Type);
                startActivity(i);


            }
        });

        //Toast.makeText(this, "Location "+Lat+" "+Long, Toast.LENGTH_SHORT).show();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextBtn.setEnabled(false);
                ImagePicker.with(CameraActivity.this)
                        .crop(1f,1f)	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            imageUri = data.getData();
            try {
                Bitmap imageBitmap = (Bitmap) MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                int dimension = imageBitmap.getHeight();
                imageBitmap = ThumbnailUtils.extractThumbnail(imageBitmap,dimension,dimension);

                imageBitmap = Bitmap.createScaledBitmap(imageBitmap,imageSize,imageSize,false);
                classifyImage(imageBitmap);
            }
            catch (Exception e)
            {
                Toast.makeText(this, "Error in retry", Toast.LENGTH_SHORT).show();
            }
            image.setImageURI(imageUri);

            lottieAnimationView.setVisibility(View.GONE);
            image.getLayoutParams().height = image.getWidth();
            image.requestLayout();

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    public void classifyImage(Bitmap image){
        try {
            SoilNet model1 = SoilNet.newInstance(getApplicationContext());


            // Creates inputs for reference.
            TensorBuffer inputFeature01 = TensorBuffer.createFixedSize(new int[]{1, 244, 244, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer1 = ByteBuffer.allocateDirect(4 * 244 * 244 * 3);
            byteBuffer1.order(ByteOrder.nativeOrder());

            int[] intValues1 = new int[244 * 244];
            image.getPixels(intValues1, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel1 = 0;
            //iterate over each pixel and extract R, G, and B values. Add those values individually to the byte buffer.
            for(int i = 0; i < 244; i ++){
                for(int j = 0; j < 244; j++){
                    int val = intValues1[pixel1++]; // RGB
                    byteBuffer1.putFloat(((val >> 16) & 0xFF) * (1.f / 1));
                    byteBuffer1.putFloat(((val >> 8) & 0xFF) * (1.f / 1));
                    byteBuffer1.putFloat((val & 0xFF) * (1.f / 1));
                }
            }

            inputFeature01.loadBuffer(byteBuffer1);

            // Runs model inference and gets result.
            SoilNet.Outputs outputs1 = model1.process(inputFeature01);
            TensorBuffer outputFeature01 = outputs1.getOutputFeature0AsTensorBuffer();

            float[] confidences1 = outputFeature01.getFloatArray();
            //Toast.makeText(this, "Confidence "+ Arrays.toString(confidences) , Toast.LENGTH_SHORT).show();
            // find the index of the class with the biggest confidence.
            int maxPos1 = 0;
            float maxConfidence1 = 0;
            for (int i = 0; i < confidences1.length; i++) {
                if (confidences1[i] > maxConfidence1) {
                    maxConfidence1 = confidences1[i];
                    maxPos1 = i;
                }
            }
            nextBtn.setEnabled(true);
            //Toast.makeText(this, "Confident "+confidences[maxPos], Toast.LENGTH_SHORT).show();

            //String[] classes = {"Clay_Soil","Black_Soil","ALLUVIAL_Soil", "Red Soil"};
            //Toast.makeText(this, "Soitl type : "+classes[maxPos], Toast.LENGTH_SHORT).show();

            Type = maxPos1;


            // Releases model resources if no longer used.
            model1.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }


}
