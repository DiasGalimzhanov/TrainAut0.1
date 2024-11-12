package com.example.trainaut01.repository;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Inject;

public class ParentRepository {
    private final FirebaseFirestore _db;
    private final CollectionReference _collection;

    @Inject
    public ParentRepository(FirebaseFirestore db) {
        this._db = db;
        this._collection = db.collection("Parent");
    }


}
