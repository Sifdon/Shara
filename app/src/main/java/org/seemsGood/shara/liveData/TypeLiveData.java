package org.seemsGood.shara.liveData;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.seemsGood.shara.util.FirebaseUtil;
import org.seemsGood.shara.util.TypeData;
import org.seemsGood.shara.util.TypeDataUtil;

import java.util.Map;

public class TypeLiveData extends LiveData<TypeData> {

    private FirebaseFirestore firestore;
    private TypeDataUtil typeDataUtil;

    public TypeLiveData() {
        firestore = FirebaseUtil.getInstance().getFirestore();
        typeDataUtil = TypeDataUtil.getInstance();
    }

    public void loadTypeData(String key, String name) {

        if (typeDataUtil.types.containsKey(key)) {
            setValue(typeDataUtil.types.get(key));
            Log.d("TypeLiveData", "loadTypeData EXIST: " + key);
            return;
        }

        firestore.collection("types").document(key)
                .collection("data")
                .get()
                .addOnSuccessListener(snapshots -> {
                    TypeData typeData = new TypeData(key, name);
                    for (DocumentSnapshot snapshot : snapshots) {
                        switch (snapshot.getId()) {
                            case "names":
                                typeData.getNames().putAll((Map<String, String>) snapshot.get("names"));
                                Log.d("TypeLiveData", "loadTypeData: " + typeData.getNames());
                                break;
                            case "categories":
                                typeData.getCategories().putAll((Map<String, String>) snapshot.get("categories"));
                                Log.d("TypeLiveData", "loadTypeData: " + typeData.getCategories());
                                break;
                            case "kinds":
                                typeData.getKinds().putAll((Map<String, String>) snapshot.get("kinds"));
                                Log.d("TypeLiveData", "loadTypeData: " + typeData.getKinds());
                        }
                    }
                    typeDataUtil.types.put(key, typeData);
                    setValue(typeData);
                })
                .addOnFailureListener(e -> Log.d("TypeLiveData", "loadTypeData: " + e.getMessage()));
    }

}
