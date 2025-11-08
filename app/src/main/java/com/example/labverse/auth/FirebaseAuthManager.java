package com.example.labverse.auth;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.labverse.R;
import com.example.labverse.database.LabVerseDatabase;
import com.example.labverse.database.entities.UserEntity;
import com.example.labverse.utils.SecureAuthManager;

import java.util.HashMap;
import java.util.Map;
public class FirebaseAuthManager {
    private static final String TAG = "FirebaseAuthManager";
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private GoogleSignInClient googleSignInClient;
    private SecureAuthManager secureAuthManager;
    private Context context;
    private LabVerseDatabase database;

    public FirebaseAuthManager(Context context) {
        this.context = context;
        this.auth = FirebaseAuth.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
        this.secureAuthManager = new SecureAuthManager(context);
        this.database = LabVerseDatabase.getDatabase(context);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    // ==================== EMAIL/PASSWORD AUTHENTICATION ====================

    // Thay thế toàn bộ phương thức registerWithEmail của bạn bằng phương thức này

    public void registerWithEmail(String email, String password, String fullName,
                                  String affiliation, String role,
                                  AuthCallback callback) {
        // Validate password strength
        if (!isPasswordStrong(password)) {
            callback.onFailure("Password must be at least 8 characters with uppercase, lowercase, and number");
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (firebaseUser == null) {
                            callback.onFailure("Failed to get user after creation.");
                            return;
                        }

                        // BƯỚC 1: Gửi email xác thực
                        firebaseUser.sendEmailVerification()
                                .addOnCompleteListener(verificationTask -> {
                                    if (verificationTask.isSuccessful()) {
                                        // BƯỚC 2: SAU KHI GỬI EMAIL THÀNH CÔNG, MỚI TẠO PROFILE
                                        Log.d(TAG, "Verification email sent successfully.");
                                        String userId = firebaseUser.getUid();
                                        createUserProfile(userId, email, fullName, affiliation, role, callback);
                                    } else {
                                        // Xử lý lỗi nếu không gửi được email
                                        callback.onFailure("User created, but failed to send verification email: " + verificationTask.getException().getMessage());
                                    }
                                });
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    public void loginWithEmail(String email, String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Check if email is verified
                            if (!firebaseUser.isEmailVerified()) {
                                callback.onFailure("Please verify your email before logging in");
                                auth.signOut();
                                return;
                            }

                            String userId = firebaseUser.getUid();
                            String token = firebaseUser.getIdToken(false).getResult().getToken();

                            // Save to encrypted storage
                            secureAuthManager.saveAuthToken(token);
                            secureAuthManager.saveUserId(userId);

                            // Load user profile from Firestore
                            loadUserProfile(userId, callback);
                        }
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    // ==================== GOOGLE OAUTH2 AUTHENTICATION ====================

    public Intent getGoogleSignInIntent() {
        return googleSignInClient.getSignInIntent();
    }

    public void handleGoogleSignInResult(Intent data, AuthCallback callback) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                firebaseAuthWithGoogle(account.getIdToken(), callback);
            }
        } catch (ApiException e) {
            callback.onFailure("Google sign in failed: " + e.getMessage());
        }
    }

    private void firebaseAuthWithGoogle(String idToken, AuthCallback callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();
                            String email = firebaseUser.getEmail();
                            String displayName = firebaseUser.getDisplayName();

                            // Check if user exists, if not create profile
                            checkAndCreateUserProfile(userId, email, displayName, callback);
                        }
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    // ==================== USER PROFILE MANAGEMENT ====================

    private void createUserProfile(String userId, String email, String fullName,
                                   String affiliation, String role, AuthCallback callback) {
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("userId", userId);
        userProfile.put("email", email);
        userProfile.put("fullName", fullName);
        userProfile.put("affiliation", affiliation);
        userProfile.put("role", role);
        userProfile.put("createdAt", System.currentTimeMillis());
        userProfile.put("isActive", true);

        firestore.collection("users").document(userId)
                .set(userProfile)
                .addOnSuccessListener(aVoid -> {
                    // Save to local Room database
                    saveToLocalDatabase(userId, email, fullName, affiliation, role);

                    // Save auth info
                    secureAuthManager.saveUserId(userId);
                    secureAuthManager.saveUserData(email, fullName, role);

                    callback.onSuccess("Registration successful");
                })
                .addOnFailureListener(e -> {
                    callback.onFailure("Failed to create profile: " + e.getMessage());
                });
    }

