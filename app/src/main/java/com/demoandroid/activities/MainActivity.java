package com.demoandroid.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.demoandroid.R;
import com.demoandroid.services.ApiClient;
import com.demoandroid.services.Utilities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import com.mukeshsolanki.OnOtpCompletionListener;
import com.mukeshsolanki.OtpView;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    String companyName = "TechRover";
    EditText emailEditText;
    Button submitButton;
    Button otpSubmitButton;
    ApiClient apiClient = new ApiClient();
    Intent i;
    OtpView otpView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailEditText = findViewById(R.id.email_edittext);
        submitButton = findViewById(R.id.submit_button);
        otpSubmitButton = findViewById(R.id.otp_submit_button);
        otpView = findViewById(R.id.otp_text_view);


        //email submit button onClick listener
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                Pattern pattern = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
                Matcher matcher = pattern.matcher(email);
                if (matcher.matches()) {
                    Utilities.loading(true, MainActivity.this, R.id.is_loading, R.id.submit_button);
                    loginTrial(email);
                } else {
                    if (email.trim().length() == 0) {
                        emailEditText.setError("please enter email");
                    } else {
                        emailEditText.setError("please enter proper email");
                    }
                }
            }
        });

        otpSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                afterEmailVerification();
            }
        });
    }

    void loginTrial(String email) {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("email", email);
        requestData.put("loginSource", "WEB");
        apiClient.apiService.post("auth/users/login", apiClient.getHeaders(false, getApplicationContext()), requestData).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
                Map<String, Object> statusObject = (Map<String, Object>) response.body().get("status");
                if (statusObject.get("code").toString().equals("OK")) {
                    if (response.body().get("token") != null) {
                        try {
                            SharedPreferences sharedpreferences = getSharedPreferences("cred_prefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor credPrefs = sharedpreferences.edit();
                            credPrefs.putString("token", response.body().get("token").toString());
                            credPrefs.apply();
                            Utilities.loading(false, MainActivity.this, R.id.before_verification_view, R.id.after_verification_view);
                            afterEmailVerification();
                            Utilities.alert(Utilities.States.WARNING, MainActivity.this, "Email found");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                Utilities.loading(false, MainActivity.this, R.id.is_loading, R.id.submit_button);

            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                Utilities.alert(Utilities.States.FAIL, MainActivity.this, "Failed to signin");
                Utilities.loading(false, MainActivity.this, R.id.is_loading, R.id.submit_button);
            }
        });

    }

    void verifyOtp(String otp) {
        apiClient.getClientInstance().apiService.get("auth/users/otp/verify/" + otp, apiClient.getHeaders(true, getApplicationContext())).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
                Map<String, Object> statusObject = (Map<String, Object>) response.body().get("status");
                Map<String, Object> dataObject = (Map<String, Object>) response.body().get("data");
                if (statusObject.get("code").toString().equals("OK")) {
                    Utilities.alert(Utilities.States.WARNING, MainActivity.this, "Welcome " + dataObject.get("firstName").toString());
                    i = new Intent(MainActivity.this, Slider.class);
                    startActivity(i);
                    finish();
                } else {
                    Utilities.alert(Utilities.States.FAIL, MainActivity.this, "Invalid otp");
                }
                Utilities.loading(false, MainActivity.this, R.id.is_loading_new, R.id.otp_submit_button);

            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                Utilities.loading(false, MainActivity.this, R.id.is_loading_new, R.id.otp_submit_button);
                Utilities.loading(false, MainActivity.this, R.id.after_verification_view, R.id.before_verification_view);
            }
        });
    }

    void afterEmailVerification() {
        if (otpView.getText().length() == 6) {
            Utilities.loading(true, MainActivity.this, R.id.is_loading_new, R.id.otp_submit_button);
            verifyOtp(otpView.getText().toString());
        }
        //otp text view listener
        otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                if (otp.length() == 6) {
                    Utilities.loading(true, MainActivity.this, R.id.is_loading_new, R.id.otp_submit_button);
                    verifyOtp(otp);
                }
            }
        });
    }
}