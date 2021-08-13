package br.niltonvasques.moneycontrol;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

import br.niltonvasques.moneycontrol.service.NotificationService;
import br.niltonvasques.moneycontrolbeta.R;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;

@SuppressLint("NewApi")
public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "[SplashActivity]";

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        askBiometricAuth();
    }

    @Override
    protected void onResume() {
        super.onResume();
        NotificationService.schedulerNotification(this);
    }

    private void askBiometricAuth() {
        BiometricManager biometricManager = BiometricManager.from(this);

        int biometricAuth = biometricManager.canAuthenticate(BIOMETRIC_STRONG);

        if (biometricAuth != BiometricManager.BIOMETRIC_SUCCESS) {
            openMain();
            return;
        }

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(SplashActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                finish();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                openMain();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                finish();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.biometric_title))
                .setSubtitle(getString(R.string.biometric_subtitle))
                .setNegativeButtonText(getString(R.string.biometric_negative_button))
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void openMain() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }
}
