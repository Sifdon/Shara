package org.seemsGood.shara.util;

import java.util.HashMap;
import java.util.Map;

public class TypeDataUtil {

    private static TypeDataUtil instance;
    private TypeDataUtil(){}
    public static TypeDataUtil getInstance(){
        if(instance == null) instance = new TypeDataUtil();
        return instance;
    }

    public Map<String, TypeData> types = new HashMap<>();

}
