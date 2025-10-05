package com.example.labverse.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.annotation.NonNull;

import com.example.labverse.database.dao.*;
import com.example.labverse.database.entities.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {
        UserEntity.class,
        PaperEntity.class,
        CollectionEntity.class,
        AnnotationEntity.class,
        CollectionMemberEntity.class,
        CollectionPaperEntity.class,
        TagEntity.class,
        PaperTagEntity.class,
        ReadingListEntity.class,
        ReadingListPaperEntity.class,
        CommentEntity.class,
        CitationEntity.class
}, version = 1, exportSchema = false)
public abstract class LabVerseDatabase extends RoomDatabase {

    // DAOs
    public abstract UserDao userDao();
    public abstract PaperDao paperDao();
    public abstract CollectionDao collectionDao();
    public abstract AnnotationDao annotationDao();
    public abstract CollectionMemberDao collectionMemberDao();
    public abstract CollectionPaperDao collectionPaperDao();
    public abstract TagDao tagDao();
    public abstract PaperTagDao paperTagDao();
    public abstract ReadingListDao readingListDao();
    public abstract ReadingListPaperDao readingListPaperDao();
    public abstract CommentDao commentDao();
    public abstract CitationDao citationDao();

    private static volatile LabVerseDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static LabVerseDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (LabVerseDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    LabVerseDatabase.class,
                                    "labverse_database"
                            )
                            .addCallback(roomCallback)
                            .fallbackToDestructiveMigration() // For development only
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // Populate database with initial data if needed
            databaseWriteExecutor.execute(() -> {
                // Add initial data here
            });
        }
    };

    // Migration strategies (for future versions)
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Example: Add new column
            // database.execSQL("ALTER TABLE papers ADD COLUMN new_column TEXT");
        }
    };
}
