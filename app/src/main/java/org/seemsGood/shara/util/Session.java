package org.seemsGood.shara.util;

import org.seemsGood.shara.model.TypeDataTEST;
import org.seemsGood.shara.model.firebase.Shop;

import java.util.HashMap;
import java.util.Map;

public class Session {

    public static Shop shop = null;
    public static String location = null;

    public static Map<String, String> types = new HashMap<>();
    public static Map<String, TypeDataTEST> typesData = new HashMap<>();

}
