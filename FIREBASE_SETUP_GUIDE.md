# 🔥 Hướng Dẫn Cấu Hình Firebase Cho Android App

## 📋 Checklist - Những Gì Cần Làm Trên Firebase Console

### ✅ Bước 1: Truy Cập Firebase Console

1. Mở trình duyệt và vào: https://console.firebase.google.com
2. Chọn project **labverse-5acb2**
3. Đảm bảo bạn đã đăng nhập với Google account có quyền admin

---

## 🔐 Bước 2: Enable Authentication

### 2.1. Vào Authentication
1. Trong Firebase Console, click vào **Authentication** ở menu bên trái
2. Click tab **Sign-in method**

### 2.2. Enable Email/Password
1. Tìm **Email/Password** trong danh sách providers
2. Click vào nó
3. Toggle **Enable** sang ON
4. (Tùy chọn) Bật **Email link (passwordless sign-in)** nếu muốn
5. Click **Save**

### 2.3. Tạo Test User (Nếu Chưa Có)
1. Click tab **Users**
2. Click **Add user**
3. Nhập:
   - Email: `test@labverse.com` (hoặc email bất kỳ)
   - Password: `Test123456`
4. Click **Add user**

✅ **Xong Authentication!**

---

## 📚 Bước 3: Enable Firestore Database

### 3.1. Tạo Database
1. Click **Firestore Database** ở menu bên trái
2. Nếu chưa có database, click **Create database**
3. Chọn location: **asia-southeast1 (Singapore)** (gần Việt Nam nhất)
4. **Chọn chế độ:**
   - Chọn **Start in test mode** (cho development)
   - Click **Next** → **Enable**

### 3.2. Set Firestore Rules
1. Sau khi database được tạo, click tab **Rules**
2. Xóa rule mặc định và paste code này:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // Papers collection - User chỉ truy cập papers của mình
    match /papers/{paperId} {
      allow read, write: if request.auth != null && 
                          request.auth.uid == resource.data.userId;
      allow create: if request.auth != null;
    }
    
    // Annotations collection - User chỉ truy cập annotations của mình
    match /annotations/{annotationId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && 
                    request.auth.uid == request.resource.data.userId;
      allow create: if request.auth != null;
    }
    
    // Collections collection
    match /collections/{collectionId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && 
                    request.auth.uid == resource.data.ownerId;
      allow create: if request.auth != null;
    }
    
    // Users collection
    match /users/{userId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && 
                    request.auth.uid == userId;
      allow create: if request.auth != null;
    }
  }
}
```

3. Click **Publish**

✅ **Xong Firestore!**

---

## 📦 Bước 4: Enable Firebase Storage

### 4.1. Tạo Storage Bucket
1. Click **Storage** ở menu bên trái
2. Click **Get started**
3. Chọn **Start in test mode**
4. Click **Next**
5. Chọn location: **asia-southeast1** (Singapore)
6. Click **Done**

### 4.2. Set Storage Rules
1. Click tab **Rules**
2. Xóa rule mặc định và paste code này:

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    
    // PDF files - User chỉ truy cập files của mình
    match /papers/{userId}/{paperId} {
      // Cho phép đọc nếu đã đăng nhập và là chủ file
      allow read: if request.auth != null && request.auth.uid == userId;
      
      // Cho phép upload nếu đã đăng nhập và là chủ file
      allow write: if request.auth != null && request.auth.uid == userId;
      
      // Giới hạn file size: 100MB
      allow write: if request.resource.size < 100 * 1024 * 1024;
    }
  }
}
```

3. Click **Publish**

### 4.3. Kiểm Tra Bucket URL
- Bucket URL sẽ là: `gs://labverse-5acb2.firebasestorage.app`
- Hoặc: `labverse-5acb2.firebasestorage.app`
- ✅ Đã đúng với file `google-services.json` của bạn!

✅ **Xong Storage!**

---

## 🔧 Bước 5: Verify Android App Configuration

### 5.1. Kiểm Tra Android App
1. Click vào icon ⚙️ (Settings) ở góc trên bên trái
2. Click **Project settings**
3. Scroll xuống **Your apps**
4. Kiểm tra xem có Android app với package name: `com.example.labverse`

### 5.2. Nếu Chưa Có App, Thêm App Mới:
1. Click **Add app**
2. Chọn **Android** icon
3. Nhập:
   - **Android package name:** `com.example.labverse`
   - **App nickname:** `LabVerse` (tùy chọn)
   - **Debug signing certificate SHA-1:** (để trống cho development)
