package com.ngsolutions.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SendOTPActivity extends AppCompatActivity {
    ImageButton backButton;
    Button sendOTPButton;
    EditText countryCodeText, mobileNumberText;
    TextView counter;
    LottieAnimationView progressBar;
    String mobileNumberString;
    FirebaseAuth Auth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;
    long DelayToSwitch = 8000;
    long mTime = DelayToSwitch+1000;
    CountDownTimer mCountDown;
    boolean detectedOTP = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_o_t_p);
        loadLocale();
        backButton = findViewById(R.id.BackBtn);
        sendOTPButton = findViewById(R.id.SendOTPBtn);
        countryCodeText = findViewById(R.id.CountryCodeField);
        mobileNumberText = findViewById(R.id.MobileNumberField);
        progressBar = findViewById(R.id.ProgressBar1);
        counter = findViewById(R.id.Counter);
        Auth = FirebaseAuth.getInstance();
        progressBar.setVisibility(View.GONE);


        //Intent verifyIntent = new Intent(SendOTPActivity.this,VerifyOTPActivity.class);
        //startActivity(verifyIntent);


        //back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //send otp
        sendOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mobileNumberText.getText().toString().trim().isEmpty() || countryCodeText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SendOTPActivity.this, getString(R.string.enter_mobile), Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                sendOTPButton.setVisibility(View.INVISIBLE);
                mobileNumberString = countryCodeText.getText().toString()+mobileNumberText.getText().toString();
                PhoneAuthOptions options = PhoneAuthOptions.newBuilder(Auth)
                        .setPhoneNumber(mobileNumberString)
                        .setTimeout(DelayToSwitch/1000,TimeUnit.SECONDS)
                        .setActivity(SendOTPActivity.this)
                        .setCallbacks(mCallBacks)
                        .build();
                PhoneAuthProvider.verifyPhoneNumber(options);
            }
        });

        mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signIn(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(SendOTPActivity.this, e.getMessage() , Toast.LENGTH_SHORT).show();
                sendOTPButton.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCodeSent(@NonNull String verificationCode, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationCode, forceResendingToken);
                Toast.makeText(SendOTPActivity.this, getString(R.string.otp_sent), Toast.LENGTH_SHORT).show();
                startTimer();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(detectedOTP)
                            return;
                        Intent verifyIntent = new Intent(SendOTPActivity.this,VerifyOTPActivity.class);
                        verifyIntent.putExtra("auth",verificationCode);
                        verifyIntent.putExtra("mobile",mobileNumberString);
                        startActivity(verifyIntent);
                        finish();
                    }
                },DelayToSwitch);

            }
        };
    }

    private  void startTimer()
    {
        mCountDown = new CountDownTimer(mTime,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTime = millisUntilFinished;
                counter.setText(getString(R.string.waiting_for_otp) +mTime/1000 +getString(R.string.seconds));
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    private void sendToHome() {
        Intent intent = new Intent(SendOTPActivity.this,HomePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        sendOTPButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        detectedOTP = true;
        finish();
    }
    private void signIn(PhoneAuthCredential credential){
        Auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    sendToHome();
                }
                else
                {
                    Toast.makeText(SendOTPActivity.this, task.getException().getMessage() , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void setLocale(String lang)
    {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("My_lang",lang);
        editor.apply();
    }
    private  void loadLocale()
    {
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String lang = prefs.getString("My_lang","en");
        setLocale(lang);
    }
}