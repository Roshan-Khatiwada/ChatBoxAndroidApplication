package com.example.chatbox;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class login_OTP extends AppCompatActivity {

    private EditText OTPEditText;
    private Button verifyOTP_btn;
    private ProgressBar login_progressBar;
    private String verificationId;
    private String phoneNumber;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    PhoneAuthProvider.ForceResendingToken resendingToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);

        OTPEditText = findViewById(R.id.otp);
        verifyOTP_btn = findViewById(R.id.verifyOTP_btn);
        login_progressBar = findViewById(R.id.loginProgressBar);
        phoneNumber = getIntent().getStringExtra("phoneNumber").toString();
        sendOTP(phoneNumber);

        verifyOTP_btn.setOnClickListener(View ->{
            String enteredOTP = OTPEditText.getText().toString().trim();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,enteredOTP);
            verifyOTP(credential);
        });
    }

    private void verifyOTP(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(login_OTP.this, "OTP verification successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(login_OTP.this, login_userName.class);
                    intent.putExtra("phoneNumber",phoneNumber);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(), "OTP verification failed", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void sendOTP(String phoneNumber) {
        PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+977" + phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        login(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(getApplicationContext(), "OTP verification failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationId = s;
                        resendingToken = forceResendingToken;
                    }
                });
        PhoneAuthProvider.verifyPhoneNumber(builder.build());


    }

    void login(PhoneAuthCredential phoneCredential) {

        mAuth.signInWithCredential(phoneCredential).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                Intent intent = new Intent(login_OTP.this, login_userName.class);
                intent.putExtra("phone", phoneNumber);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Verification failed", Toast.LENGTH_LONG).show();
            }
        });
    }


}