4. Click **Register app**
5. Download file `google-services.json` → Thay thế file cũ trong `app/` folder
6. Click **Next** → **Next** → **Continue to console**

✅ **Xong Android App!**

---

## 📊 Bước 6: Test Connection

### 6.1. Build và Chạy App
```bash
gradlew.bat clean
gradlew.bat assembleDebug
gradlew.bat installDebug
```

### 6.2. Test Authentication
1. Mở app
2. Đăng nhập với test user: `test@labverse.com` / `Test123456`
3. Nếu thành công → ✅ Authentication OK

### 6.3. Test Storage Upload
1. Trong app, vào **Import Paper**
2. Upload một PDF file
3. Kiểm tra Firebase Console → Storage:
   - Sẽ thấy folder `papers/{userId}/`
   - Có file PDF bên trong
4. Nếu thấy file → ✅ Storage OK

### 6.4. Test Firestore Sync
1. Mở PDF đã upload
2. Thêm annotation (highlight/note)
3. Kiểm tra Firebase Console → Firestore:
   - Sẽ thấy collection `annotations`
   - Có document với annotation data
4. Nếu thấy data → ✅ Firestore OK

---

## 🐛 Troubleshooting

### Lỗi: "FirebaseApp initialization unsuccessful"
**Giải pháp:**
- Đảm bảo file `google-services.json` đúng vị trí: `app/google-services.json`
- Sync Gradle: File → Sync Project with Gradle Files
- Rebuild: Build → Clean Project → Rebuild Project

### Lỗi: "Authentication failed"
**Giải pháp:**
- Kiểm tra Email/Password provider đã enable chưa
- Tạo test user trên Firebase Console
- Kiểm tra internet connection

### Lỗi: "Permission denied" khi upload PDF
**Giải pháp:**
- Kiểm tra Storage rules đã publish chưa
- Đảm bảo user đã đăng nhập (`request.auth != null`)
- Kiểm tra file size < 100MB

### Lỗi: "Missing or insufficient permissions" trên Firestore
**Giải pháp:**
- Kiểm tra Firestore rules đã publish chưa
- Đảm bảo user đã đăng nhập
- Check userId trong rules match với auth.uid

### Lỗi: "Storage bucket not found"
**Giải pháp:**
- Verify bucket URL trong `google-services.json`
- Đảm bảo Storage đã được enable trên Console
- Download lại `google-services.json` mới

---

## 📱 Test Mode vs Production Mode

### Test Mode (Development) - ĐANG DÙNG
- ✅ **Ưu điểm:** Dễ test, không cần viết rules phức tạp
- ⚠️ **Nhược điểm:** KHÔNG AN TOÀN cho production
- ⏰ **Hết hạn:** 30 ngày sau khi tạo (Firestore test mode)

### Production Mode - KHI DEPLOY
Khi app đã sẵn sàng release, update rules:

**Firestore Rules (Production):**
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

**Storage Rules (Production):**
```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

---

## ✅ Checklist Cuối Cùng

Trước khi test app, đảm bảo:

- [ ] Firebase Authentication đã enable
- [ ] Email/Password provider đã enable
- [ ] Đã tạo ít nhất 1 test user
- [ ] Firestore Database đã được tạo
- [ ] Firestore Rules đã publish
- [ ] Storage Bucket đã được tạo
- [ ] Storage Rules đã publish
- [ ] Android app đã được add với đúng package name
- [ ] File `google-services.json` đã có trong `app/` folder
- [ ] Project đã build thành công

---

## 🎯 Các URL Quan Trọng

- **Firebase Console:** https://console.firebase.google.com/project/labverse-5acb2
- **Authentication:** https://console.firebase.google.com/project/labverse-5acb2/authentication
- **Firestore:** https://console.firebase.google.com/project/labverse-5acb2/firestore
- **Storage:** https://console.firebase.google.com/project/labverse-5acb2/storage
- **Project Settings:** https://console.firebase.google.com/project/labverse-5acb2/settings/general

---

## 📞 Support

Nếu gặp vấn đề:
1. Check Logcat trong Android Studio để xem error messages
2. Verify từng bước trong checklist trên
3. Đảm bảo internet connection ổn định
4. Test với test user trước khi tạo real users

---

**Sau khi làm xong các bước trên, app sẽ hoạt động hoàn toàn với Firebase!** 🎉

