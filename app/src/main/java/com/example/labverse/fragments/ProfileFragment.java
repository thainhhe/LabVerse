package com.example.labverse.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.labverse.R;
import com.example.labverse.activities.LoginActivity;
import com.example.labverse.activities.EditProfileActivity;
import com.example.labverse.activities.SettingsActivity; // Đã thêm
import com.example.labverse.auth.FirebaseAuthManager; // Đã thêm
import com.example.labverse.database.LabVerseDatabase; // Đã thêm
import com.example.labverse.database.entities.UserEntity; // Đã thêm
import com.example.labverse.utils.SecureAuthManager; // Đã thay đổi

public class ProfileFragment extends Fragment {

    private TextView tvUserName, tvUserEmail, tvUserRole;
    private Button btnEditProfile, btnSettings, btnLogout;
    private SecureAuthManager secureAuthManager; // Đã thay đổi
    private FirebaseAuthManager firebaseAuthManager; // Đã thêm
    private LabVerseDatabase database; // Đã thêm

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Khởi tạo các trình quản lý và database
        secureAuthManager = new SecureAuthManager(getContext());
        firebaseAuthManager = new FirebaseAuthManager(getContext());
        database = LabVerseDatabase.getDatabase(getContext());

        initViews(view);
        setupClickListeners();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Bắt đầu quan sát dữ liệu người dùng từ Room
        observeUserData();
    }

    private void initViews(View view) {
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserEmail = view.findViewById(R.id.tv_user_email);
        tvUserRole = view.findViewById(R.id.tv_user_role);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnSettings = view.findViewById(R.id.btn_settings);
        btnLogout = view.findViewById(R.id.btn_logout);
    }

    /**
     * Lấy userId từ SecureAuthManager và đăng ký lắng nghe
     * thay đổi dữ liệu UserEntity từ Room Database.
     */
    private void observeUserData() {
        String userId = secureAuthManager.getUserId();
        if (userId == null) {
            // Người dùng không hợp lệ, có thể xử lý logout
            return;
        }

        database.userDao().getUserById(userId).observe(getViewLifecycleOwner(), new Observer<UserEntity>() {
            @Override
            public void onChanged(UserEntity user) {
                if (user != null) {
                    // Cập nhật UI ngay khi có dữ liệu từ Room
                    tvUserName.setText(user.getFullName());
                    tvUserEmail.setText(user.getEmail());
                    tvUserRole.setText(user.getRole());

                    // TODO: Để hiển thị ảnh đại diện (iv_profile_picture),
                    // bạn cần thêm trường 'avatarUrl' vào UserEntity và
                    // cập nhật nó trong EditProfileActivity.
                    // Sau đó, dùng Glide để tải ảnh tại đây.
                }
            }
        });
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
                // Mở màn hình Settings
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sử dụng FirebaseAuthManager để đăng xuất
                firebaseAuthManager.logout();

                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });
    }
}