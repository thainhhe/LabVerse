package com.example.labverse.database.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;

@Entity(tableName = "collection_paper_cross_ref",
        primaryKeys = {"collection_id", "paper_id"},
        foreignKeys = {
                @ForeignKey(entity = CollectionEntity.class,
                        parentColumns = "collection_id",
                        childColumns = "collection_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = PaperEntity.class,
                        parentColumns = "paper_id",
                        childColumns = "paper_id",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("collection_id"), @Index("paper_id")})
public class CollectionPaperCrossRef {

    @NonNull
    @ColumnInfo(name = "collection_id")
    private String collectionId;

    @NonNull
    @ColumnInfo(name = "paper_id")
    private String paperId;

    // Constructors
    public CollectionPaperCrossRef(@NonNull String collectionId, @NonNull String paperId) {
        this.collectionId = collectionId;
        this.paperId = paperId;
    }

    @NonNull
    public String getCollectionId() { return collectionId; }

    @NonNull
    public String getPaperId() { return paperId; }
}