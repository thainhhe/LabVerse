package com.example.labverse.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.labverse.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class UpdateStatusBottomSheet extends BottomSheetDialogFragment {

    // Interface để gửi dữ liệu về Activity
    public interface OnStatusUpdatedListener {
        void onStatusUpdated(String newStatus, String newPriority);
    }
    private OnStatusUpdatedListener mListener;

    private RadioGroup rgStatus, rgPriority;
    private Button btnSave;

    private String currentStatus, currentPriority;
    private String collectionId, paperId;

    // Hàm 'constructor' để nhận dữ liệu
    public static UpdateStatusBottomSheet newInstance(String collectionId, String paperId, String status, String priority) {
        UpdateStatusBottomSheet fragment = new UpdateStatusBottomSheet();
        Bundle args = new Bundle();
        args.putString("collectionId", collectionId);
        args.putString("paperId", paperId);
        args.putString("status", status);
        args.putString("priority", priority);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            collectionId = getArguments().getString("collectionId");
            paperId = getArguments().getString("paperId");
            currentStatus = getArguments().getString("status");
            currentPriority = getArguments().getString("priority");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_update_status, container, false);

        rgStatus = view.findViewById(R.id.rg_status);
        rgPriority = view.findViewById(R.id.rg_priority);
        btnSave = view.findViewById(R.id.btn_save_status);

        // Đặt trạng thái radio button (check) cho đúng
        setCurrentSelections();

        btnSave.setOnClickListener(v -> {
            // 1. Lấy lựa chọn mới
            String newStatus = getSelectedStatus();
            String newPriority = getSelectedPriority();

            // 2. Gửi về Activity qua interface
            mListener.onStatusUpdated(newStatus, newPriority);

            // 3. Đóng BottomSheet
            dismiss();
        });

        return view;
    }

    // Gắn listener (Activity) vào
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (OnStatusUpdatedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnStatusUpdatedListener");
        }
    }

    // --- Các hàm Helper ---

    private void setCurrentSelections() {
        // Check Status
        if (currentStatus != null) {
            switch (currentStatus.toLowerCase()) {
                case "reading":
                    rgStatus.check(R.id.rb_status_reading);
                    break;
                case "finished":
                    rgStatus.check(R.id.rb_status_finished);
                    break;
                case "unread":
                default:
                    rgStatus.check(R.id.rb_status_to_read);
                    break;
            }
        }

        // Check Priority
        if (currentPriority != null) {
            switch (currentPriority.toLowerCase()) {
                case "high":
                    rgPriority.check(R.id.rb_priority_high);
                    break;
                case "medium":
                    rgPriority.check(R.id.rb_priority_medium);
                    break;
                case "low":
                    rgPriority.check(R.id.rb_priority_low);
                    break;
            }
        } else {
            rgPriority.check(R.id.rb_priority_medium); // Mặc định
        }
    }

    private String getSelectedStatus() {
        int selectedId = rgStatus.getCheckedRadioButtonId();
        if (selectedId == R.id.rb_status_reading) {
            return "reading";
        } else if (selectedId == R.id.rb_status_finished) {
            return "finished";
        } else {
            return "unread";
        }
    }

    private String getSelectedPriority() {
        int selectedId = rgPriority.getCheckedRadioButtonId();
        if (selectedId == R.id.rb_priority_high) {
            return "high";
        } else if (selectedId == R.id.rb_priority_low) {
            return "low";
        } else {
            return "medium";
        }
    }
}