package com.example.labverse.database.relations;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;
import com.example.labverse.database.entities.CollectionEntity;
import com.example.labverse.database.entities.CollectionPaperCrossRef;
import com.example.labverse.database.entities.PaperEntity;
import java.util.List;


public class CollectionWithPapers {

    @Embedded
    public CollectionEntity collection;

    @Relation(
            parentColumn = "collection_id",
            entityColumn = "paper_id",
            associateBy = @Junction(CollectionPaperCrossRef.class)
    )
    public List<PaperEntity> papers;
}