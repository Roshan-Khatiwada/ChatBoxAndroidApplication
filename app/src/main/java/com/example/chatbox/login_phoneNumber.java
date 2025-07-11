package com.example.chatbox;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

// Activity for user phone number verification
public class login_phoneNumber extends AppCompatActivity {

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    EditText phoneNumberEditText;
    Button sendOTPBtn;
    Spinner regionSpinner;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    String verificationId;
    String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone_number);

        mAuth = FirebaseAuth.getInstance();

        phoneNumberEditText = findViewById(R.id.phoneNumber);
        sendOTPBtn = findViewById(R.id.sendOTPbtn);
        regionSpinner = findViewById(R.id.regionSpinner);
        progressBar = findViewById(R.id.loginProgressBar);

        progressBar.setVisibility(View.GONE);

        // Set up the spinner for selecting region
        setupRegionSpinner();

        sendOTPBtn.setOnClickListener(view -> {
            if (validatePhoneNumber()) {
                // If phone number is valid, proceed to OTP verification activity
                Intent intent = new Intent(login_phoneNumber.this, login_OTP.class);
                intent.putExtra("phoneNumber", phoneNumberEditText.getText().toString().trim());
                startActivity(intent);
            }
        });
    }

    // Set up the spinner with regions
    private void setupRegionSpinner() {
        String[] regions = {"NP"}; // Add other regions as needed
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, regions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        regionSpinner.setAdapter(adapter);
    }

    // Validate the entered phone number
    private boolean validatePhoneNumber() {
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String selectedRegion = regionSpinner.getSelectedItem().toString();

        // Validate the phone number using a custom validator class
        if (PhoneNumberValidator.isValidPhoneNumber(phoneNumber, selectedRegion)) {
            showToast("Valid phone number");
            return true;
        } else {
            showToast("Invalid phone number");
            return false;
        }
    }

    // Show a toast message
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
