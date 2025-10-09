// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.7.3" apply false
    id("com.android.library") version "8.7.3" apply false
    // Nếu bạn dùng Kotlin, hãy thêm dòng này (nếu chưa có)
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false // Thay bằng phiên bản Kotlin của bạn

    // THÊM DÒNG NÀY VÀO
    id("com.google.gms.google-services") version "4.4.2" apply false
}

