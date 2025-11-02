package com.example.labverse.activities;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.labverse.R;
import com.example.labverse.utils.SecureAuthManager;
import java.util.HashMap;
import java.util.Map;
public class SettingsActivity extends AppCompatActivity {

    private SwitchMaterial switchNotifications;
    private SwitchMaterial switchEmailNotifications;
    private SwitchMaterial switchPushNotifications;
    private SwitchMaterial switchNewPapers;
    private SwitchMaterial switchCollectionUpdates;
    private SwitchMaterial switchComments;

    private FirebaseFirestore firestore;
    private SecureAuthManager authManager;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setupToolbar();
        initFirebase();
        initViews();
        loadSettings();
        setupListeners();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Settings");
        }
    }

    private void initFirebase() {
        firestore = FirebaseFirestore.getInstance();
        authManager = new SecureAuthManager(this);
        userId = authManager.getUserId();
    }

    private void initViews() {
        switchNotifications = findViewById(R.id.switch_notifications);
        switchEmailNotifications = findViewById(R.id.switch_email_notifications);
        switchPushNotifications = findViewById(R.id.switch_push_notifications);
        switchNewPapers = findViewById(R.id.switch_new_papers);
        switchCollectionUpdates = findViewById(R.id.switch_collection_updates);
        switchComments = findViewById(R.id.switch_comments);
    }

    private void loadSettings() {
        firestore.collection("user_preferences").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Boolean notificationsEnabled = documentSnapshot.getBoolean("notificationsEnabled");
                        Boolean emailEnabled = documentSnapshot.getBoolean("emailNotifications");
                        Boolean pushEnabled = documentSnapshot.getBoolean("pushNotifications");
                        Boolean newPapers = documentSnapshot.getBoolean("notifyNewPapers");
                        Boolean collectionUpdates = documentSnapshot.getBoolean("notifyCollectionUpdates");
                        Boolean comments = documentSnapshot.getBoolean("notifyComments");

                        switchNotifications.setChecked(notificationsEnabled != null && notificationsEnabled);
                        switchEmailNotifications.setChecked(emailEnabled != null && emailEnabled);
                        switchPushNotifications.setChecked(pushEnabled != null && pushEnabled);
                        switchNewPapers.setChecked(newPapers != null && newPapers);
                        switchCollectionUpdates.setChecked(collectionUpdates != null && collectionUpdates);
                        switchComments.setChecked(comments != null && comments);
                    } else {
                        // Set default values
                        setDefaultSettings();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load settings", Toast.LENGTH_SHORT).show();
                });
    }

    private void setupListeners() {
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateSetting("notificationsEnabled", isChecked);

            // Enable/disable other switches
            switchEmailNotifications.setEnabled(isChecked);
            switchPushNotifications.setEnabled(isChecked);
            switchNewPapers.setEnabled(isChecked);
            switchCollectionUpdates.setEnabled(isChecked);
            switchComments.setEnabled(isChecked);
        });

        switchEmailNotifications.setOnCheckedChangeListener((buttonView, isChecked) ->
                updateSetting("emailNotifications", isChecked));

        switchPushNotifications.setOnCheckedChangeListener((buttonView, isChecked) ->
                updateSetting("pushNotifications", isChecked));

        switchNewPapers.setOnCheckedChangeListener((buttonView, isChecked) ->
                updateSetting("notifyNewPapers", isChecked));

        switchCollectionUpdates.setOnCheckedChangeListener((buttonView, isChecked) ->
                updateSetting("notifyCollectionUpdates", isChecked));

        switchComments.setOnCheckedChangeListener((buttonView, isChecked) ->
                updateSetting("notifyComments", isChecked));
    }

    private void updateSetting(String key, boolean value) {
        Map<String, Object> updates = new HashMap<>();
        updates.put(key, value);
        updates.put("updatedAt", System.currentTimeMillis());

        firestore.collection("user_preferences").document(userId)
                .set(updates, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    // Settings saved
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save settings", Toast.LENGTH_SHORT).show();
                });
    }

    private void setDefaultSettings() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("notificationsEnabled", true);
        defaults.put("emailNotifications", true);
        defaults.put("pushNotifications", true);
        defaults.put("notifyNewPapers", true);
        defaults.put("notifyCollectionUpdates", true);
        defaults.put("notifyComments", true);
        defaults.put("userId", userId);
        defaults.put("createdAt", System.currentTimeMillis());

        firestore.collection("user_preferences").document(userId)
                .set(defaults)
                .addOnSuccessListener(aVoid -> loadSettings());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
