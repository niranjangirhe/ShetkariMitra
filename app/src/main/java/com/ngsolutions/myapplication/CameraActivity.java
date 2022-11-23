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

public class CameraActivity extends AppCompatActivity {

    Button capture;
    ImageView image;
    Button backBtn, nextBtn;
    LottieAnimationView lottieAnimationView;
    private final static int imageSize = 244;
    double Lat, Long;
    Uri imageUri;
    int Type;
    int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        capture = findViewById(R.id.CaptureBtn);
        image = findViewById(R.id.cameraPreview);
        lottieAnimationView = findViewById(R.id.soilAnimation);
        backBtn = findViewById(R.id.soilBack2Btn);
        nextBtn = findViewById(R.id.soilNext2Btn);

        mode = getIntent().getExtras().getInt("Mode");
        //Toast.makeText(this, "Mode"+mode, Toast.LENGTH_SHORT).show();
        if(mode==1) {
            Lat = getIntent().getExtras().getDouble("Lat");
            Long = getIntent().getExtras().getDouble("Long");
        }

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mode==1) {
                    Intent i = new Intent(CameraActivity.this, TestResultActivity.class);
                    i.putExtra("Lat", Lat);
                    i.putExtra("Long", Long);
                    i.putExtra("imageUri",imageUri);
                    i.putExtra("Type",Type);
                    i.putExtra("Mode",mode);
                    startActivity(i);
                }
                else{
                    Intent i = new Intent(CameraActivity.this, EmptyTestResultActivity.class);
                    i.putExtra("imageUri",imageUri);
                    i.putExtra("Type",Type);
                    i.putExtra("Mode",mode);
                    startActivity(i);
                }

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

                imageBitmap = Bitmap.createScaledBitmap(imageBitmap,224,224,false);
                classifyImage(imageBitmap);
            }
            catch (Exception e)
            {
                Toast.makeText(this, "Error in retry", Toast.LENGTH_SHORT).show();
            }
            image.setImageURI(imageUri);
            nextBtn.setEnabled(true);
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
            SoilNet model = SoilNet.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 244, 244, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;
            //iterate over each pixel and extract R, G, and B values. Add those values individually to the byte buffer.
            for(int i = 0; i < imageSize; i ++){
                for(int j = 0; j < imageSize; j++){
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 1));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            SoilNet.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            // find the index of the class with the biggest confidence.
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            //String[] classes = {"Clay_Soil","Black_Soil","ALLUVIAL_Soil", "Red Soil"};
           //Toast.makeText(this, "Soitl type : "+classes[maxPos], Toast.LENGTH_SHORT).show();
            Type = maxPos;

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }


}
