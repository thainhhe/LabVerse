package com.example.labverse.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.example.labverse.MainActivity;
import com.example.labverse.R;
import com.example.labverse.utils.AuthManager;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000; // 2 seconds
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        authManager = new AuthManager(this);

        // Delay for splash screen and then navigate to appropriate screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navigateToNextScreen();
            }
        }, SPLASH_DURATION);
    }

    private void navigateToNextScreen() {
        Intent intent;

        if (authManager.isLoggedIn()) {
            // User is already logged in, go to main activity
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            // User is not logged in, go to login activity
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
