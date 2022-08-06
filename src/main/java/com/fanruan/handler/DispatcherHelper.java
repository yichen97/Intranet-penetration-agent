package com.fanruan.handler;


import java.util.HashMap;
import java.util.Map;

public class DispatcherHelper {

    private static final Map<String, Class<?>> WRAPPER_CLASS_MAP = new HashMap<String, Class<?>>(){
        {
            put("Integer", Integer.TYPE);
            put("Short", Short.TYPE);
            put("Long", Long.TYPE);
            put("Double", Double.TYPE);
            put("Float", Float.TYPE);
            put("Byte", Byte.TYPE);
            put("Character", Character.TYPE);
            put("Boolean", Boolean.TYPE);

        }
    };

    public static boolean isWraps(Object o){
        return isWraps(o.getClass());
    }

    public static boolean isWraps(Class clz){
        return WRAPPER_CLASS_MAP.containsKey(getClassName(clz.getName()));
    }

    public static Class castToPrimitiveClass(Class clz){
        return WRAPPER_CLASS_MAP.get(getClassName(clz.getName()));
    }

    public static String getClassName(String fullyQualifiedClassName){
        String[] arr = fullyQualifiedClassName.split("\\.");
        int n = arr.length;
        if(n == 0) throw new RuntimeException("the class name invoked is wrong");
        return arr[n-1];
    }
}
