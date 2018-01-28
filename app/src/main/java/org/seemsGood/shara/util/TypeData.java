package org.seemsGood.shara.util;

import org.seemsGood.shara.model.Company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeData {

    private String key;
    private String name;

    private Map<String, String> names = new HashMap<>();
    private Map<String, String> kinds = new HashMap<>();
    private Map<String, String> categories = new HashMap<>();

    private List<Company> companies = new ArrayList<>();

    public TypeData(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getCategories() {
        return categories;
    }

    public Map<String, String> getKinds() {
        return kinds;
    }

    public Map<String, String> getNames() {
        return names;
    }

    public List<Company> getCompanies() {
        return companies;
    }

}
