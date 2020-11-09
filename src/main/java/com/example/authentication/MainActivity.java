package com.example.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.core.content.ContextCompat;

import androidx.biometric.BiometricPrompt;

import android.app.KeyguardManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private BiometricPrompt.PromptInfo promptInfoWO;
    private BiometricManager manager;
    private KeyguardManager keyguard;
    private int successes;
    private int errors;
    TextView tvStats;
    TextView tvParams;
    Button btLoginWith;
    Button btLoginWithout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btLoginWith = findViewById(R.id.btW);
        btLoginWithout = findViewById(R.id.btWO);
        tvStats = findViewById(R.id.tvStats);
        tvParams = findViewById(R.id.tvParams);

        successes = 0;
        errors = 0;

        manager = manager.from(this);
        keyguard = (KeyguardManager) this.getSystemService(KEYGUARD_SERVICE);
        tvStats.setText("Successes: "  + successes + "\nErrors: " + errors);
        tvParams.setText("can authenticate: " +String.valueOf(manager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) +
                "\nis device secure: " +  String.valueOf(keyguard.isDeviceSecure())  );

        executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(MainActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
                errors += 1;
                tvStats.setText("Successes: "  + successes + "\nErrors: " + errors);
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                successes +=1;
                tvStats.setText("Successes: "  + successes + "\nErrors: " + errors);

            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
                errors += 1;
                tvStats.setText("Successes: "  + successes + "\nErrors: " + errors);

            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login with device credentials")
                .setSubtitle("Use biometric or device credentials")
                .setDeviceCredentialAllowed(true)
                .build();

        promptInfoWO = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login without device credentials")
                .setSubtitle("Use biometric credentials only")
                .setDeviceCredentialAllowed(false)
                .setNegativeButtonText("Cancel")
                .build();

        btLoginWith.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                biometricPrompt.authenticate(promptInfo);
            }
        });

        btLoginWithout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                biometricPrompt.authenticate(promptInfoWO);
            }
        });
    }
}
