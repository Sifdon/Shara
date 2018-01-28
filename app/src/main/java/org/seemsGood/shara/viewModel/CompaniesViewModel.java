package org.seemsGood.shara.viewModel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.seemsGood.shara.liveData.CompaniesLiveData;
import org.seemsGood.shara.liveData.TypeLiveData;
import org.seemsGood.shara.util.FirebaseUtil;

import java.util.HashMap;
import java.util.Map;

public class CompaniesViewModel extends ViewModel {

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;

    private TypeLiveData type = new TypeLiveData();
    private CompaniesLiveData newCompanies = new CompaniesLiveData();

    private MutableLiveData<Map<String,String>> types = new MutableLiveData<>();

    public CompaniesViewModel() {

        firestore = FirebaseUtil.getInstance().getFirestore();
        storage = FirebaseUtil.getInstance().getStorage();

        firestore.collection("types")
                .get()
                .addOnSuccessListener(snapshots -> {

                    Map<String, String> typesMap = new HashMap<>();
                    for (DocumentSnapshot snapshot : snapshots)
                        typesMap.put(snapshot.getString("name"),snapshot.getId());

                    types.setValue(typesMap);
                })
                .addOnFailureListener(e -> Log.d("CompaniesViewModel", "CompaniesViewModel: "+e.getMessage()));
    }

    public TypeLiveData getOpenedType() {
        return type;
    }

    public CompaniesLiveData getNewCompanies() {
        return newCompanies;
    }

    public MutableLiveData<Map<String, String>> getTypes() {
        return types;
    }

    public void loadType(String name) {
        if (types.getValue() == null) return;
        Log.d("CompaniesViewModel", "loadType: " + types.getValue().get(name));
        type.loadTypeData(types.getValue().get(name), name);
    }

    public void loadNewCompanies() {
        if (type.getValue() == null) return;
        Log.d("CompaniesViewModel", "loadNewCompanies: " + type.getValue().getKey());
        newCompanies.loadNewCompanies(type.getValue().getKey());
    }

}
