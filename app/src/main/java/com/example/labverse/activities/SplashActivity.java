package com.example.labverse.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper; // Import Looper
import androidx.appcompat.app.AppCompatActivity;

import com.example.labverse.MainActivity; // Quan trọng: Đảm bảo import đúng MainActivity
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.FirebaseApp; // Quan trọng: Import FirebaseApp

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Không cần setContentView cho màn hình chờ, để nó trống sẽ hiển thị theme của cửa sổ

        // BƯỚC 1: KHỞI TẠO FIREBASE (Rất quan trọng!)
        // Đảm bảo Firebase được khởi tạo trước khi sử dụng bất kỳ dịch vụ nào của nó.
        FirebaseApp.initializeApp(this);

        // BƯỚC 2: TẠO ĐỘ TRỄ VÀ KIỂM TRA ĐĂNG NHẬP
        // Sử dụng Handler với Looper.getMainLooper() để đảm bảo an toàn
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Lấy người dùng hiện tại từ Firebase
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            // Điều hướng dựa trên trạng thái đăng nhập
            if (currentUser != null) {
                // Người dùng đã đăng nhập, chuyển đến MainActivity
                // Sử dụng getApplicationContext() để an toàn hơn
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            } else {
                // Người dùng chưa đăng nhập, chuyển đến LoginActivity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }

            // Đóng SplashActivity để người dùng không thể quay lại bằng nút Back
            finish();
        }, 1500); // Độ trễ 1.5 giây để hiển thị logo (nếu có)
    }
}