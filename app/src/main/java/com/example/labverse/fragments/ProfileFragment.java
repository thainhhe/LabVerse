package com.example.labverse.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.labverse.R;
import com.example.labverse.activities.LoginActivity;
import com.example.labverse.activities.EditProfileActivity;
import com.example.labverse.utils.AuthManager;

public class ProfileFragment extends Fragment {

    private TextView tvUserName, tvUserEmail, tvUserRole;
    private Button btnEditProfile, btnSettings, btnLogout;
    private AuthManager authManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        authManager = new AuthManager(getContext());
        initViews(view);
        loadUserData();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserEmail = view.findViewById(R.id.tv_user_email);
        tvUserRole = view.findViewById(R.id.tv_user_role);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnSettings = view.findViewById(R.id.btn_settings);
        btnLogout = view.findViewById(R.id.btn_logout);
    }

    private void loadUserData() {
        tvUserName.setText(authManager.getUserName());
        tvUserEmail.setText(authManager.getUserEmail());
        tvUserRole.setText(authManager.getUserRole());
    }

    private void setupClickListeners() {
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Open settings activity
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authManager.logout();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }
}
