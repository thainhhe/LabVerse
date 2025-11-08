# LabVerse - PDF Import & Reader Implementation Guide

## Overview
This document describes the implementation of the **Import New Paper Activity** and **Integrated PDF Reader Activity** features for the LabVerse application.

---

## 📚 Features Implemented

### 1. Import New Paper Activity
**Purpose:** To add new research papers to a user's library from various sources.

**Location:** `app/src/main/java/com/example/labverse/activities/`
- `ImportPaperActivity.java` - Main import options screen
- `PdfProcessingActivity.java` - PDF upload and metadata entry

**Key Features:**
- ✅ Upload PDF via Android file picker
- ✅ Upload PDF to Firebase Storage with progress tracking
- ✅ Extract and enter paper metadata (title, authors, journal, year, DOI, abstract)
- ✅ Save paper information to local Room database
- ✅ Automatic sync with Firebase Firestore
- ✅ Navigate to Paper Detail screen after successful import

**UI Components:**
- Material Design cards for import options
- File picker dialog for PDF selection
- Progress bar for upload tracking
- Text input fields for paper metadata
- Save/Cancel action buttons

---

### 2. Integrated PDF Reader Activity
**Purpose:** To provide an in-app PDF reading experience with annotation capabilities.

**Location:** `app/src/main/java/com/example/labverse/activities/PdfReaderActivity.java`

