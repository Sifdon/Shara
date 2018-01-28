package org.seemsGood.shara.util;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseUtil {

    private static String TYPES = "types";
    private static String COMPANIES = "companies";

    private static FirebaseUtil instance;

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;

    private FirebaseUtil() {
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    public static FirebaseUtil getInstance() {
        if (instance == null) instance = new FirebaseUtil();

        return instance;
    }

    public FirebaseFirestore getFirestore() {
        return firestore;
    }

    public FirebaseStorage getStorage() {
        return storage;
    }

    public StorageReference getStorageReference(String ref) {
        return storage.getReference(ref);
    }
}
