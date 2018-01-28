package org.seemsGood.shara.model;

import org.seemsGood.shara.model.firebase.ShopMain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeDataTEST {

    public Map<String, String> categories = new HashMap<>();

    public List<String> keys = new ArrayList<>();
    public Map<String, String> names = new HashMap<>();
    public Map<String, ShopMain> shops = new HashMap<>();
    public Map<String, ShopMain> loaded = new HashMap<>();

    public TypeDataTEST() {
    }
}
