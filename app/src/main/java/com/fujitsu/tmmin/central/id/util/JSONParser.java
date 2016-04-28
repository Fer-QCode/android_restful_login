package com.fujitsu.tmmin.central.id.util;

/**
 * Created by yusufya on 4/18/2016.
 */
public class JSONParser {
    private static JSONParser instance = null;
    public static JSONParser Instance()
    {
        JSONParser result = null;
        if (instance == null)
        {
            result = new JSONParser();
        }
        return result;
    }

    public <T> T ObjectMapper(Class<T> cls, String jsonMsg){
        T result = null;
        try {
            result = cls.newInstance();
            jsonMsg = jsonMsg.replaceAll("{","");
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

}
