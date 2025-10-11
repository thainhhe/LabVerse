package com.example.labverse.activities;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.example.labverse.MainActivity;
import com.example.labverse.R;
import com.example.labverse.auth.FirebaseAuthManager;


public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin, btnGoogleSignIn;
    private TextView tvRegister, tvForgotPassword;
    private ProgressBar progressBar;
    private FirebaseAuthManager authManager;

    private ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authManager = new FirebaseAuthManager(this);

        // Check if already logged in
        if (authManager.isLoggedIn()) {
            navigateToMain();
            return;
        }

        initViews();
        setupClickListeners();
        setupGoogleSignInLauncher();
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnGoogleSignIn = findViewById(R.id.btn_google_sign_in);
        tvRegister = findViewById(R.id.tv_register);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> performEmailLogin());

        btnGoogleSignIn.setOnClickListener(v -> performGoogleSignIn());

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        tvForgotPassword.setOnClickListener(v -> showForgotPasswordDialog());
    }

    private void setupGoogleSignInLauncher() {
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        authManager.handleGoogleSignInResult(result.getData(),
                                new FirebaseAuthManager.AuthCallback() {
                                    @Override
                                    public void onSuccess(String message) {
                                        hideProgress();
                                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                                        navigateToMain();
                                    }

                                    @Override
                                    public void onFailure(String error) {
                                        hideProgress();
                                        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                                    }
                                });
                    } else {
                        hideProgress();
                        Toast.makeText(this, "Google sign-in cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void performEmailLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        showProgress();

        authManager.loginWithEmail(email, password, new FirebaseAuthManager.AuthCallback() {
            @Override
            public void onSuccess(String message) {
                hideProgress();
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                navigateToMain();
            }

            @Override
            public void onFailure(String error) {
                hideProgress();
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void performGoogleSignIn() {
        showProgress();
        Intent signInIntent = authManager.getGoogleSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    private void showForgotPasswordDialog() {
        // Create dialog with email input
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Reset Password");
        builder.setMessage("Enter your email address to receive password reset link");

        final EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);

        builder.setPositiveButton("Send", (dialog, which) -> {
            String email = input.getText().toString().trim();
            if (!TextUtils.isEmpty(email)) {
                showProgress();
                authManager.sendPasswordResetEmail(email, new FirebaseAuthManager.AuthCallback() {
                    @Override
                    public void onSuccess(String message) {
                        hideProgress();
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(String error) {
                        hideProgress();
                        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);
        btnGoogleSignIn.setEnabled(false);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
        btnLogin.setEnabled(true);
        btnGoogleSignIn.setEnabled(true);
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}