    private void loadUserProfile(String userId, AuthCallback callback) {
        firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String email = documentSnapshot.getString("email");
                        String fullName = documentSnapshot.getString("fullName");
                        String affiliation = documentSnapshot.getString("affiliation");
                        String role = documentSnapshot.getString("role");
                        secureAuthManager.saveUserId(userId);
                        // Save to secure storage
                        secureAuthManager.saveUserData(email, fullName, role);

                        // Save to local database
                        saveToLocalDatabase(userId, email, fullName, affiliation, role);

                        callback.onSuccess("Login successful");
                    } else {
                        callback.onFailure("User profile not found");
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onFailure("Failed to load profile: " + e.getMessage());
                });
    }

    // ĐÃ SỬA LẠI:
    private void checkAndCreateUserProfile(String googleUserId, String email, String displayName,
                                           AuthCallback callback) {
        if (email == null || email.isEmpty()) {
            callback.onFailure("Google account does not have an email.");
            return;
        }

        Log.d(TAG, "checkAndCreateUserProfile: Querying Firestore for email: " + email);

        firestore.collection("users")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            com.google.firebase.firestore.DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            String existingUserId = documentSnapshot.getId();

                            Log.d(TAG, "User email exists. Loading profile for " + existingUserId);
                            loadUserProfile(existingUserId, callback);


                        } else {
                            Log.d(TAG, "New user. Creating profile for " + googleUserId);
                            createUserProfile(googleUserId, email, displayName, "", "Researcher", callback);
                        }
                    } else {
                        Log.e(TAG, "Lỗi truy vấn Firestore (Firestore query failed): ", task.getException());
                        callback.onFailure("Failed to check user profile: " + task.getException().getMessage());
                    }
                });
    }

    private void saveToLocalDatabase(String userId, String email, String fullName,
                                     String affiliation, String role) {
        LabVerseDatabase.databaseWriteExecutor.execute(() -> {
            UserEntity user = new UserEntity(userId, email);
            user.setFullName(fullName);
            user.setAffiliation(affiliation);
            user.setRole(role);
            user.setFirebaseUid(userId);
            user.setSyncStatus("synced");
            user.setLastSync(System.currentTimeMillis());

            database.userDao().insert(user);
        });
    }

    // ==================== PASSWORD MANAGEMENT ====================

    public void sendPasswordResetEmail(String email, AuthCallback callback) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess("Password reset email sent");
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    public void changePassword(String currentPassword, String newPassword, AuthCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null || user.getEmail() == null) {
            callback.onFailure("User not authenticated");
            return;
        }

        // Re-authenticate before changing password
        AuthCredential credential = com.google.firebase.auth.EmailAuthProvider
                .getCredential(user.getEmail(), currentPassword);

        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user.updatePassword(newPassword)
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        callback.onSuccess("Password changed successfully");
                                    } else {
                                        callback.onFailure(updateTask.getException().getMessage());
                                    }
                                });
                    } else {
                        callback.onFailure("Current password is incorrect");
                    }
                });
    }

    // ==================== VALIDATION ====================

    private boolean isPasswordStrong(String password) {
        // At least 8 characters
        if (password.length() < 8) return false;

        // Has uppercase
        if (!password.matches(".*[A-Z].*")) return false;

        // Has lowercase
        if (!password.matches(".*[a-z].*")) return false;

        // Has number
        if (!password.matches(".*\\d.*")) return false;

        return true;
    }

    // ==================== LOGOUT ====================

    public void logout() {
        auth.signOut();
        googleSignInClient.signOut();
        secureAuthManager.logout();
    }

    // ==================== HELPERS ====================

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public boolean isLoggedIn() {
        return auth.getCurrentUser() != null && secureAuthManager.isLoggedIn();
    }

    // ==================== CALLBACK INTERFACE ====================

    public interface AuthCallback {
        void onSuccess(String message);
        void onFailure(String error);
    }
}
