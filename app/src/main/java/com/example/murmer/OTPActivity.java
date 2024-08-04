package com.example.murmer;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.murmer.utils.AndroidUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {

    String phone;
    EditText otpinput;
    Button nextbtn;
    ProgressBar progressBar;
    TextView resendotp;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Long timeoutSec = 30L;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otpactivity);

        otpinput = findViewById(R.id.otp_sent);
        nextbtn = findViewById(R.id.login_btn);
        progressBar = findViewById(R.id.progressbarwow);
        resendotp = findViewById(R.id.resend_otp);

        phone = getIntent().getStringExtra("phone");

        sendOTP(phone, false);

        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String entered_otp = otpinput.getText().toString().trim();
                if (entered_otp.isEmpty() || entered_otp.length() < 6) {
                    AndroidUtil.showToast(getApplicationContext(), "Please enter a valid 6-digit OTP");
                    return;
                }
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, entered_otp);
                signIn(credential);
                setinprogress(true);
            }
        });

        resendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendOTP(phone, true);
            }
        });
    }

    void sendOTP(String phone, boolean isResend) {
        startResendTimer();
        setinprogress(true);
        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(timeoutSec, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signIn(phoneAuthCredential);
                                setinprogress(false);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                AndroidUtil.showToast(getApplicationContext(), "OTP Verification Failed: " + e.getMessage());
                                setinprogress(false);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationCode = s;
                                resendingToken = forceResendingToken;
                                AndroidUtil.showToast(getApplicationContext(), "OTP Sent Successfully");
                                setinprogress(false);
                            }
                        });

        if (isResend) {
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        } else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }



    void setinprogress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            nextbtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            nextbtn.setVisibility(View.VISIBLE);
        }
    }

    void signIn(PhoneAuthCredential phoneAuthCredential) {
        setinprogress(true);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                setinprogress(false);
                if (task.isSuccessful()) {
                    Intent intent = new Intent(OTPActivity.this, UserNameActivity.class);
                    intent.putExtra("phone", phone);
                    startActivity(intent);
                    finish(); // Finish current activity to prevent going back
                } else {
                    AndroidUtil.showToast(getApplicationContext(), "OTP verification failed: " + task.getException().getMessage());
                }
            }
        });
    }

    void startResendTimer() {
        resendotp.setEnabled(false);
        new CountDownTimer(timeoutSec * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;
                resendotp.setText("Resend OTP in " + secondsRemaining + " seconds");
            }

            public void onFinish() {
                resendotp.setText("Resend OTP");
                resendotp.setEnabled(true);
                timeoutSec = 30L; // Reset the timeout_sec for the next resend attempt
            }
        }.start();
    }
}
