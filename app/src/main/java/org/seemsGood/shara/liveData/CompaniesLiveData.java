package org.seemsGood.shara.liveData;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.seemsGood.shara.model.Company;
import org.seemsGood.shara.util.FirebaseUtil;
import org.seemsGood.shara.util.TypeDataUtil;

import java.util.ArrayList;
import java.util.List;

public class CompaniesLiveData extends LiveData<List<Company>> {

    private FirebaseFirestore firestore;
    private TypeDataUtil typeDataUtil;

    public CompaniesLiveData() {
        firestore = FirebaseUtil.getInstance().getFirestore();
        typeDataUtil = TypeDataUtil.getInstance();
    }

    public void loadNewCompanies(String key) {

        if (typeDataUtil.types.get(key).getCompanies().size() != 0) {
            setValue(typeDataUtil.types.get(key).getCompanies());
            return;
        }

        firestore.collection("types").document(key)
                .collection("companies")
                .get()
                .addOnSuccessListener(documentSnapshots -> {
                    List<Company> companies = new ArrayList<>();
                    for (DocumentSnapshot snapshot : documentSnapshots) {
                        Company company = snapshot.toObject(Company.class);
                        company.setKey(snapshot.getId());
                        company.setTypeKey(key);
                        companies.add(company);
                    }
                    typeDataUtil.types.get(key).getCompanies().addAll(companies);
                    setValue(companies);
                })
                .addOnFailureListener(e -> Log.d("CompaniesViewModel", "loadTypeData: " + e.getMessage()));
    }

}