**Key Features:**
- ✅ PDF rendering using AndroidPdfViewer library (https://github.com/marain87/AndroidPdfViewer)
- ✅ Load PDFs from Firebase Storage or local files
- ✅ Reading progress tracking (current page, total pages)
- ✅ Multiple annotation types:
  - **Highlight** - Mark important text with colors
  - **Note** - Add text notes to specific pages
  - **Underline** - Underline important sections
- ✅ Color picker with 5 colors (Yellow, Green, Blue, Red, Purple)
- ✅ Annotation overlay system
- ✅ View and manage existing annotations
- ✅ Delete annotations
- ✅ Automatic sync to Firebase Firestore
- ✅ Multi-device support (annotations sync across devices)

**Annotation System:**
- Annotations stored locally in Room database
- Automatically synced to Firebase Firestore
- Linked to document and page number
- Position data stored as coordinates (startX, startY, endX, endY)
- Support for collaborative annotations (viewable by collection members)

---

## 🏗️ Architecture

### Database Layer (Room)

#### AnnotationEntity
**Location:** `app/src/main/java/com/example/labverse/database/entities/AnnotationEntity.java`

**Fields:**
- `annotationId` - Unique identifier
- `paperId` - Reference to paper
- `userId` - Owner of annotation
- `type` - Type of annotation (highlight, note, underline, drawing)
- `pageNumber` - Page where annotation is placed
- `content` - Text content (for notes)
- `color` - Highlight/underline color
- `positionData` - JSON string with coordinates
- `createdAt`, `updatedAt` - Timestamps
- `syncStatus` - Sync state (pending, synced)
- `firebaseId` - Firebase document ID

#### AnnotationDao
**Location:** `app/src/main/java/com/example/labverse/database/dao/AnnotationDao.java`

**Key Methods:**
- `getAnnotationsByPaper()` - Get all annotations for a paper
- `getAnnotationsByPage()` - Get annotations for specific page
- `insert()` - Save new annotation
- `update()` - Update existing annotation
- `delete()` - Remove annotation
- `updateSyncStatus()` - Update sync state

### Firebase Layer

#### FirebaseAnnotation
**Location:** `app/src/main/java/com/example/labverse/firebase/models/FirebaseAnnotation.java`

Cloud Firestore collection: `annotations`

**Fields:**
- Same as AnnotationEntity but optimized for Firestore
- Uses `Timestamp` for dates
- Uses `Map<String, Object>` for position data

#### Firebase Storage
**Path Structure:**
```
papers/
  ├── {userId}/
  │   ├── {paperId}.pdf
  │   ├── {paperId2}.pdf
  │   └── ...
```

---

## 🛠️ Core Components

### 1. PdfStorageManager
**Location:** `app/src/main/java/com/example/labverse/utils/PdfStorageManager.java`

**Purpose:** Manage PDF uploads, downloads, and deletions in Firebase Storage

**Methods:**
- `uploadPdf(Uri, userId, paperId, callback)` - Upload PDF with progress
- `downloadPdf(pdfUrl, callback)` - Download PDF from Firebase
- `deletePdf(pdfUrl, callback)` - Remove PDF from Firebase

**Features:**
- Progress tracking during upload
- Automatic download URL generation
- Error handling and callbacks

### 2. AnnotationManager
**Location:** `app/src/main/java/com/example/labverse/utils/AnnotationManager.java`

**Purpose:** Manage annotation CRUD operations and synchronization

**Methods:**
- `saveAnnotation(annotation, callback)` - Save new annotation
- `updateAnnotation(annotation, callback)` - Update existing annotation
- `deleteAnnotation(annotationId, callback)` - Delete annotation
- `getAnnotationsForPage(paperId, page)` - Retrieve page annotations
- `getAnnotationsForPaper(paperId)` - Retrieve all paper annotations

**Features:**
- Local-first approach (save to Room first)
- Automatic background sync to Firebase
- Conflict resolution
- Offline support

### 3. Annotation Model
**Location:** `app/src/main/java/com/example/labverse/models/Annotation.java`

**Purpose:** Runtime model for annotations

**Constants:**
```java
TYPE_HIGHLIGHT = "highlight"
TYPE_NOTE = "note"
TYPE_UNDERLINE = "underline"
TYPE_DRAWING = "drawing"

COLOR_YELLOW = "#FFFF00"
COLOR_GREEN = "#00FF00"
COLOR_BLUE = "#00BFFF"
COLOR_RED = "#FF6B6B"
COLOR_PURPLE = "#BA55D3"
```

**Methods:**
- `getPositionDataJson()` - Convert position to JSON
- `setPositionDataFromJson(json)` - Parse position from JSON

---

## 📱 UI Components

### Activities

#### ImportPaperActivity
**Layout:** `activity_import_paper.xml`

Options:
1. Upload PDF (Opens file picker)
2. Import BibTeX (Placeholder)
3. Manual Entry (Opens manual form)

#### PdfProcessingActivity
**Layout:** `activity_pdf_processing.xml`

Components:
- File information card with upload progress
- Paper metadata input fields
- Save/Cancel buttons
- Real-time upload status

#### PdfReaderActivity
**Layout:** `activity_pdf_reader.xml`

Components:
- PDFView (AndroidPdfViewer)
- Toolbar with title
- Bottom bar with page counter
- Floating Action Button for annotations
- Annotation toolbar (highlight, note, underline)
- Progress bar for loading

#### PaperDetailActivity
**Layout:** `activity_paper_detail.xml` (needs to be created)

Components:
- Paper information display
- "Read PDF" button
- Favorite toggle button
- Reading progress indicator

### Dialogs

#### Color Picker Dialog
**Layout:** `dialog_color_picker.xml`

- 5 circular color options
- Click to select color

#### Add Note Dialog
**Layout:** `dialog_add_note.xml`

- Multi-line text input
- Save/Cancel buttons

---

## 🎨 Resources

### Drawables Created

1. **ic_highlight.xml** - Highlight icon
2. **ic_note.xml** - Note icon
3. **ic_underline.xml** - Underline icon
4. **ic_edit.xml** - Edit/Annotate FAB icon
5. **ic_favorite_filled.xml** - Filled heart icon
6. **color_circle.xml** - Circular color selector shape
7. **rounded_background.xml** - Rounded card background

---

## 🔄 Data Flow

### PDF Upload Flow
```
1. User selects PDF via ImportPaperActivity
2. Opens PdfProcessingActivity with PDF URI
3. User enters paper metadata
4. Click "Save Paper"
5. PdfStorageManager uploads PDF to Firebase Storage
6. Progress tracked and displayed
7. On success, get download URL
8. Save PaperEntity to Room database with pdfUrl
9. Navigate to PaperDetailActivity
```

### Annotation Flow
```
1. User opens paper in PdfReaderActivity
2. Tap FAB to show annotation toolbar
3. Select annotation type (highlight/note/underline)
4. For highlight/underline: Select color from picker
5. For note: Enter text in dialog
6. Long press on PDF to place annotation
7. AnnotationManager saves to Room database
8. Background sync to Firebase Firestore
9. Annotation appears as overlay on PDF
10. Other devices fetch and display synced annotations
```

### Multi-Device Sync Flow
```
Device A:
1. Create annotation
2. Save to local Room database
3. Sync to Firebase Firestore

Device B:
1. Open same paper
2. Query Firebase Firestore for annotations
3. Merge with local annotations
4. Display all annotations
```

---

## 📦 Dependencies

Already configured in `build.gradle.kts`:

```kotlin
// PDF Viewer
implementation("com.github.mhiew:android-pdf-viewer:3.2.0-beta.1")

// Firebase
implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
implementation("com.google.firebase:firebase-auth")
implementation("com.google.firebase:firebase-firestore")
implementation("com.google.firebase:firebase-storage")

// Room Database
implementation("androidx.room:room-runtime:2.6.0")
kapt("androidx.room:room-compiler:2.6.0")
```

---

## 🔐 Permissions

Required permissions in `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />
```

---

## 🚀 Usage Guide

### For Users

#### Importing a Paper
1. Tap the "+" button or "Import Paper" option
2. Select "Upload PDF"
3. Choose a PDF file from your device
4. Fill in the paper details (title and authors are required)
5. Tap "Save Paper"
6. Wait for upload to complete
7. Paper appears in your library

#### Reading and Annotating
1. Open a paper from your library
2. Tap "Read PDF" button
3. Swipe to navigate pages
4. Tap the edit (pencil) FAB to show annotation tools
5. Select annotation type:
   - **Highlight**: Choose color, then tap and hold on text
   - **Note**: Enter note text, it appears on current page
   - **Underline**: Choose color, then tap and hold on text
6. Long press anywhere to view existing annotations
7. Reading progress is automatically saved

#### Managing Annotations
1. Long press on PDF to see annotations on current page
2. Tap an annotation to view details
3. Tap "Delete" to remove an annotation
4. Annotations sync across all your devices

---

## 🔧 Technical Notes

### PDF Rendering
- Uses `com.github.barteksc.pdfviewer.PDFView`
- Supports zoom, pan, and page navigation
- Renders annotations as overlays
- Hardware acceleration enabled

### Offline Support
- PDFs can be cached locally
- Annotations saved to Room database first
- Background sync when network available
- Conflicts resolved by timestamp

### Performance Optimizations
- Lazy loading of annotations by page
- PDF page caching
- Background thread operations
- Efficient bitmap rendering

### Security
- Firebase Storage rules restrict access to user's own files
- Firestore rules validate user ownership
- Secure authentication via Firebase Auth

---

## 🐛 Known Limitations

1. **Annotation Precision**: Current implementation uses simple coordinate-based positioning. Future versions could support text-based anchoring.

2. **Drawing Annotations**: TYPE_DRAWING is defined but not yet implemented in UI.

3. **Offline PDF Download**: PDFs from Firebase Storage require internet. Future versions could support offline caching.

4. **Collaboration**: Annotation sharing with collection members is database-ready but UI not yet implemented.

5. **Search in PDF**: Text search within PDF not yet implemented.

---

## 🔮 Future Enhancements

1. **Advanced Annotation Tools**
   - Free-hand drawing
   - Shape tools (rectangle, circle, arrow)
   - Text highlighting with selection
   - Voice notes

2. **Collaboration Features**
   - Real-time annotation updates
   - Comment threads on annotations
   - @mention other users
   - Annotation permissions

3. **PDF Enhancements**
   - Text-to-speech
   - Translation
   - Table of contents navigation
   - Bookmarks

4. **Export Options**
   - Export annotations as PDF comments
   - Generate summary of notes
   - Export to Markdown/Word

---

## 📞 Support

For issues or questions:
1. Check Firebase console for upload/sync errors
2. Review Logcat for detailed error messages
3. Verify network connectivity for cloud features
4. Ensure Firebase configuration is correct

---

## 📝 License

Part of LabVerse application. All rights reserved.

---

**Last Updated:** November 8, 2025
**Version:** 1.0.0
**Author:** AI Assistant

