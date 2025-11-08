package com.example.labverse.activities;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.labverse.R;
import com.example.labverse.auth.FirebaseAuthManager;
import com.example.labverse.database.LabVerseDatabase;
import com.example.labverse.database.entities.UserEntity;
import com.example.labverse.utils.SecureAuthManager;

import java.util.HashMap;
import java.util.Map;
public class EditProfileActivity extends AppCompatActivity {

    private ImageView ivProfilePicture;
    private TextInputEditText etFullName, etEmail, etAffiliation;
    private AutoCompleteTextView spinnerRole;
    private Button btnSave, btnChangePassword, btnCancel;
    private ProgressBar progressBar;

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private SecureAuthManager authManager;
    private FirebaseAuthManager firebaseAuthManager;
    private LabVerseDatabase database;

    private String userId;
    private Uri selectedImageUri;
    private String currentAvatarUrl;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        setupToolbar();
        initFirebase();
        initViews();
        setupImagePicker();
        setupRoleSpinner();
        loadUserData();
        setupClickListeners();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Edit Profile");
        }
    }

    private void initFirebase() {
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        authManager = new SecureAuthManager(this);
        firebaseAuthManager = new FirebaseAuthManager(this);
        database = LabVerseDatabase.getDatabase(this);
        userId = authManager.getUserId();
    }

    private void initViews() {
        ivProfilePicture = findViewById(R.id.iv_profile_picture);
        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etAffiliation = findViewById(R.id.et_affiliation);
        spinnerRole = findViewById(R.id.spinner_role);
        btnSave = findViewById(R.id.btn_save);
        btnChangePassword = findViewById(R.id.btn_change_password);
        btnCancel = findViewById(R.id.btn_cancel);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        ivProfilePicture.setImageURI(selectedImageUri);
                    }
                });

        ivProfilePicture.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
//            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });
    }

    private void setupRoleSpinner() {
        String[] roles = getResources().getStringArray(R.array.user_roles);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                roles
        );
        spinnerRole.setAdapter(adapter);
    }

    private void loadUserData() {
        showProgress();

        firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String fullName = documentSnapshot.getString("fullName");
                        String email = documentSnapshot.getString("email");
                        String affiliation = documentSnapshot.getString("affiliation");
                        String role = documentSnapshot.getString("role");
                        currentAvatarUrl = documentSnapshot.getString("avatarUrl");

                        etFullName.setText(fullName);
                        etEmail.setText(email);
                        etAffiliation.setText(affiliation);
                        spinnerRole.setText(role, false);

                        // Load avatar
                        if (!TextUtils.isEmpty(currentAvatarUrl)) {
                            Glide.with(this)
                                    .load(currentAvatarUrl)
                                    .placeholder(R.drawable.ic_person)
                                    .into(ivProfilePicture);
                        }
                    }
                    hideProgress();
                })
                .addOnFailureListener(e -> {
                    hideProgress();
                    Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                });
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> saveProfile());
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void saveProfile() {
        String fullName = etFullName.getText().toString().trim();
        String affiliation = etAffiliation.getText().toString().trim();
        String role = spinnerRole.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Name is required");
            return;
        }

        if (TextUtils.isEmpty(affiliation)) {
            etAffiliation.setError("Affiliation is required");
            return;
        }

        showProgress();

        // Check if new image selected
        if (selectedImageUri != null) {
            uploadAvatar(fullName, affiliation, role);
        } else {
            updateProfile(fullName, affiliation, role, currentAvatarUrl);
        }
    }

    private void uploadAvatar(String fullName, String affiliation, String role) {
        StorageReference avatarRef = storage.getReference()
                .child("avatars/" + userId + ".jpg");

        avatarRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    avatarRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String avatarUrl = uri.toString();
                        updateProfile(fullName, affiliation, role, avatarUrl);
                    });
                })
                .addOnFailureListener(e -> {
                    hideProgress();
                    Toast.makeText(this, "Failed to upload avatar", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateProfile(String fullName, String affiliation, String role, String avatarUrl) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("fullName", fullName);
        updates.put("affiliation", affiliation);
        updates.put("role", role);
        if (avatarUrl != null) {
            updates.put("avatarUrl", avatarUrl);
        }
        updates.put("updatedAt", System.currentTimeMillis());

        firestore.collection("users").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    // Update local database
                    updateLocalDatabase(fullName, affiliation, role, avatarUrl);

                    // Update secure storage
                    authManager.saveUserData(etEmail.getText().toString(), fullName, role);

                    hideProgress();
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    hideProgress();
                    Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateLocalDatabase(String fullName, String affiliation, String role, String avatarUrl) {
        LabVerseDatabase.databaseWriteExecutor.execute(() -> {
            UserEntity user = database.userDao().getUserByEmail(etEmail.getText().toString());
            if (user != null) {
                user.setFullName(fullName);
                user.setAffiliation(affiliation);
                user.setRole(role);
                database.userDao().update(user);
            }
        });
    }

    private void showChangePasswordDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        TextInputEditText etCurrentPassword = dialogView.findViewById(R.id.et_current_password);
        TextInputEditText etNewPassword = dialogView.findViewById(R.id.et_new_password);
        TextInputEditText etConfirmPassword = dialogView.findViewById(R.id.et_confirm_password);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Change Password")
                .setView(dialogView)
                .setPositiveButton("Change", null)
                .setNegativeButton("Cancel", (d, w) -> d.dismiss())
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(v -> {
                String currentPassword = etCurrentPassword.getText().toString().trim();
                String newPassword = etNewPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();

                if (TextUtils.isEmpty(currentPassword)) {
                    etCurrentPassword.setError("Required");
                    return;
                }

                if (TextUtils.isEmpty(newPassword) || newPassword.length() < 8) {
                    etNewPassword.setError("Password must be at least 8 characters");
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    etConfirmPassword.setError("Passwords do not match");
                    return;
                }

                showProgress();
                dialog.dismiss();

                firebaseAuthManager.changePassword(currentPassword, newPassword,
                        new FirebaseAuthManager.AuthCallback() {
                            @Override
                            public void onSuccess(String message) {
                                hideProgress();
                                Toast.makeText(EditProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(String error) {
                                hideProgress();
                                Toast.makeText(EditProfileActivity.this, error, Toast.LENGTH_LONG).show();
                            }
                        });
            });
        });

        dialog.show();
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);
        btnChangePassword.setEnabled(false);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
        btnSave.setEnabled(true);
        btnChangePassword.setEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}