package com.ngsolutions.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class VerifyOTPActivity extends AppCompatActivity {

    EditText inputCode1,inputCode2,inputCode3,inputCode4,inputCode5,inputCode6;
    ImageButton backButton;
    Button verifyOTPButton;
    ProgressBar progressBar;
    String verificationId,mobileNumber;
    TextView mobileDisplay,resendOTPButton;
    FirebaseAuth Auth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;
    boolean resendHit;
    long DelayToSwitch = 18000;
    long mTime = DelayToSwitch+1000;
    CountDownTimer mCountDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_o_t_p);
        loadLocale();
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String lang = prefs.getString("My_lang","");
        inputCode1 = findViewById(R.id.Code1Field);
        inputCode2 = findViewById(R.id.Code2Field);
        inputCode3 = findViewById(R.id.Code3Field);
        inputCode4 = findViewById(R.id.Code4Field);
        inputCode5 = findViewById(R.id.Code5Field);
        inputCode6 = findViewById(R.id.Code6Field);
        progressBar = findViewById(R.id.ProgressBar2);
        verifyOTPButton = findViewById(R.id.VerifyBtn);
        mobileDisplay = findViewById(R.id.MobieNumberDisplayField);
        backButton = findViewById(R.id.backBtn2);
        resendOTPButton = findViewById(R.id.ResendBtn);
        Auth = FirebaseAuth.getInstance();

        //Max length of OTP field is 1
        InputFilter[] inputFilters = new InputFilter[1];
        inputFilters[0] = new InputFilter.LengthFilter(1);
        inputCode1.setFilters(inputFilters);
        inputCode2.setFilters(inputFilters);
        inputCode3.setFilters(inputFilters);
        inputCode4.setFilters(inputFilters);
        inputCode5.setFilters(inputFilters);
        inputCode6.setFilters(inputFilters);
        verificationId = getIntent().getStringExtra("auth");
        mobileNumber = getIntent().getStringExtra("mobile");

        if(lang.contains("en"))
            mobileDisplay.setText(getString(R.string.OTP_sent)+mobileNumber);
        else
            mobileDisplay.setText(mobileNumber+getString(R.string.OTP_sent));
        joinInputCode();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentBack = new Intent(VerifyOTPActivity.this,SendOTPActivity.class);
                intentBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                
                startActivity(intentBack);
                finish();
            }
        });

        progressBar.setVisibility(View.GONE);
        verifyOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputCode1.getText().toString().trim().isEmpty() ||
                        inputCode2.getText().toString().trim().isEmpty() ||
                        inputCode3.getText().toString().trim().isEmpty() ||
                        inputCode4.getText().toString().trim().isEmpty() ||
                        inputCode5.getText().toString().trim().isEmpty() ||
                        inputCode6.getText().toString().trim().isEmpty()) {
                    Toast.makeText(VerifyOTPActivity.this, getString(R.string.please_enter_valid_otp), Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                verifyOTPButton.setVisibility(View.INVISIBLE);
                String code = inputCode1.getText().toString() +
                        inputCode2.getText().toString() +
                        inputCode3.getText().toString() +
                        inputCode4.getText().toString() +
                        inputCode5.getText().toString() +
                        inputCode6.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,code);
                signIn(credential);
            }
        });

        resendOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(resendHit) {
                    if(lang.contains("en"))
                        Toast.makeText(VerifyOTPActivity.this, getString(R.string.Retry_after)+mTime/1000+getString(R.string.seconds), Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(VerifyOTPActivity.this, mTime/1000+getString(R.string.seconds)+getString(R.string.Retry_after), Toast.LENGTH_SHORT).show();
                    return;
                }
                resendHit=true;
                PhoneAuthOptions options = PhoneAuthOptions.newBuilder(Auth)
                        .setPhoneNumber(mobileNumber)
                        .setTimeout( DelayToSwitch/1000,TimeUnit.SECONDS)
                        .setActivity(VerifyOTPActivity.this)
                        .setCallbacks(mCallBacks)
                        .build();
                    progressBar.setVisibility(View.VISIBLE);
                    verifyOTPButton.setVisibility(View.INVISIBLE);
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
                Toast.makeText(VerifyOTPActivity.this, getString(R.string.Wrong_otp), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                verifyOTPButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCodeSent(@NonNull String verificationCode, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationCode, forceResendingToken);
                Toast.makeText(VerifyOTPActivity.this, getString(R.string.OTP_Resent), Toast.LENGTH_SHORT).show();
                verificationId = verificationCode;
                progressBar.setVisibility(View.GONE);
                verifyOTPButton.setVisibility(View.VISIBLE);
                mTime = DelayToSwitch+1000;
                startTimer();
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                resendHit=false;
            }
        };
    }

    private void joinInputCode() {
        inputCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!inputCode1.getText().toString().trim().isEmpty())
                    inputCode2.requestFocus();

            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        inputCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!inputCode2.getText().toString().trim().isEmpty()) {
                    inputCode3.requestFocus();
                }
                else
                {
                    inputCode1.requestFocus();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!inputCode3.getText().toString().trim().isEmpty()){
                    inputCode4.requestFocus();
                }
                else
                {
                    inputCode2.requestFocus();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!inputCode4.getText().toString().trim().isEmpty()) {
                    inputCode5.requestFocus();
                }
                else
                {
                    inputCode3.requestFocus();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!inputCode5.getText().toString().trim().isEmpty()) {
                    inputCode6.requestFocus();
                }
                else
                {
                    inputCode4.requestFocus();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(inputCode6.getText().toString().trim().isEmpty())
                    inputCode5.requestFocus();
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private void sendToHome() {
        Intent intent = new Intent(VerifyOTPActivity.this,HomePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        verifyOTPButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
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
                    Toast.makeText(VerifyOTPActivity.this, getString(R.string.invalid_otp) , Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
                verifyOTPButton.setVisibility(View.VISIBLE);
            }
        });
    }
    private  void startTimer()
    {
        mCountDown = new CountDownTimer(mTime,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTime = millisUntilFinished;
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = Auth.getCurrentUser();
        if(user!=null){
            sendToHome();
        }
    }
    @Override
    public void onBackPressed() {
        Intent intentBack = new Intent(VerifyOTPActivity.this,SendOTPActivity.class);
        intentBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentBack);
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