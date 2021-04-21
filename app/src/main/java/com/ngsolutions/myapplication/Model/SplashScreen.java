package com.ngsolutions.myapplication.Model;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.ngsolutions.myapplication.HomePage;
import com.ngsolutions.myapplication.IntroductoryActivity;
import com.ngsolutions.myapplication.MainActivity;

public class SplashScreen extends AppCompatActivity {
    FirebaseAuth Auth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*Auth = FirebaseAuth.getInstance();
        FirebaseUser user = Auth.getCurrentUser();


        if (user != null) {
            sendToHome();
        } else {
            sendToMain();
        }*/
        sendToIntroductroy();
    }

    private void sendToIntroductroy() {
        Intent intent = new Intent(SplashScreen.this, IntroductoryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void sendToHome() {
        Intent intent = new Intent(SplashScreen.this, HomePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    private void sendToMain() {
        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
